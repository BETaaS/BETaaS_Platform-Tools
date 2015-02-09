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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.betaas.service.extendedservice.api.impl.CoordSystems.UTMVertex;
import eu.betaas.service.extendedservice.api.impl.CoordSystems.UTMZoneId;

// Definition of the roads map
public class Map {
	
	private final static float MAX_DISTANCE_THRESHOLD_METERS = 15.0f;
	
	public Map() {
		mSegments = new Vector<Segment>();
	}

	// load the definition from the file
	public void load(String filePath) throws Exception {
		BufferedReader br = null;
		float lat, lon;
		
		if (filePath == null) throw new Exception("Null input file");
		
		try {
			br = new BufferedReader(new FileReader(filePath));

			// prepare for coordinates conversions
			CoordSystems cs = new CoordSystems();
			UTMVertex utm = new UTMVertex(0, 0, new UTMZoneId());
			int useUTM = 23;
			
			String line;
			String values[];
			int nLine = 0;
			while ((line = br.readLine()) != null) {
				nLine++;
				
				if ((line.isEmpty()) || (line.startsWith("#"))) continue;
				
				Segment segment = new Segment();
				
				values = line.split(";");
				if ((values == null) || (values.length != 5)) {
					throw new Exception("Unexpected line (" + nLine + ") in map file");
				}
				
				segment.id = values[0];
				if ((segment.id == null) || (segment.id.isEmpty())) {
					throw new Exception("Invalid ID at line " + nLine);
				}
				
				try {
					// read and convert to UTM
					lon = Float.parseFloat(values[1]);
					lat = Float.parseFloat(values[2]);
					cs.LLtoUTM(useUTM, lat, lon, utm);
					segment.startUTMy = utm.mUTMy;
					segment.startUTMx = utm.mUTMx;

					lon = Float.parseFloat(values[3]);
					lat = Float.parseFloat(values[4]);
					cs.LLtoUTM(useUTM, lat, lon, utm);					
					segment.endUTMy = utm.mUTMy;
					segment.endUTMx = utm.mUTMx;
				} catch (NumberFormatException e) {
					throw new Exception("Invalid coordinate at line " + nLine);
				}
				
				segment.trafficIntensity = 0.0f;
				
				mSegments.add(segment);
				
			}
		} catch (IOException e) {
			throw(e);
		} finally {
			if (br != null) br.close();
		}
	}
	
	
	/**
	 * Retrieve the traffic intensity of the map segment that passes through the
	 * specified position, within the predefined margin
	 * @param lat
	 * @param lon
	 * @return the car/hour value or -1 if no segment passes near the specified position
	 */
	public float getTrafficIntensity(double lat, double lon) {
		int i;
		Segment curr;
		float dist, minDistance;
		
		int closestIdx = -1;
		minDistance = 1000000.0f;
		for (i=0; i < mSegments.size(); i++) {
			// compute the distance from the current segment
			curr = mSegments.get(i);
			dist = (float)distance(curr, lat, lon);
			if ((dist <= MAX_DISTANCE_THRESHOLD_METERS) && 
		        (dist < minDistance)) {
				minDistance = dist;
				closestIdx = i;
			}
		}
		
		if (closestIdx == -1) return -1.0f;
		
		mLogger.info("Getting traffic intensity from segment " + mSegments.get(closestIdx).id + ". closestIdx=" + closestIdx);
		
		return mSegments.get(closestIdx).trafficIntensity;
	}
	
	
	/**
	 * Updates all the segments' intensity if the are very close to the given location
	 * @param lat
	 * @param lon
	 * @param carsMinute
	 */
	public void updateIntensity(double lat, double lon, float carsMinute) {
		Segment curr;
		float dist;
		
		for (int i=0; i < mSegments.size(); i++) {
			// compute the distance from the current segment
			curr = mSegments.get(i);
			dist = (float)distance(curr, lat, lon);
			if (dist <= MAX_DISTANCE_THRESHOLD_METERS) {
				mLogger.info("Updating segment " + curr.id + " with intensity " + carsMinute + ". idx=" + i);
				curr.trafficIntensity = carsMinute;
			}
		}
	}
	
	
	/**
	 * Computes the minimum distance of a given point from a map segment
	 * @param segment
	 * @param lat
	 * @param lon
	 * @return
	 */
	private double distance(Segment segment, double lat, double lon) {
		// prepare for coordinates conversions
		CoordSystems cs = new CoordSystems();
		UTMVertex utm = new UTMVertex(0, 0, new UTMZoneId());
		int useUTM = 23;
		double result;
		
		cs.LLtoUTM(useUTM, lat, lon, utm);	
		
		double perpX, perpY;
		double m, q, m1, q1, theta1, theta2;
	    
	    // check whether the shortest way to the segment is the perpendicular one
	    // or the one to one vertex
	    
	    // Find the intersection between the segment line and the perpendicular to it
	    if (segment.endUTMx == segment.startUTMx) {
	        perpY = utm.mUTMy;
	        perpX = segment.startUTMx;
	    } else {
	        // coefficients of line l
	        m = (segment.endUTMy - segment.startUTMy) / (segment.endUTMx - segment.startUTMx);
	        q = -(segment.startUTMx) * m + segment.startUTMy;
	        // coefficients of perpendicular line
	        m1 = -1.0 / m;
	        q1 = utm.mUTMy - m1 * utm.mUTMx;
	        // intersection point perp
	        perpY = m * (q1 - q) / (m - m1) + q;
	        perpX = (q1 - q) / (m - m1);
	    }
	    
	    theta1 = angle(utm.mUTMx, utm.mUTMy, perpX, perpY, segment.startUTMx, segment.startUTMy);
	    theta2 = angle(utm.mUTMx, utm.mUTMy, perpX, perpY, segment.endUTMx, segment.endUTMy);

	    // Check if theta1 and theta2 have the same sign
	    if (theta1 * theta2 >= 0) {
	        // the point is not "in the middle" of the segment
	        // find the minimum distance (to p1 or to p2)
	        double d1, d2;
	        d1 = distance(utm.mUTMx, utm.mUTMy, segment.startUTMx, segment.startUTMy);
	        d2 = distance(utm.mUTMx, utm.mUTMy, segment.endUTMx, segment.endUTMy);
	        if (d1 < d2) {
	            result = d1;
	            // nearest = segment.start;
	        } else {
	            result = d2;
	            // nearest = segment.end
	        }
	    } else {
	        // find the distance along the perpendicular
	        result = distance(utm.mUTMx, utm.mUTMy, perpX, perpY);
	        // nearest = perp;
	    }

	    return result;
	}
	
	
	/// Distance point-point
	private double distance(double p1x, double p1y, double p2x, double p2y) {
	    double diff1, diff2;
	    
	    diff1 = p1y - p2y;
	    diff2 = p1x - p2x;
	    
	    return Math.sqrt(diff1 * diff1 + diff2 * diff2);
	}
	
	/// Angle between p/p1 and p/p2
	private double angle(double px, double py, double p1x, double p1y, double p2x, double p2y) {
	    double theta1, theta2, prod, m1, m2, result;
	    
	    // normalize respect to p
	    p1y -= py;
	    p1x -= px;
	    p2y -= py;
	    p2x -= px;
	    
	    // use the module of vector product between normalized p1 and p2
	    prod = p1x * p2y - p2x * p1y;
	    m1 = module(p1x, p1y);
	    m2 = module(p2x, p2y);
	    if (m1 * m2 == 0) return 0.0;
	    prod /= (m1 * m2);
	    theta1 = Math.asin(prod);
	    
	    prod = p1y * p2y + p1x * p2x;
	    theta2 = Math.acos(prod / (m1 * m2));
	    
	    if (theta1 < 0) {
	        result = -theta2;
	    } else {
	        result = theta2;
	    }
	            
	    return result;
	}
	
	private double module(double x, double y) {
		return Math.sqrt(x*x + y*y);
	}
	
	
	// Representation of a road arc
	private class Segment {
		// identifier of the multiline arc. More segments may have the same id
		public String id;
		
		public double startUTMy;
		public double startUTMx;
		
		public double endUTMy;
		public double endUTMx;
		
		// current car/hour estimation
		public float trafficIntensity;
	}
	
	/** Logger */
	private static Logger mLogger = Logger.getLogger(LEZExtendedServiceImpl.LOGGER_NAME);

	// the set of segments making up the roads network
	private Vector<Segment> mSegments;
}
