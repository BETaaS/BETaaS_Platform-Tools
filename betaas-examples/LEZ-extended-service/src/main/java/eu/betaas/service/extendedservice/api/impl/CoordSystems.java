/*
Copyright 2014-2015 Intecs Spa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package eu.betaas.service.extendedservice.api.impl;

public class CoordSystems {
	
	public final static int WGS84 = 23;
	
	public static class UTMVertex {
		public double mUTMy;
		public double mUTMx;
		public UTMZoneId mZoneId;
		
		public UTMVertex(double UTMy, double UTMx, UTMZoneId zoneId) {
			mUTMy = UTMy;
			mUTMx = UTMx;
			mZoneId = new UTMZoneId(zoneId.mLetter, zoneId.mNumber);
		}
	}
	
	public static class WGSVertex {
		public double mWGSy;
		public double mWGSx;
		
		public WGSVertex(double WGSy, double WGSx) {
			mWGSy = WGSy;
			mWGSx = WGSx;
		}
	}
	
	public static class UTMZoneId {
		public String mLetter;
		public int mNumber;
		public UTMZoneId(String letter, int number) {
			mLetter = letter;
			mNumber = number;
		}
		public UTMZoneId() {
			mLetter = "";
			mNumber = 0;
		}
	}
	
	private final static double FOURTHPI = Math.PI / 4;
	private final static double deg2rad = Math.PI / 180;
	private final static double rad2deg = 180.0 / Math.PI;
	
	private class Ellipsoid {
	    
	    ///////////// PUBLIC SECTION /////////////
	    /** Constructor.
	     */
	    public Ellipsoid() {}

	    /** Constructor */
	    Ellipsoid(int Id, String name, double radius, double ecc) {
	        id = Id;
	        ellipsoidName = name; 
	        EquatorialRadius = radius; 
	        eccentricitySquared = ecc;
	    }

	    int id;
	    
	    String ellipsoidName;
	    
	    double EquatorialRadius; 
	    
	    double eccentricitySquared;
	    
	}

	private Ellipsoid ellipsoid[] = 
		{//  id, Ellipsoid name, Equatorial Radius, square of eccentricity	
		    new Ellipsoid( -1, "Placeholder", 0, 0),//placeholder only, To allow array indices to match id numbers
		    new Ellipsoid( 1, "Airy", 6377563, 0.00667054),
		    new Ellipsoid( 2, "Australian National", 6378160, 0.006694542),
		    new Ellipsoid( 3, "Bessel 1841", 6377397, 0.006674372),
		    new Ellipsoid( 4, "Bessel 1841 (Nambia) ", 6377484, 0.006674372),
		    new Ellipsoid( 5, "Clarke 1866", 6378206, 0.006768658),
		    new Ellipsoid( 6, "Clarke 1880", 6378249, 0.006803511),
		    new Ellipsoid( 7, "Everest", 6377276, 0.006637847),
		    new Ellipsoid( 8, "Fischer 1960 (Mercury) ", 6378166, 0.006693422),
		    new Ellipsoid( 9, "Fischer 1968", 6378150, 0.006693422),
		    new Ellipsoid( 10, "GRS 1967", 6378160, 0.006694605),
		    new Ellipsoid( 11, "GRS 1980", 6378137, 0.00669438),
		    new Ellipsoid( 12, "Helmert 1906", 6378200, 0.006693422),
		    new Ellipsoid( 13, "Hough", 6378270, 0.00672267),
		    new Ellipsoid( 14, "International", 6378388, 0.00672267),
		    new Ellipsoid( 15, "Krassovsky", 6378245, 0.006693422),
		    new Ellipsoid( 16, "Modified Airy", 6377340, 0.00667054),
		    new Ellipsoid( 17, "Modified Everest", 6377304, 0.006637847),
		    new Ellipsoid( 18, "Modified Fischer 1960", 6378155, 0.006693422),
		    new Ellipsoid( 19, "South American 1969", 6378160, 0.006694542),
		    new Ellipsoid( 20, "WGS 60", 6378165, 0.006693422),
		    new Ellipsoid( 21, "WGS 66", 6378145, 0.006694542),
		    new Ellipsoid( 22, "WGS-72", 6378135, 0.006694318),
		    new Ellipsoid( 23, "WGS-84", 6378137, 0.00669438)
		};
	

	public void LLtoUTM(int ReferenceEllipsoid, 
			                   double Lat, 
			                   double Long,
			                   UTMVertex utm) {
		double a = ellipsoid[ReferenceEllipsoid].EquatorialRadius;
	    double eccSquared = ellipsoid[ReferenceEllipsoid].eccentricitySquared;
	    double k0 = 0.9996;
	    
	    double LongOrigin;
	    double eccPrimeSquared;
	    double N, T, C, A, M;
	    
	    //Make sure the longitude is between -180.00 .. 179.9
	    double LongTemp = (Long+180)-(int)((Long+180)/360)*360-180; // -180.00 .. 179.9;
	    
	    double LatRad = Lat*deg2rad;
	    double LongRad = LongTemp*deg2rad;
	    double LongOriginRad;
	    int    ZoneNumber;
	    
	    ZoneNumber = (int)((LongTemp + 180)/6) + 1;
	    
	    if( Lat >= 56.0 && Lat < 64.0 && LongTemp >= 3.0 && LongTemp < 12.0 )
	        ZoneNumber = 32;
	    
	    // Special zones for Svalbard
	    if( Lat >= 72.0 && Lat < 84.0 ) {
	        if(      LongTemp >= 0.0  && LongTemp <  9.0 ) ZoneNumber = 31;
	        else if( LongTemp >= 9.0  && LongTemp < 21.0 ) ZoneNumber = 33;
	        else if( LongTemp >= 21.0 && LongTemp < 33.0 ) ZoneNumber = 35;
	        else if( LongTemp >= 33.0 && LongTemp < 42.0 ) ZoneNumber = 37;
	    }
	    LongOrigin = (ZoneNumber - 1)*6 - 180 + 3;  //+3 puts origin in middle of zone
	    LongOriginRad = LongOrigin * deg2rad;
	    
	    //compute the UTM Zone from the latitude and longitude
	    if (utm.mZoneId == null) utm.mZoneId = new UTMZoneId();
	    utm.mZoneId.mNumber = ZoneNumber;
	    utm.mZoneId.mLetter = UTMLetterDesignator(Lat);
	    
	    eccPrimeSquared = (eccSquared)/(1-eccSquared);
	    
	    N = a/Math.sqrt(1-eccSquared*Math.sin(LatRad)*Math.sin(LatRad));
	    T = Math.tan(LatRad)*Math.tan(LatRad);
	    C = eccPrimeSquared*Math.cos(LatRad)*Math.cos(LatRad);
	    A = Math.cos(LatRad)*(LongRad-LongOriginRad);
	    
	    M = a*((1	- eccSquared/4		- 3*eccSquared*eccSquared/64	- 5*eccSquared*eccSquared*eccSquared/256)*LatRad
	            - (3*eccSquared/8	+ 3*eccSquared*eccSquared/32	+ 45*eccSquared*eccSquared*eccSquared/1024)*Math.sin(2*LatRad)
	            + (15*eccSquared*eccSquared/256 + 45*eccSquared*eccSquared*eccSquared/1024)*Math.sin(4*LatRad)
	            - (35*eccSquared*eccSquared*eccSquared/3072)*Math.sin(6*LatRad));
	    
	    utm.mUTMx = (double)(k0*N*(A+(1-T+C)*A*A*A/6
	            + (5-18*T+T*T+72*C-58*eccPrimeSquared)*A*A*A*A*A/120)
	            + 500000.0);
	    
	    utm.mUTMy = (double)(k0*(M+N*Math.tan(LatRad)*(A*A/2+(5-T+9*C+4*C*C)*A*A*A*A/24
	            + (61-58*T+T*T+600*C-330*eccPrimeSquared)*A*A*A*A*A*A/720)));
	    if(Lat < 0)
	        utm.mUTMy += 10000000.0; //10000000 meter offset for southern hemisphere
	    
	}

	private String UTMLetterDesignator(double Lat)
	{
	    String LetterDesignator;
	    
	    if((84 >= Lat) && (Lat >= 72)) LetterDesignator = "X";
	    else if((72 > Lat) && (Lat >= 64)) LetterDesignator = "W";
	    else if((64 > Lat) && (Lat >= 56)) LetterDesignator = "V";
	    else if((56 > Lat) && (Lat >= 48)) LetterDesignator = "U";
	    else if((48 > Lat) && (Lat >= 40)) LetterDesignator = "T";
	    else if((40 > Lat) && (Lat >= 32)) LetterDesignator = "S";
	    else if((32 > Lat) && (Lat >= 24)) LetterDesignator = "R";
	    else if((24 > Lat) && (Lat >= 16)) LetterDesignator = "Q";
	    else if((16 > Lat) && (Lat >= 8)) LetterDesignator = "P";
	    else if(( 8 > Lat) && (Lat >= 0)) LetterDesignator = "N";
	    else if(( 0 > Lat) && (Lat >= -8)) LetterDesignator = "M";
	    else if((-8> Lat) && (Lat >= -16)) LetterDesignator = "L";
	    else if((-16 > Lat) && (Lat >= -24)) LetterDesignator = "K";
	    else if((-24 > Lat) && (Lat >= -32)) LetterDesignator = "J";
	    else if((-32 > Lat) && (Lat >= -40)) LetterDesignator = "H";
	    else if((-40 > Lat) && (Lat >= -48)) LetterDesignator = "G";
	    else if((-48 > Lat) && (Lat >= -56)) LetterDesignator = "F";
	    else if((-56 > Lat) && (Lat >= -64)) LetterDesignator = "E";
	    else if((-64 > Lat) && (Lat >= -72)) LetterDesignator = "D";
	    else if((-72 > Lat) && (Lat >= -80)) LetterDesignator = "C";
	    else LetterDesignator = "Z"; //This is here as an error flag to show that the Latitude is outside the UTM limits
	    
	    return LetterDesignator;
	}


	public void UTMtoLL(int ReferenceEllipsoid, 
			double UTMNorthing, 
			double UTMEasting,
			UTMZoneId zoneId,
			WGSVertex wgs ) {
	    
	    double k0 = 0.9996;
	    double a = ellipsoid[ReferenceEllipsoid].EquatorialRadius;
	    double eccSquared = ellipsoid[ReferenceEllipsoid].eccentricitySquared;
	    double eccPrimeSquared;
	    double e1 = (1-Math.sqrt(1-eccSquared))/(1+Math.sqrt(1-eccSquared));
	    double N1, T1, C1, R1, D, M;
	    double LongOrigin;
	    double mu, phi1, phi1Rad;
	    double x, y;
	    int NorthernHemisphere; //1 for northern hemispher, 0 for southern
	    
	    x = UTMEasting - 500000.0; //remove 500,000 meter offset for longitude
	    y = UTMNorthing;
	    
	    if((zoneId.mLetter.charAt(0) - 'N') >= 0)
	        NorthernHemisphere = 1;//point is in northern hemisphere
	    else {
	        NorthernHemisphere = 0;//point is in southern hemisphere
	        y -= 10000000.0;//remove 10,000,000 meter offset used for southern hemisphere
	    }
	    
	    LongOrigin = (zoneId.mNumber - 1)*6 - 180 + 3;  //+3 puts origin in middle of zone
	    
	    eccPrimeSquared = (eccSquared)/(1-eccSquared);
	    
	    M = y / k0;
	    mu = M/(a*(1-eccSquared/4-3*eccSquared*eccSquared/64-5*eccSquared*eccSquared*eccSquared/256));
	    
	    phi1Rad = mu	+ (3*e1/2-27*e1*e1*e1/32)*Math.sin(2*mu)
	            + (21*e1*e1/16-55*e1*e1*e1*e1/32)*Math.sin(4*mu)
	            +(151*e1*e1*e1/96)*Math.sin(6*mu);
	    phi1 = phi1Rad*rad2deg;
	    
	    N1 = a/Math.sqrt(1-eccSquared*Math.sin(phi1Rad)*Math.sin(phi1Rad));
	    T1 = Math.tan(phi1Rad)*Math.tan(phi1Rad);
	    C1 = eccPrimeSquared*Math.cos(phi1Rad)*Math.cos(phi1Rad);
	    R1 = a*(1-eccSquared)/Math.pow(1-eccSquared*Math.sin(phi1Rad)*Math.sin(phi1Rad), 1.5);
	    D = x/(N1*k0);
	    
	    wgs.mWGSy = phi1Rad - (N1*Math.tan(phi1Rad)/R1)*(D*D/2-(5+3*T1+10*C1-4*C1*C1-9*eccPrimeSquared)*D*D*D*D/24
	            +(61+90*T1+298*C1+45*T1*T1-252*eccPrimeSquared-3*C1*C1)*D*D*D*D*D*D/720);
	    wgs.mWGSy = wgs.mWGSy * rad2deg;
	    
	    wgs.mWGSx = (D-(1+2*T1+C1)*D*D*D/6+(5-2*C1+28*T1-3*C1*C1+8*eccPrimeSquared+24*T1*T1)
	            *D*D*D*D*D/120)/Math.cos(phi1Rad);
	    wgs.mWGSx = LongOrigin + wgs.mWGSx * rad2deg;
	    
	}

	
	public static void main(String args[]) {
		CoordSystems cs = new CoordSystems();
		UTMVertex utm = new UTMVertex(0, 0, new UTMZoneId());
		//UTMVertex utm = new UTMVertex(0, 0, new UTMZoneId("T", 32));
		WGSVertex wgs = new WGSVertex(0, 0);
		int useUTM;
		
		double lat = 43.655287;
		double lon = 10.439036;
		useUTM = 23;
		
		System.out.println("lon="+lon+" lat="+lat);
		
		cs.LLtoUTM(useUTM, lat, lon, utm);
		System.out.println("UTM: " + utm.mUTMx + " " + utm.mUTMy + " zone: " + utm.mZoneId.mNumber + utm.mZoneId.mLetter);
		
		utm.mUTMx += 100;
		utm.mUTMy += 100;
		
		UTMZoneId utmId = new UTMZoneId();
		cs.UTMtoLL(useUTM, utm.mUTMy, utm.mUTMx, utm.mZoneId, wgs);
		System.out.println("WGS84: " + wgs.mWGSx + " " + wgs.mWGSy);
	}
}
