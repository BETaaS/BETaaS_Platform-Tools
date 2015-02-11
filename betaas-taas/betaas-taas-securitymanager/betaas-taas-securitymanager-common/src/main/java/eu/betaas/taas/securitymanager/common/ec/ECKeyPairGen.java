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

package eu.betaas.taas.securitymanager.common.ec;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9Curve;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;
public class ECKeyPairGen {
	/**
	 * Generate a random EC (Elliptic Curve) random 192-bit key pair (equivalent 
	 * to 1536-bit RSA) based on NIST and SECG, using Bc (Bouncy Castle) classes
	 * @return a pair of EC keys (AsymmetricCipherKeyPair type)
	 */
	public static AsymmetricCipherKeyPair generateECKeyPair192(){
		AsymmetricCipherKeyPairGenerator kpGen = new ECKeyPairGenerator();
		
		// First, define an EC curve
		// ECCurve.Fp(p, a, b); p = prime; a,b = constants defined in equation E: y^2=x^3+ax+b (mod p)
		ECCurve curve = new ECCurve.Fp
				(new BigInteger(ECParams.P_192_R1, 16),			// p 
				new BigInteger(ECParams.A_192_R1,16), 			// a
				new BigInteger(ECParams.B_192_R1,16));			// b
		
		byte[] seed = Hex.decode(ECParams.SEED_192_R1);
		
		// finally use the seed in the ECKeyGenerationParameters along with the others
		// ECKeyGenerationParameters(ECDomainParameters(ECCurve, G, n, h),random)
		kpGen.init(new ECKeyGenerationParameters(new ECDomainParameters(curve, 
				curve.decodePoint(Hex.decode(ECParams.G_192_R1_NCOMP)),		// G		 
				new BigInteger(ECParams.N_192_R1,16), 										// n
				new BigInteger(ECParams.H_192_R1,16),											// h 
				seed), 																										// seed
				new SecureRandom()));
		
		return kpGen.generateKeyPair();
	}
	
	/**
	 * Generate a random EC (Elliptic Curve) random 224-bit key pair (equivalent 
	 * to 2048-bit RSA) based on NIST and SECG, using Bc (Bouncy Castle) classes
	 * @return a pair of EC keys (AsymmetricCipherKeyPair type)
	 */
	public static AsymmetricCipherKeyPair generateECKeyPair224(){
		AsymmetricCipherKeyPairGenerator kpGen = new ECKeyPairGenerator();
		
		// ECCurve.Fp(p, a, b); p = prime; a,b = constants defined in equation E: y^2=x^3+ax+b (mod p)
		ECCurve curve = new ECCurve.Fp(new BigInteger(ECParams.P_224_R1, 16), 
				new BigInteger(ECParams.A_224_R1,16), 
				new BigInteger(ECParams.B_224_R1,16));
				
		byte[] seed = Hex.decode(ECParams.SEED_224_R1);
		
		// finally use the seed in the ECKeyGenerationParameters along with the others
		// ECKeyGenerationParameters(ECDomainParameters(ECCurve, G, n, h),random)
		kpGen.init(new ECKeyGenerationParameters(new ECDomainParameters(curve,
				curve.decodePoint(Hex.decode(ECParams.G_224_R1_NCOMP)), 
				new BigInteger(ECParams.N_224_R1,16), 
				new BigInteger(ECParams.H_224_R1,16), seed), new SecureRandom()));
		
		return kpGen.generateKeyPair();
	}
	
	/**
	 * Generate a random EC (Elliptic Curve) random 192-bit key pair (equivalent 
	 * to 1536-bit RSA) based on NIST and SECG, using JCA classes
	 * @return
	 * @throws Exception
	 */
	public static KeyPair generateECKeyPair192Jca() throws Exception{
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("EC", "BC");
    
    byte[] seed = Hex.decode(ECParams.SEED_192_R1);
		
    EllipticCurve curve = new EllipticCurve(new ECFieldFp(
    		new BigInteger(ECParams.P_192_R1,16)),			// p
    		new BigInteger(ECParams.A_192_R1,16),				// a
    		new BigInteger(ECParams.B_192_R1,16),				// b
    		seed);																			// seed
    
    // other alternative to purely use JDK 
    java.security.spec.ECPoint g = new java.security.spec.ECPoint
    		(new BigInteger(ECParams.GX_192_R1,16),
    		new BigInteger(ECParams.GY_192_R1,16));
    
    ECParameterSpec ecSpec = new ECParameterSpec(
    		curve,
    		g,																		// G
    		new BigInteger(ECParams.N_192_R1,16),	// n									
    		1);																		// h
    
    kpGen.initialize(ecSpec, new SecureRandom());
    
    return kpGen.generateKeyPair();
	}
	
	/**
	 * Generate a random EC (Elliptic Curve) random 224-bit key pair (equivalent 
	 * to 2048-bit RSA) based on NIST and SECG, using JCA classes
	 * @return
	 * @throws Exception
	 */
	public static KeyPair generateECKeyPair224Jca() throws Exception{
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("EC", "BC");
    
		byte[] seed = Hex.decode(ECParams.SEED_224_R1);
		
    EllipticCurve curve = new EllipticCurve(new ECFieldFp(
    		new BigInteger(ECParams.P_224_R1,16)),			// p
    		new BigInteger(ECParams.A_224_R1,16),				// a
    		new BigInteger(ECParams.B_224_R1,16),				// b
    		seed);																			// seed
    
    // other alternative to purely use JDK 
    java.security.spec.ECPoint g = new java.security.spec.ECPoint
    		(new BigInteger(ECParams.GX_224_R1,16),
    		new BigInteger(ECParams.GY_224_R1,16));
    
    ECParameterSpec ecSpec = new ECParameterSpec(
    		curve,
    		g,																		// G
    		new BigInteger(ECParams.N_224_R1,16),	// n									
    		1);																		// h
    
    kpGen.initialize(ecSpec, new SecureRandom());
    
    return kpGen.generateKeyPair();
	}
	
	/**
	 * Generate random 192-bit EC Public Key given the Q/W parameters of EC Public
	 * Key, i.e. the X and Y coordinate  
	 * @param Wx: X coordinate of Q or W point representing the EC public key
	 * @param Wy: Y coordinate of Q or W point representing the EC public key
	 * @return
	 * @throws Exception
	 */
	public static ECPublicKey generateECPublicKey192Jca(BigInteger Wx, BigInteger Wy) 
			throws Exception{
		ECPoint w = new ECPoint(Wx, Wy);
		
		// Do some work around with the seed from hex to byte[]
			BigInteger seedBi = new BigInteger(ECParams.SEED_192_R1, 16);
			byte[] seed = seedBi.toByteArray();
			// to check whether we have extra 0 at MSB or not
			if (seed[0] == 0) {
				byte[] tmp = new byte[seed.length - 1];
				System.arraycopy(seed, 1, tmp, 0, tmp.length);
				seed = tmp;
			}
			
			EllipticCurve curve = new EllipticCurve(new ECFieldFp(
	    		new BigInteger(ECParams.P_192_R1,16)),			// p
	    		new BigInteger(ECParams.A_192_R1,16),				// a
	    		new BigInteger(ECParams.B_192_R1,16),				// b
	    		seed);																			// seed
	    
	    // other alternative to purely use JDK 
	    java.security.spec.ECPoint g = new java.security.spec.ECPoint
	    		(new BigInteger(ECParams.GX_192_R1,16),
	    		new BigInteger(ECParams.GY_192_R1,16));
	    
	    ECPublicKeySpec ecParSpec = new ECPublicKeySpec(w ,new ECParameterSpec(
	    		curve, g, new BigInteger(ECParams.N_192_R1,16), 1));
	    
	    KeyFactory kf = KeyFactory.getInstance("EC");
	    
	    return (ECPublicKey) kf.generatePublic(ecParSpec);
	}
	
	/**
	 * Generate random 192-bit EC Public Key given the Q/W parameters of EC Public
	 * Key, i.e. the X and Y coordinate  
	 * @param Wx: X coordinate of Q or W point representing the EC public key
	 * @param Wy: Y coordinate of Q or W point representing the EC public key
	 * @return
	 * @throws Exception
	 */
	public static ECPublicKeyParameters generateECPublicKey192(BigInteger Wx, BigInteger Wy) 
			throws Exception{
	// First, define an EC curve
		// ECCurve.Fp(p, a, b); p = prime; a,b = constants defined in equation E: y^2=x^3+ax+b (mod p)
		ECCurve curve = new ECCurve.Fp
				(new BigInteger(ECParams.P_192_R1, 16),			// p 
				new BigInteger(ECParams.A_192_R1,16), 			// a
				new BigInteger(ECParams.B_192_R1,16));			// b
		
		byte[] seed = Hex.decode(ECParams.SEED_192_R1);
		
		org.bouncycastle.math.ec.ECPoint gPoint = curve.createPoint(Wx, Wy);
		
		return new ECPublicKeyParameters(gPoint, new ECDomainParameters(curve, 
				curve.decodePoint(Hex.decode(ECParams.G_192_R1_NCOMP)),		// G		 
				new BigInteger(ECParams.N_192_R1,16), 										// n
				new BigInteger(ECParams.H_192_R1,16),											// h 
				seed));
	}
	
	/**
	 * A method to reconstruct an ECPublicKey from a SubjectPublicKeyInfo of a 
	 * certificate 
	 * @param info: SubjectPublicKeyInfo in a X509Certificate
	 * @return: ECPublicKeyParameters
	 */
	public static ECPublicKeyParameters generateECPublicKey(SubjectPublicKeyInfo info){
		X962Parameters as = (X962Parameters) info.getAlgorithm().getParameters();
		DERSequence aa = (DERSequence) as.getParameters();
		Enumeration en = aa.getObjects();
		ECCurve curve = null;
		org.bouncycastle.math.ec.ECPoint g = null;
		byte[] seed = null;
		BigInteger h = null;
		BigInteger n = null;
		while(en.hasMoreElements()){
			Object oen = en.nextElement();
			if(oen instanceof X9Curve){
				curve = ((X9Curve) oen).getCurve();
				seed = ((X9Curve) oen).getSeed();
			}
			else if(oen instanceof X9ECPoint){
				g = ((X9ECPoint) oen).getPoint();
			}
			else if(oen instanceof ASN1Integer){
				BigInteger xoen = ((ASN1Integer) oen).getValue();
				if(xoen.equals(BigInteger.ONE))
					h = xoen;
				else
					n = xoen;
			}
		}
		
		ASN1OctetString key = new DEROctetString(info.getPublicKeyData().getBytes());
		X9ECPoint derQ = new X9ECPoint(curve, key);
		
		ECDomainParameters dParams = new ECDomainParameters(curve, 
				g, n, h, seed);
		
		return new ECPublicKeyParameters(derQ.getPoint(), dParams);
	}
}
