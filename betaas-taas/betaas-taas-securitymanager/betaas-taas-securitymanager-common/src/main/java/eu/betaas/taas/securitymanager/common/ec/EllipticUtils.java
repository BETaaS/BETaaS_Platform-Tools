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
import java.security.spec.ECFieldFp;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

public class EllipticUtils {
	
	public static boolean verifyECPoint(EllipticCurve ec, ECPoint point){
		BigInteger a = ec.getA();
		BigInteger b = ec.getB();
		ECFieldFp field = (ECFieldFp) ec.getField();
		BigInteger p = field.getP();
		
		BigInteger x = point.getAffineX();
		BigInteger y = point.getAffineY();
		
		BigInteger right = x.pow(3).add(a.multiply(x)).add(b).mod(p);
		BigInteger left = y.pow(2).mod(p);
		
		if(left.compareTo(right)!=0)
			return false;
		
		return true;
	}
	/**
	 * A method to calculate addition of 2 ECPoints: p3 = p1+p2, with 
	 * the help of bouncy castle awesome library =)
	 * @param p1 ECPoint 1
	 * @param p2 ECPoint 2
	 * @return p1 + p2
	 */
	public static ECPoint pointAdditionPrime(EllipticCurve ec, ECPoint p1, ECPoint p2){
		// get the curve parameters
		ECFieldFp field = (ECFieldFp) ec.getField();
		BigInteger p = field.getP();
		BigInteger a = ec.getA();
		BigInteger b = ec.getB();
		
		org.bouncycastle.math.ec.ECCurve curve = 
				new org.bouncycastle.math.ec.ECCurve.Fp(p, a, b);
		
		org.bouncycastle.math.ec.ECPoint pb1 = 
				curve.createPoint(p1.getAffineX(), p1.getAffineY());
		
		org.bouncycastle.math.ec.ECPoint pb2 = 
				curve.createPoint(p2.getAffineX(), p2.getAffineY());
		
		org.bouncycastle.math.ec.ECPoint pb3 = pb1.add(pb2);
				
		ECPoint p3 = 
				new ECPoint(pb3.normalize().getXCoord().toBigInteger(), 
						pb3.normalize().getYCoord().toBigInteger());
		
		return p3;
	}
	
	/**
	 * A method to calculate the doubling of an ECPoint p, i.e. q = 2p, with the
	 * help of bouncy castle awesome library =)
	 * @param p1 the ECPoint to be doubled
	 * @param a the constant a from the elliptic curve
	 * @return 2p
	 */
	public static ECPoint pointDoublingPrime(EllipticCurve ec, ECPoint p1){
		// get the curve parameters
		ECFieldFp field = (ECFieldFp) ec.getField();
		BigInteger p = field.getP();
		BigInteger a = ec.getA();
		BigInteger b = ec.getB();
			
		org.bouncycastle.math.ec.ECCurve curve = 
				new org.bouncycastle.math.ec.ECCurve.Fp(p, a, b);
		
		org.bouncycastle.math.ec.ECPoint pb1 = 
				curve.createPoint(p1.getAffineX(), p1.getAffineY());
		
		org.bouncycastle.math.ec.ECPoint pb3 = pb1.twice();
		
		ECPoint p3 = 
				new ECPoint(pb3.normalize().getXCoord().toBigInteger(), 
						pb3.normalize().getYCoord().toBigInteger());
		
		return p3;
	}
	
	/**
	 * A method to get the negative of an ECPoint, i.e. p (xp,yp); -p(xp,-yp), 
	 * with the help of bouncy castle awesome library
	 * @param p1 an ECPoint
	 * @return -p
	 */
	public static ECPoint pointNegativePrime(EllipticCurve ec, ECPoint p1){
		// get the curve parameters
		ECFieldFp field = (ECFieldFp) ec.getField();
		BigInteger p = field.getP();
		BigInteger a = ec.getA();
		BigInteger b = ec.getB();
				
		org.bouncycastle.math.ec.ECCurve curve = 
				new org.bouncycastle.math.ec.ECCurve.Fp(p, a, b);
			
		org.bouncycastle.math.ec.ECPoint pb1 = 
				curve.createPoint(p1.getAffineX(), p1.getAffineY());
		
		org.bouncycastle.math.ec.ECPoint pb2 = pb1.negate();
		
		ECPoint p2 = 
				new ECPoint(p1.getAffineX(),pb2.normalize().getYCoord().toBigInteger());
		
		return p2;
	}
	
	/**
	 * A method to multiply an ECPoint with a scalar number
	 * @param p the ECPoint
	 * @param k the scalar number 
	 * @param a the constant a of the elliptic curve
	 * @return k*p
	 */
	public static ECPoint pointMultiplication(EllipticCurve ec, ECPoint p1, BigInteger k){
		// get the curve parameters
		ECFieldFp field = (ECFieldFp) ec.getField();
		BigInteger p = field.getP();
		BigInteger a = ec.getA();
		BigInteger b = ec.getB();
					
		org.bouncycastle.math.ec.ECCurve curve = 
				new org.bouncycastle.math.ec.ECCurve.Fp(p, a, b);
				
		org.bouncycastle.math.ec.ECPoint pb1 = 
				curve.createPoint(p1.getAffineX(), p1.getAffineY());
			
		org.bouncycastle.math.ec.ECPoint pb2 = pb1.multiply(k);
		
		ECPoint R = new ECPoint(pb2.normalize().getXCoord().toBigInteger(), 
				pb2.normalize().getYCoord().toBigInteger());
		
		return R;
	}
}
