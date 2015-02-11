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

public class ECParams {

	/**
	 * prime modulus p of the 192-bit random elliptic curve domain parameter  
	 */
	public static final String P_192_R1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFF";
	/**
	 * the constant a of the curve E:y^2=x^3+ax+b over Fp (192-bit)
	 */
	public static final String A_192_R1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFC";
	/**
	 * the constant b of the curve E:y^2=x^3+ax+b over Fp (192-bit)
	 */
	public static final String B_192_R1 = "64210519E59C80E70FA7E9AB72243049FEB8DEECC146B9B1";
	/**
	 * the base point G in compressed form (192-bit)
	 */
	public static final String G_192_R1_COMP = "03188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF1012";
	/**
	 * the base point G in uncompressed form
	 */
	public static final String G_192_R1_NCOMP = "04188DA80EB03090F67CBF20EB43A18800F4FF0AFD82FF101207192B95FFC8DA78631011ED6B24CDD573F977A11E794811";
	/**
	 * the base point x coordinate Gx of the curve E:y^2=x^3+ax+b over Fp (192-bit)
	 */
	public static final String GX_192_R1 = "188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012";
	/**
	 * the base point y coordinate Gy of the curve E:y^2=x^3+ax+b over Fp (192-bit)
	 */
	public static final String GY_192_R1 = "07192b95ffc8da78631011ed6b24cdd573f977a11e794811";
	/**
	 * seed in which E is chosen at random (192-bit)
	 */
	public static final String SEED_192_R1 = "3045AE6FC8422F64ED579528D38120EAE12196D5";
	/**
	 * the order n of G (192-bit)
	 */
	public static final String N_192_R1 = "FFFFFFFFFFFFFFFFFFFFFFFF99DEF836146BC9B1B4D22831";
	/**
	 * the cofactor h (192-bit)
	 */
	public static final String H_192_R1 = "01";
	
	/**
	 * prime modulus p of the 224-bit random elliptic curve domain parameter  
	 */
	public static final String P_224_R1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001";
	/**
	 * the constant a of the curve E:y^2=x^3+ax+b over Fp (224-bit)
	 */
	public static final String A_224_R1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE";
	/**
	 * the constant b of the curve E:y^2=x^3+ax+b over Fp (224-bit)
	 */
	public static final String B_224_R1 = "B4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4";
	/**
	 * the base point x coordinate Gx of the curve E:y^2=x^3+ax+b over Fp (224-bit)
	 */
	public static final String GX_224_R1 = "b70e0cbd6bb4bf7f321390b94a03c1d356c21122343280d6115c1d21";
	/**
	 * the base point y coordinate Gy of the curve E:y^2=x^3+ax+b over Fp (224-bit)
	 */
	public static final String GY_224_R1 = "bd376388b5f723fb4c22dfe6cd4375a05a07476444d5819985007e34";
	/**
	 * the base point G in compressed form (224-bit)
	 */
	public static final String G_224_R1_COMP = "02B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21";
	/**
	 * the base point G in uncompressed form (224-bit)
	 */
	public static final String G_224_R1_NCOMP = "04B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34";
	/**
	 * seed in which E is chosen at random (224-bit)
	 */
	public static final String SEED_224_R1 = "BD71344799D5C7FCDC45B59FA3B9AB8F6A948BC5";
	/**
	 * the order n of G (224-bit)
	 */
	public static final String N_224_R1 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D";
	/**
	 * the cofactor h (224-bit)
	 */
	public static final String H_224_R1 = "01";
}
