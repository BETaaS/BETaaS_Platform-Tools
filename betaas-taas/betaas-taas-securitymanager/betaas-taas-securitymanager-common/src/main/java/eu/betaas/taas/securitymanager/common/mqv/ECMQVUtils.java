/**
Copyright 2014-2015 Center for TeleInFrastruktur (CTIF), Aalborg University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author Bayu Anggorojati [ba@es.aau.dk]
Center for TeleInFrastruktur, Aalborg University, Denmark
 */

package eu.betaas.taas.securitymanager.common.mqv;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECCurve.Fp;

import eu.betaas.taas.securitymanager.common.ec.EllipticUtils;;

public class ECMQVUtils {
	
	/**
	 * A method to perform embedded public key validation (BC style)
	 * @param pub: the public key to be validated
	 * @return true if valid
	 */
	public static boolean validateEmbedPubKey(AsymmetricKeyParameter pub){
		
		ECPublicKeyParameters ecPub = (ECPublicKeyParameters) pub;
		ECCurve.Fp fpCurve = (Fp) ecPub.getParameters().getCurve();
		BigInteger p = fpCurve.getQ();
		BigInteger xq = ecPub.getQ().normalize().getXCoord().toBigInteger();
		BigInteger yq = ecPub.getQ().normalize().getYCoord().toBigInteger();
		
		// validate the xq and yq, they must be in the interval [0,q-1]
		if(xq.compareTo(BigInteger.ZERO) == -1 && 
				xq.compareTo(p.subtract(BigInteger.ONE)) == 1 && 
				yq.compareTo(BigInteger.ZERO) == -1 && 
				yq.compareTo(p.subtract(BigInteger.ONE))==1)
			return false;
		
		BigInteger a = fpCurve.getA().toBigInteger();
		BigInteger b = fpCurve.getB().toBigInteger();
		
		// test whether Q lies in the curve defined by y^2(mod p)=x^3+ax+b (mod p)
		BigInteger rightEq = xq.pow(3).add(a.multiply(xq)).add(b).mod(p);
//		System.out.println("x^3+ax+b 	= "+rightEq.toString());
		
		BigInteger y2 = yq.pow(2).mod(p);
//		System.out.println("y^2 		= "+y2.toString());
		
		if(y2.compareTo(rightEq) != 0)
			return false;
		
		return true;
	}
	
	/**
	 * A method to compute the implicit signature, s (BC style), defined as: 
	 * s = (ephPriv +	ephPub_*statPriv)mod(n)
	 * @param ephPub: the ephemeral public key
	 * @param ephPriv: the ephemeral private key
	 * @param statPriv: the static private key
	 * @return implicit signature
	 */
	public static BigInteger computeImplicitSig(ECPublicKeyParameters ephPub, 
			ECPrivateKeyParameters ephPriv, ECPrivateKeyParameters statPriv){
		
		BigInteger n = ephPub.getParameters().getN();
		BigInteger r_ = ephPriv.getD();
		BigInteger w_ = statPriv.getD();
		
		BigInteger Rx = ephPub.getQ().normalize().getXCoord().toBigInteger();
		
		BigInteger Rx_ = calculateRx_(Rx, n);
		
		BigInteger implS = r_.add(Rx_.multiply(w_)).mod(n);
		
		return implS;
	}
	
	/**
	 * A method to perform embedded public key validation (JCA style)
	 * @param pub: the public key to be validated
	 * @return true if valid
	 */
	public static boolean validateEmbedPubKey(PublicKey pub){
		
		ECPublicKey ecPub = (ECPublicKey) pub;
		ECFieldFp field = (ECFieldFp) ecPub.getParams().getCurve().getField();
		BigInteger p = field.getP();
		BigInteger xq = ecPub.getW().getAffineX();
		BigInteger yq = ecPub.getW().getAffineY();
		
		// validate the xq and yq, they must be in the interval [0,q-1]
		if(xq.compareTo(BigInteger.ZERO) == -1 && 
				xq.compareTo(p.subtract(BigInteger.ONE)) == 1 && 
				yq.compareTo(BigInteger.ZERO) == -1 && 
				yq.compareTo(p.subtract(BigInteger.ONE))==1)
			return false;
		
		BigInteger a = ecPub.getParams().getCurve().getA();
		BigInteger b = ecPub.getParams().getCurve().getB();
		
		// test whether Q lies in the curve defined by y^2(mod p)=x^3+ax+b (mod p)
		BigInteger rightEq = xq.pow(3).add(a.multiply(xq)).add(b).mod(p);
//		System.out.println("x^3+ax+b 	= "+rightEq.toString());
		
		BigInteger y2 = yq.pow(2).mod(p);
//		System.out.println("y^2 		= "+y2.toString());
		
		if(y2.compareTo(rightEq) != 0)
			return false;
		
		return true;
	}
	
	/**
	 * A method to compute the implicit signature, s (JCA style), defined as: 
	 * s = (ephPriv +	ephPub_*statPriv)mod(n)
	 * @param ephPub: the ephemeral public key
	 * @param ephPriv: the ephemeral private key
	 * @param statPriv: the static private key
	 * @return implicit signature
	 */
	public static BigInteger computeImplicitSig(ECPublicKey ephPub, 
			ECPrivateKey ephPriv, ECPrivateKey statPriv){
		
		BigInteger n = ephPub.getParams().getOrder();
		BigInteger r_ = ephPriv.getS();
		BigInteger w_ = statPriv.getS();
		
		BigInteger Rx = ephPub.getW().getAffineX();
		
		BigInteger Rx_ = calculateRx_(Rx, n);
		
		BigInteger implS = r_.add(Rx_.multiply(w_)).mod(n);
		
		return implS;
	}
	
	/**
	 * A method to calculate the associate value of an ECPoint R, i.e. Rx_
	 * @param Rx: the x-coordinate of ECPoint R
	 * @param n: the order of EC base point
	 * @return the associate value of ECPoint R, Rx_
	 */
	public static BigInteger calculateRx_(BigInteger Rx, BigInteger n){
		// work around to calculate log of BigInteger value
		String nStr = n.toString();
		int nLen = nStr.length();
			
		nStr = "0."+nStr;
		double nDoubKoma = Double.parseDouble(nStr);
		double fDouble = ((double)nLen + Math.log10(nDoubKoma))/Math.log10(2.0);
		// ceil of fDouble/2
		fDouble = Math.ceil(fDouble/2);
			
		BigInteger coef2 = BigInteger.valueOf(2).pow((int) fDouble);
		BigInteger Rx_ = Rx.mod(coef2).add(coef2);
			
		return Rx_;
	}
	
	/**
	 * A method to calculate the shared key (BC Style), 
	 * K = h*implicitSignature(ephPub + Rx_*statPub)
	 * @param ephPub: the ephemeral public key 
	 * @param statPub: the static/long term public key 
	 * @param h: the EC cofactor
	 * @param implSig: the implicit signature 
	 * @return the shared secret key, K
	 */
	public static org.bouncycastle.math.ec.ECPoint calculateSharedKey(
		ECPublicKeyParameters ephPub, ECPublicKeyParameters statPub, 
		BigInteger h, BigInteger implSig){
		
		// ECPoint of the ephemeral public key 
		org.bouncycastle.math.ec.ECPoint wEph = ephPub.getQ();
		// ECPoint of the static public key
		org.bouncycastle.math.ec.ECPoint wStat = statPub.getQ();
			
		// K = h*implSig*(ephPub +ephPub_*statPub)
		// to do this, we need to calculate scalar multiplication of an ECPoint
		BigInteger n = statPub.getParameters().getN();
		// calculate the Rx_ (ephPub_)
		BigInteger Rx_ = calculateRx_(wEph.normalize().getXCoord().toBigInteger(), n);
			
		// calculate the ephPub_*statPub
		org.bouncycastle.math.ec.ECPoint R = wStat.multiply(Rx_);
		//calculate the ephPub + ephPub_*statPub (ephPub + R)
		R = R.add(wEph);
		//calculate the h * implicit signature
		BigInteger hImplSig = implSig.multiply(h);
		// finally, calculate the shared key K = h*implSig*(ephPub +ephPub_*statPub)
		org.bouncycastle.math.ec.ECPoint K = R.multiply(hImplSig);
			
		return K;
	} 
	
	/**
	 * A method to calculate the shared key (JCA style), 
	 * K = h*implicitSignature(ephPub + Rx_*statPub)
	 * @param ephPub: the ephemeral public key 
	 * @param statPub: the static/long term public key 
	 * @param h: the EC cofactor
	 * @param implSig: the implicit signature 
	 * @return the shared secret key, K
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public static ECPoint calculateSharedKey(ECPublicKey ephPub, 
			ECPublicKey statPub, BigInteger h, BigInteger implSig) 
					throws NoSuchAlgorithmException, NoSuchProviderException{
		
		// ECPoint of the ephemeral public key 
		ECPoint wEph = ephPub.getW();
		// ECPoint of the static public key
		ECPoint wStat = statPub.getW();
		// get the curve
		EllipticCurve ec = statPub.getParams().getCurve();
		
		// K = h*implSig*(ephPub +ephPub_*statPub)
		// to do this, we need to calculate scalar multiplication of an ECPoint
		BigInteger n = statPub.getParams().getOrder();
		// calculate the Rx_ (ephPub_)
		BigInteger Rx_ = calculateRx_(wEph.getAffineX(), n);
		
		// calculate the ephPub_*statPub
		ECPoint R = EllipticUtils.pointMultiplication(ec, wStat, Rx_);
		//calculate the ephPub + ephPub_*statPub (ephPub + R)
		R = EllipticUtils.pointAdditionPrime(ec, wEph, R);
		//calculate the h * implicit signature
		BigInteger hImplSig = implSig.multiply(h);
		// finally, calculate the shared key K = h*implSig*(ephPub +ephPub_*statPub)
		ECPoint K = EllipticUtils.pointMultiplication(ec, R, hImplSig);
		
		return K;
	}
	
	/**
	 * A method to derive a new key using HKDF (Hash based Key Derivation Function) 
	 * @param keyIn: an "original" key to be derived
	 * @param keyOutLenByte: the length of the new derived (output) key in byte
	 * @return 
	 */
	public static byte[] deriveKeyHKDF(byte[] keyIn, int keyOutLenByte){
		DerivationParameters kdfParam = 
				new HKDFParameters(keyIn, null, intToByteArray(keyOutLenByte*8));
				
		HKDFBytesGenerator hkdfGen = new HKDFBytesGenerator(new SHA1Digest());		
		hkdfGen.init(kdfParam);
		// initialize the new key with size of L bits (or L/8 bytes)
		byte[] newKey = new byte[keyOutLenByte];
		
		hkdfGen.generateBytes(newKey, 0, keyOutLenByte);
		
		return newKey;
	}
	
	/**
	 * A method to calculate the MAC to be sent to other GW in the ECMQV process
	 * @param num: an integer that represents the step, e.g. either 2 or 3
	 * @param ufnA: User Friendly Name of GW A
	 * @param ufnB: User Friendly Name of GW B
	 * @param ephPubA: Ephemeral public key of GW A
	 * @param ephPubB: Ephemeral public key of GW A
	 * @param k1: key to encrypt the MAC (derived from KDF)
	 * @return
	 */
	public static byte[] computeMAC(String num, String ufnA, String ufnB, 
			byte[] ephPubA, byte[] ephPubB, byte[] k1){
		
		HMac hmac = new HMac(new SHA1Digest());
		hmac.init(new KeyParameter(k1));
		
		// concatenate the message/info (in bytes)
		byte[] numByte = num.getBytes();
		byte[] ufnAbyte = ufnA.getBytes();
		byte[] ufnBbyte = ufnB.getBytes();
		int byteLen = numByte.length + ufnAbyte.length + ufnBbyte.length + 
				ephPubA.length + ephPubB.length;
		byte[] in = new byte[byteLen];
		
		int c = 0;
		for(int i =0;i<numByte.length;i++){
			in[c] = numByte[i];
			c++;
		}
		for(int i=0;i<ufnAbyte.length;i++){
			in[c] = ufnAbyte[i];
			c++;
		}
		for(int i=0;i<ufnBbyte.length;i++){
			in[c] = ufnBbyte[i];
			c++;
		}
		for(int i=0;i<ephPubA.length;i++){
			in[c] = ephPubA[i];
			c++;
		}
		for(int i=0;i<ephPubB.length;i++){
			in[c] = ephPubB[i];
			c++;
		}
		
		hmac.update(in, 0, byteLen);
		
		byte[] out = new byte[hmac.getMacSize()];
		hmac.doFinal(out, 0);
		
		return out;
	}
	
	/**
	 * Helper function to convert integer into byte array
	 * @param the integer value
	 * @return byte array representation of the integer value
	 */
	public static final byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
        (byte)(value >>> 16),
        (byte)(value >>> 8),
        (byte)value
    };
	}
}
