/*
 *
Copyright 2014-2015 Department of Information Engineering, University of Pisa

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

// BETaaS - Building the Environment for the Things as a Service
//
// Component: TaaS QoS Manager
// Responsible: Carlo Vallati & Giacomo Tanganelli

package eu.betaas.taas.qosmanager.heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssignmentStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMAssuredRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMRequestStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingServiceStar;
import eu.betaas.taas.bigdatamanager.database.hibernate.data.QoSMThingStar;

/**
 * The Class Reservation.
 */
public class Reservation {

	private class AssignmentPlus{
		
		public AssignmentPlus(QoSMAssignmentStar ass, double d) {
			this.ass = ass;
			v = d;
		}

		/** The value. */
		double v;
		
		QoSMAssignmentStar ass;
	}
	
	/**
	 * The Class Reserveobj.
	 */
	private class Reserveobj {
		
		/** The feasible. */
		boolean feasible = false;
		
		/** The z. */
		double z = 0.0;
		
		/** The b. */
		ArrayList<QoSMThingStar> b;
		
		/** The y. */
		ArrayList<QoSMAssignmentStar> y;
		

		
		/**
		 * Instantiates a new Reserveobj.
		 *
		 */
		Reserveobj() {
			b = new ArrayList<QoSMThingStar>();
			y = new ArrayList<QoSMAssignmentStar>();
		}
		
		public String toString(int which){
			String msg = new String();
			msg = "Feasible: " + String.valueOf(feasible);
			msg += "\nWhich: " + String.valueOf(which);
			msg += "\nAssignments: ";
			for(QoSMAssignmentStar a : y)
			{
				msg += a.toString();
			}
			return msg;
		}
		
	}
	
	/**
	 * Execute the heuristic with different parameters and return the allocation schema which 
	 * maximize the objective function.
	 *
	 * @param k the list of requests
	 * @param p the costs of assign a request to a thing
	 * @param b2 the characteristics of a thing 
	 * @param ts 
	 * @param epsilon the tolerance accepted
	 * @return the reservation results
	 */
	public ReservationResults compute(List<QoSMRequestStar> k, ArrayList<QoSMAssignmentStar> p, 
			List<QoSMThingStar> b2, Map<String, QoSMThingServiceStar> ts, double epsilon){
		
		Reserveobj[] res = new Reserveobj[3];
		ArrayList<QoSMAssignmentStar> F = new ArrayList<QoSMAssignmentStar>(); // Feasibility matrix
		
		for(int i=0;i<p.size();i++){
			F.add(new QoSMAssignmentStar(p.get(i)));
		}
		
		res[0] = ABGAP(k, F, p, b2, ts, epsilon, true);

		F.clear();
		for(int i=0;i<p.size();i++){
			F.add(new QoSMAssignmentStar(p.get(i)));
		}
		
		res[1]= ABGAP(k, F, p, b2, ts, epsilon, false);

		F.clear();
		for(int i=0;i<p.size();i++){
			QoSMAssignmentStar a = new QoSMAssignmentStar(p.get(i));
			a.setTotalBatteryCost(p.get(i).getTotalComputationalCost());
			F.add(a);
		}

		res[2] = ABGAP(k, F, p, b2, ts, epsilon, false);

		ReservationResults ret = new ReservationResults();
		int imax=0;
		// Gets the best heuristic
		for(int j=1;j<3;j++)
		{
			if(res[imax].z<res[j].z && res[j].feasible)
				imax = j;
		}
		if(res[imax].feasible){
			ret.setFeasible(true);
			ArrayList<QoSMThingStar> b = new ArrayList<QoSMThingStar>();
			for(QoSMThingStar t : res[imax].b)
			{
				b.add(new QoSMThingStar(t));
			}
			ret.setB(b);
			ret.y = new ArrayList<QoSMAssignmentStar>();
			for(QoSMAssignmentStar a : res[imax].y)
			{
				ret.y.add(new QoSMAssignmentStar(a));
			}
			ret.which = imax;
		}
		return ret;
	}
	
	/**
	 * Execute the heuristic specifically tailored for battery consumption.
	 *
	 * @param n the number of things
	 * @param k the number of request
	 * @param p the matrix of battery costs 
	 * @param W the matrix of computational costs
	 * @param b2 the vector of starting battery
	 * @param ts 
	 * @param C 
	 * @param numass 
	 * @param epsilon the tolerance used to stop iterations
	 * @return the reserve object
	 */
	private Reserveobj ABGAP(List<QoSMRequestStar> k, ArrayList<QoSMAssignmentStar> f, ArrayList<QoSMAssignmentStar> p, 
			List<QoSMThingStar> b2, Map<String, QoSMThingServiceStar> ts, double epsilon, boolean battery){
		Reserveobj res = null;
		double upper = 1.0;
		double lower = 0.0;
		double teta = 0;
		double z = 0;
		//System.out.println("teta = "+teta);
		res = GAP(k, f, p, b2, ts, teta, battery);
	
		if(res.feasible == true)
		{
			teta = (upper - lower) / 2;
			while((upper - lower) > epsilon)
			{
				//System.out.println("teta = "+teta);
				res = GAP(k, f, p, b2, ts, teta, battery);
	
				if(res.feasible)
				{
					z = teta;
					lower = teta;
					teta = teta + ((upper-lower) / 2 );
				}
				else
				{
					upper = teta;
					teta = teta - ((upper-lower) / 2 );
				}
	
			}
			if(!res.feasible){
				teta = z;
				//System.out.println("teta = "+teta);
				res = GAP(k, f, p, b2, ts, teta, battery);
			}
		}
		
		res.z = z;
		return res;
	}

	/**
	 * Real_battery_ gap.
	 *
	 * @param n the n
	 * @param k2 the k
	 * @param ts 
	 * @param F the f
	 * @param W the w
	 * @param B the b
	 * @param C 
	 * @param teta the teta
	 * @return the reserveobj
	 */
	private Reserveobj GAP(List<QoSMRequestStar> k2, ArrayList<QoSMAssignmentStar> f2, ArrayList<QoSMAssignmentStar> p2, 
			List<QoSMThingStar> b2, Map<String, QoSMThingServiceStar> s, double teta, boolean battery) {
	
		Reserveobj res = new Reserveobj();
		
		ArrayList<AssignmentPlus> Fj = new ArrayList<AssignmentPlus>();
		QoSMAssignmentStar max;
		QoSMAssignmentStar[] ma;
		QoSMAssignmentStar chosen = null;
		
		double ds, d;
		
		double INF = Double.POSITIVE_INFINITY;
		
		ArrayList<QoSMRequestStar> K = cloneRequests(k2);
		ArrayList<QoSMAssignmentStar> F = cloneAssignments(f2);
		ArrayList<QoSMAssignmentStar> P = cloneAssignments(p2);
		ArrayList<QoSMThingStar> B = cloneThings(b2);
		
		Map<String, QoSMThingServiceStar> S = cloneThingServices(s);
		
		res.feasible = true;
		res.b.clear();
		res.b = B;

		ArrayList<QoSMRequestStar> bckK = cloneRequests(K);

		
		while(res.feasible && !K.isEmpty()){
			ds = -1 * INF;
			for(int j=0;j<K.size();j++){
				QoSMRequestStar req = K.get(j);
				Fj.clear();
				for(QoSMAssignmentStar a : F)
				{
					if(!(a.getId().getServiceId().equals(req.getId().getServiceId()) && 
							a.getId().getRequestId() == req.getId().getRequestId()))
						continue;
					
					QoSMThingServiceStar ts = S.get(a.getId().getThingServiceId());
					QoSMThingStar t = getThing(ts.getDeviceId(), res.b);
					double exp = 1.0 /(t.getNumass() + 1.0);
					double cal_res =  Math.pow(2, exp);
					double upper = (t.getNumass() + 1.0 ) * (cal_res - 1.0);
					if((a.getTotalComputationalCost() + t.getCapacityUsed()) < upper 
							&& (t.getBatteryLevel() - a.getTotalBatteryCost()) >= teta){
						
						QoSMAssignmentStar ass = new QoSMAssignmentStar(req.getId().getServiceId(),
								req.getId().getRequestId(), ts.getThingServiceId(), a.getTotalBatteryCost(), 
								a.getTotalComputationalCost());

						AssignmentPlus assp = null;
						if(battery == true){
							assp = new AssignmentPlus(ass, t.getBatteryLevel() - ass.getTotalBatteryCost());
						}
						else{
							assp = new AssignmentPlus(ass,ass.getTotalBatteryCost());
						}
						Fj.add(assp);
					}
				}
				if(Fj.isEmpty()){
					res.feasible = false;
				}
				else{
					
					if(Fj.size() <= 1){
						d = INF;
						max = Fj.get(0).ass;
					}
					else{
						ma = maximum(Fj);
						max=ma[0];
						d = max.getTotalBatteryCost() - ma[1].getTotalBatteryCost();
					}
					if(d>ds){
						ds = d;
						chosen=max;
					}
				}
			}// end for
			if(res.feasible){
				QoSMThingServiceStar ts = S.get(chosen.getId().getThingServiceId());
				QoSMThingStar t = getThing(ts.getDeviceId(), res.b);
				res.y.add(new QoSMAssignmentStar(chosen));
				t.setBatteryLevel(t.getBatteryLevel() - chosen.getTotalBatteryCost());
				t.setCapacityUsed((t.getCapacityUsed() + chosen.getTotalComputationalCost()));
				int num = t.getNumass();
				num++;
				t.setNumass(num);
				QoSMRequestStar del=null;
				for(QoSMRequestStar r : K)
				{
					if(r.getId().getServiceId().equals(chosen.getId().getServiceId()) && 
							r.getId().getRequestId() == chosen.getId().getRequestId())
						del = r;
				}
				K.remove(del);
			}

		}
		if(!battery && res.feasible)
		{
			QoSMAssignmentStar ip, min;
			
			for(int j=0;j<bckK.size();j++){
				ip = res.y.get(j);
				
				Fj.clear();
				QoSMRequestStar req = getRequest(ip.getId().getServiceId(), ip.getId().getRequestId(), bckK);
				for(QoSMAssignmentStar a : P)
				{
					if(!(a.getId().getServiceId().equals(req.getId().getServiceId()) && 
							a.getId().getRequestId() == req.getId().getRequestId()))
						continue;
					QoSMThingServiceStar ts = S.get(a.getId().getThingServiceId());
					QoSMThingStar t = getThing(ts.getDeviceId(), res.b);
					double upper = (t.getNumass() + 1) * Math.pow(2, ((1/(t.getNumass() + 1))-1));
					
					if((a.getTotalComputationalCost() + t.getCapacityUsed()) < upper 
							&& (t.getBatteryLevel() - a.getTotalBatteryCost()) >= teta){
						if(!ip.equals(a)){
							QoSMAssignmentStar ass = new QoSMAssignmentStar(req.getId().getServiceId(),
									req.getId().getRequestId(), ts.getThingServiceId(), a.getTotalBatteryCost(), 
									a.getTotalComputationalCost());
							AssignmentPlus assp = null;
							assp = new AssignmentPlus(ass, t.getBatteryLevel() - ass.getTotalBatteryCost());
							Fj.add(assp);
						}
					}
				}
				
				
				if(!Fj.isEmpty()){
					min = minimum(Fj);
					if(min.getTotalBatteryCost() < ip.getTotalBatteryCost()){
						QoSMAssignmentStar del = null;
						for(QoSMAssignmentStar a : res.y)
						{
							if(a.equals(ip))
								del = a;
						}
						res.y.remove(del);
						res.y.add(new QoSMAssignmentStar(min));
						
						QoSMThingServiceStar ts = S.get(ip.getId().getThingServiceId());
						QoSMThingStar t = getThing(ts.getDeviceId(), res.b);
						
						t.setBatteryLevel(t.getBatteryLevel() + ip.getTotalBatteryCost());
						t.setCapacityUsed(t.getCapacityUsed() - ip.getTotalComputationalCost());
						
						ts = S.get(min.getId().getThingServiceId());
						QoSMThingStar t2 = getThing(ts.getDeviceId(), res.b);
						t2.setBatteryLevel(t2.getBatteryLevel() - min.getTotalBatteryCost());
						t2.setCapacityUsed(t2.getCapacityUsed() + min.getTotalComputationalCost());
						
						
						int num = t.getNumass();
						t.setNumass(num--);
						num = t2.getNumass();
						t2.setNumass(num++);
		
					}
				}
		
			}
	
		}
		
		
		
		/*if(res.feasible)
		{
			for(QoSMThingStar t : res.b)
			{
				QoSMThingStar origin = getThing(t.getDeviceId(), B);
				t.setBatteryLevel(t.getBatteryLevel() * origin.getBatteryLevel());
			}
		}*/
		return res;
	}

	private Map<String, QoSMThingServiceStar> cloneThingServices(
			Map<String, QoSMThingServiceStar> ts) {
		Map<String, QoSMThingServiceStar> clone = new HashMap<String, QoSMThingServiceStar>();
		Set<String> keys = ts.keySet();
		for(String k : keys){
			clone.put(k, new QoSMThingServiceStar(ts.get(k)));
		}
		return clone;
	}

	private static ArrayList<QoSMRequestStar> cloneRequests(List<QoSMRequestStar> k2) {
		ArrayList<QoSMRequestStar> clone = new ArrayList<QoSMRequestStar>(k2.size());
	    for(QoSMRequestStar item: k2) 
	    	clone.add(new QoSMRequestStar(item));
	    return clone;
	}
	
	private static ArrayList<QoSMAssignmentStar> cloneAssignments(ArrayList<QoSMAssignmentStar> f2) {
		ArrayList<QoSMAssignmentStar> clone = new ArrayList<QoSMAssignmentStar>(f2.size());
	    for(QoSMAssignmentStar item: f2) 
	    	clone.add(new QoSMAssignmentStar(item));
	    return clone;
	}
	
	private static ArrayList<QoSMThingStar> cloneThings(List<QoSMThingStar> b2) {
		ArrayList<QoSMThingStar> clone = new ArrayList<QoSMThingStar>(b2.size());
	    for(QoSMThingStar item: b2) 
	    	clone.add(new QoSMThingStar(item));
	    return clone;
	}
	

	/**
	 * Execute the heuristic specifically tailored for battery consumption.
	 *
	 * @param n the number of things
	 * @param k the number of request
	 * @param F the matrix of desiderable values
	 * @param P the matrix of battery costs 
	 * @param W the matrix of computational costs
	 * @param B the vector of starting battery
	 * @param epsilon the tolerance used to stop iterations
	 * @return the reserve object
	 */
	/*private Reserveobj real_general_ABGAP(ArrayList<Request> K,  ArrayList<Assignment> F, ArrayList<Assignment> P, 
			ArrayList<Thing> B, double epsilon){
		Reserveobj res = null;
		double upper = 1.0;
		double lower = 0.0;
		double teta = 0;
		double z = 0;
		res = real_general_GAP(K, F, P, B, teta);
	
		if(res.feasible== true)
		{
			teta = (upper - lower) / 2;
			while((upper - lower) > epsilon)
			{
				res = real_general_GAP(K, F, P, B, teta);
	
				if(res.feasible)
				{
					z = teta;
					lower = teta;
					teta = teta + ((upper-lower) / 2 );
				}
				else
				{
					upper = teta;
					teta = teta - ((upper-lower) / 2 );
				}
	
			}
			if(!res.feasible){
				teta = z;
				res = real_general_GAP(K, F, P, B, teta);
			}
		}
		
		res.z = z;
		return res;
	}
*/
	/**
	 * Real_general_ gap.
	 *
	 * @param n the n
	 * @param k the k
	 * @param F the f
	 * @param P the p
	 * @param W the w
	 * @param B the b
	 * @param numass 
	 * @param C 
	 * @param teta the teta
	 * @return the reserveobj
	 */
	/*private Reserveobj real_general_GAP(ArrayList<Request> K,  ArrayList<Assignment> F, ArrayList<Assignment> P, 
			ArrayList<Thing> B, double teta) {
	
		Reserveobj res = new Reserveobj();
		
	
		List<Assignment> Fj = new ArrayList<Assignment>();
	
		
		Assignment max;
		Assignment[] ma;
		Assignment chosen = null;
		
		//ArrayPlus[] ma = null;
		double ds, d;
		double INF = Double.POSITIVE_INFINITY;
	
		res.feasible = true;
		res.b.clear();
		for(Thing t : B){
			res.b.add(new Thing(t));
		}
		for(Thing t : res.b)
			t.setBatteryLevel(1.0);
		
		ArrayList<Request> bckK = new ArrayList<Request>();
		bckK.addAll(K);
		
		while(res.feasible && !K.isEmpty()){
			ds = -1 * INF;
			for(int j=0;j<K.size();j++){
				Request req = K.get(j);
				for(Assignment a : P)
				{
					if(!a.getRequest().equals(req))
						continue;
					ThingService ts = a.getThingService();
					Thing t = getThing(ts.getDeviceId(), res.b);
					double upper = (t.getNumass() + 1) * Math.pow(2, ((1/(t.getNumass() + 1))-1));
					if((ts.getQosspec().getComputationalCost() + t.getCapacityUsed()) < upper 
							&& (t.getBatteryLevel() - ts.getQosspec().getBatteryCost()) >= teta){
						Assignment ass = new Assignment(req, ts);
						ass.getThingService().getQosspec().setBatteryCost(getF(F,ts));
						Fj.add(ass);
					}
				}
				if(Fj.isEmpty()){
					res.feasible = false;
				}
				else{
					
					if(Fj.size() <= 1){
						d = INF;
						max = Fj.get(0);
						
					}
					else{
						
						ma = maximum(Fj);
						max=ma[0];
						d = max.getThingService().getQosspec().getBatteryCost() - ma[1].getThingService().getQosspec().getBatteryCost();
					}
					if(d>ds){
						ds = d;
						chosen=new Assignment(max);
					}
				}
			}// end for
			if(res.feasible){
				Thing t = getThing(chosen.getThingService().getDeviceId(), res.b);
				res.y.add(chosen);
				t.setBatteryLevel(t.getBatteryLevel() - chosen.getThingService().getQosspec().getBatteryCost());
				t.setCapacityUsed((t.getCapacityUsed() + chosen.getThingService().getQosspec().getComputationalCost()));
				int num = t.getNumass();
				t.setNumass(num++);
				K.remove(chosen.getRequest());
			}
		}
		
		Assignment ip, min;
	
		for(int j=0;j<bckK.size();j++){
			ip = res.y.get(j);
			
			Fj.clear();
			Request req = ip.getRequest();
			for(Assignment a : P)
			{
				if(!a.getRequest().equals(req))
					continue;
				ThingService ts = a.getThingService();
				Thing t = getThing(ts.getDeviceId(), res.b);
				double upper = (t.getNumass() + 1) * Math.pow(2, ((1/(t.getNumass() + 1))-1));
				if((ts.getQosspec().getComputationalCost() + t.getCapacityUsed()) < upper 
						&& (t.getBatteryLevel() - ts.getQosspec().getBatteryCost()) >= teta){
					if(ip!= a){
						Assignment ass = new Assignment(req, ts);
						Fj.add(ass);
					}
				}
			}
			
			
			if(Fj.isEmpty()){
				min = minimum(Fj);
				if(min.getThingService().getQosspec().getBatteryCost() < ip.getThingService().getQosspec().getBatteryCost()){
					
					res.y.remove(ip);
					res.y.add(new Assignment(min));
										
					Thing t = getThing(ip.getThingService().getDeviceId(), res.b);
					
					t.setBatteryLevel(t.getBatteryLevel() + ip.getThingService().getQosspec().getBatteryCost());
					t.setCapacityUsed(t.getCapacityUsed() - ip.getThingService().getQosspec().getComputationalCost());
					
					Thing t2 = getThing(min.getThingService().getDeviceId(), res.b);
					t2.setBatteryLevel(t2.getBatteryLevel() - min.getThingService().getQosspec().getBatteryCost());
					t2.setCapacityUsed(t2.getCapacityUsed() - min.getThingService().getQosspec().getComputationalCost());
					
					
					int num = t.getNumass();
					t.setNumass(num--);
					num = t2.getNumass();
					t2.setNumass(num++);
	
				}
			}
	
		}
		for(Thing t : res.b)
		{
			Thing origin = getThing(t.getDeviceId(), B);
			t.setBatteryLevel(t.getBatteryLevel() * origin.getBatteryLevel());
		}
		return res;
	}
*/
	/**
	 * Execute the heuristic with different parameters and return the allocation schema which 
	 * maximize the objective function .
	 *
	 * @param n the number of things
	 * @param k the number of request
	 * @param P the matrix of battery costs
	 * @param W the matrix of computational costs
	 * @param B the vector of starting battery
	 * @param C the vector of starting capacity
	 * @param epsilon the tolerance used to stop iterations
	 * @return the reservation results
	 */
	/*
	public ReservationResults compute(Thing[] N, ArrayList<Request> K, ThingService[][] P, 
			Computational[][] W, Thing[] B, Computational[] C,
			double epsilon){
		int n=N.length;
		int k=K.size();
		Reserveobj[] res = new Reserveobj[3];
		ThingService[][] F = new ThingService[n][k]; // Feasibility matrix
		
		res[0] = battery_ABGAP(N, K, P, W, B, C, epsilon);
		
		for(int i=0;i<n;i++){
			F[i]=P[i].clone();
		}
		res[1]= general_ABGAP(N, K, F, P, W, B, C, epsilon);
		for(int i=0;i<n;i++){
			F[i]=P[i].clone();
			for(int j=0;j<k;j++)
				F[i][j].setCost(W[i][j].getComputationalCost());
		}
		res[2] = general_ABGAP(N, K, F, P, W, B, C, epsilon);
		
		ReservationResults ret = new ReservationResults(n, k);
		int imax=0;
		for(int j=1;j<3;j++)
		{
			if(res[imax].z<res[j].z && res[j].feasible)
				imax = j;
		}
		if(res[imax].feasible){
			ret.setFeasible(true);
			ret.setB(res[imax].b.clone());
			ret.c = res[imax].c.clone();
			ret.y = new ArrayList<Assignment>(res[imax].y);
			ret.which = imax;
			ret.numass = res[imax].numass.clone();
		}
		return ret;
	}*/

	/**
	 * Execute the heuristic specifically tailored for battery consumption.
	 *
	 * @param n the number of things
	 * @param k the number of request
	 * @param P the matrix of battery costs 
	 * @param W the matrix of computational costs
	 * @param B the vector of starting battery
	 * @param C the vector of starting capacity
	 * @param epsilon the tolerance used to stop iterations
	 * @return the reserve object
	 */
	/*private Reserveobj battery_ABGAP(Thing[] N, ArrayList<Request> K, ThingService[][] P, 
			Computational[][] W, Thing[] B, Computational[] C,
			double epsilon){
		Reserveobj res = null;
		double upper = 1.0;
		double lower = 0.0;
		double teta = 0;
		double z = 0;
		
		res = battery_GAP(N, K, P, W, B, C, teta);

		if(res.feasible== true)
		{
			teta = (upper - lower) / 2;
			while((upper - lower) > epsilon)
			{
				res = battery_GAP(N, K, P, W, B, C, teta);

				if(res.feasible)
				{
					z = teta;
					lower = teta;
					teta = teta + ((upper-lower) / 2 );
				}
				else
				{
					upper = teta;
					teta = teta - ((upper-lower) / 2 );
				}

			}
			if(!res.feasible){
				teta = z;
				res = battery_GAP(N, K, P, W, B, C, teta);
			}
		}
		
		res.z = z;
		return res;
	}*/
	/**
	 * Solve the GAP problem with the additional constraint of a certain threshold (teta).
	 *
	 * @param n the number of things
	 * @param k the number of request
	 * @param P the matrix of battery costs 
	 * @param W the matrix of computational costs
	 * @param B the vector of starting battery
	 * @param C the vector of starting capacity
	 * @param teta the minimum remaining battery ratio
	 * @return the reserve object
	 */
/*
	private Reserveobj battery_GAP(ThingService[] N, ArrayList<Request> K, ThingService[][] P, 
			Computational[][] W, Thing[] B, Computational[] C, double teta) {
		
		int n = N.length;
		int k= K.size();
		Reserveobj res = new Reserveobj(n,k);
		
		Computational[] wass = new Computational[n];
		List<Integer> Fj = new ArrayList<Integer>();
		List<ArrayPlus> ORD = new ArrayList<ArrayPlus>();
		ArrayPlus[] ma = null;
		double ds, d, max;
		int imax, is = 0, js = 0;
		double INF = Double.POSITIVE_INFINITY;
		
		java.util.Arrays.fill(wass, 0);
			

		res.feasible = true;
		res.b = B.clone();
		res.c = C.clone();
		
		while(res.feasible && !K.isEmpty()){
			ds = -1 * INF;
			for(int j=0;j<k;j++){
				for(int i=0; i<n; i++){
					if((W[i][j].getComputationalCost() + wass[i].getComputationalCost()) < res.c[i].getComputationalCost() 
							&& (res.b[i].getBatteryLevel() - P[i][j].getCost()) >= teta){
						
						Fj.add(i);
					}			
						
				}
				if(Fj.isEmpty()){
					res.feasible = false;
				}
				else{
					for(Integer i : Fj){
						ArrayPlus tmp = new ArrayPlus();
						tmp.v = res.b[i].getBatteryLevel() - P[i][j].getCost();
						tmp.index = i;
						ORD.add(tmp);
					}
					if(Fj.size() <= 1){
						d = INF;
						max = ORD.get(0).v;
						imax = ORD.get(0).index;
					}
					else{
						ma = maximum(ORD);
						max = ma[0].v;
						imax = ma[0].index;
						d = max - ma[1].v;
					}
					if(d>ds){
						ds = d;
						is = imax;
						js = j;
					}
				}
			}// end for
			if(res.feasible){
				res.y.add(new Assignment(K.get(js),N[is]));
				res.b[is].setBatteryLevel(res.b[is].getBatteryLevel() - P[is][js].getCost());
				wass[is].setComputationalCost(wass[is].getComputationalCost() + W[is][js].getComputationalCost());
				K.remove(js);
			}
		}

		return res;
	}
	*/
	/**
	 * Execute the heuristic specifically tailored for battery consumption.
	 *
	 * @param n the number of things
	 * @param k the number of request
	 * @param F the matrix of desiderable values
	 * @param P the matrix of battery costs 
	 * @param W the matrix of computational costs
	 * @param B the vector of starting battery
	 * @param C the vector of starting capacity
	 * @param epsilon the tolerance used to stop iterations
	 * @return the reserve object
	 */
	/*private Reserveobj general_ABGAP(Thing[] N, ArrayList<Request> K, ThingService[][] F, ThingService[][] P, 
			Computational[][] W, Thing[] B, Computational[] C,
			double epsilon){
		Reserveobj res = null;
		double upper = 1.0;
		double lower = 0.0;
		double teta = 0;
		double z = 0;
		res = general_GAP(N, K, F, P, W, B, C, teta);

		if(res.feasible== true)
		{
			teta = (upper - lower) / 2;
			while((upper - lower) > epsilon)
			{
				res = general_GAP(N, K, F, P, W, B, C, teta);

				if(res.feasible)
				{
					z = teta;
					lower = teta;
					teta = teta + ((upper-lower) / 2 );
				}
				else
				{
					upper = teta;
					teta = teta - ((upper-lower) / 2 );
				}

			}
			if(!res.feasible){
				teta = z;
				res = general_GAP(N, K, F, P, W, B, C, teta);
			}
		}
		
		res.z = z;
		return res;
	}*/
	/**
	 * Solve the GAP problem with the additional constraint of a certain threshold (teta).
	 *
	 * @param n the number of things
	 * @param k the number of request
	 * @param F the matrix of desiderable values
	 * @param P the matrix of battery costs 
	 * @param W the matrix of computational costs
	 * @param B the vector of starting battery
	 * @param C the vector of starting capacity
	 * @param teta the minimum remaining battery ratio
	 * @return the reserve object
	 */
	/*private Reserveobj general_GAP(Thing[] N, ArrayList<Request> K, ThingService[][] F, ThingService[][] P, 
			Computational[][] W, Thing[] B, Computational[] C, double teta) {

		int n = N.length;
		int k= K.size();
		Reserveobj res = new Reserveobj(n,k);
		
		Computational[] wass = new Computational[n];
		List<Integer> Fj = new ArrayList<Integer>();
		List<ArrayPlus> ORD = new ArrayList<ArrayPlus>();
		ArrayPlus[] ma = null;
		double ds, d, max;
		int imax, is = 0, js = 0;
		double INF = Double.POSITIVE_INFINITY;
		
		java.util.Arrays.fill(wass, 0);
		
		res.feasible = true;
		res.b = B.clone();
		
		ArrayList<Request> bckK = new ArrayList<Request>();
		bckK.addAll(K);
		
		while(res.feasible && !K.isEmpty()){
			ds = -1 * INF;
			for(int j=0;j<k;j++){
				for(int i=0; i<n; i++){
					if((W[i][j].getComputationalCost() + wass[i].getComputationalCost()) < C[i].getComputationalCost() 
							&& (res.b[i].getBatteryLevel() - P[i][j].getCost()) >= teta){
						Fj.add(i);
					}
				}
				if(Fj.isEmpty()){
					res.feasible = false;
				}
				else{
					for(Integer i : Fj){
						ArrayPlus tmp = new ArrayPlus();
						tmp.v = F[i][j].getCost();
						tmp.index = i;
						ORD.add(tmp);
					}
					
					if(Fj.size() <= 1){
						d = INF;
						max = ORD.get(0).v;
						imax = ORD.get(0).index;
					}
					else{
						ma = maximum(ORD);
						max = ma[0].v;
						imax = ma[0].index;
						d = max - ma[1].v;
					}
					if(d>ds){
						ds = d;
						is = imax;
						js = j;
					}
				}
			}// end for
			if(res.feasible){
				res.y.add(new Assignment(K.get(js),N[is], js, is));
				res.b[is].setBatteryLevel(res.b[is].getBatteryLevel() - P[is][js].getCost());
				wass[is].setComputationalCost(wass[is].getComputationalCost() + W[is][js].getComputationalCost());
				K.remove(js);
			}
		}
		int ip = 0, newi;
		double m;
		ArrayPlus min;

		for(int j=0;j<k;j++){
			for(Assignment a : res.y)
			{
				if(a.js == j)
					ip=a.is;
			}
	
			Fj.clear();
			ORD.clear();
	
			for(int i=0; i<n; i++){
				if((W[i][j].getComputationalCost() + wass[i].getComputationalCost()) < C[i].getComputationalCost() 
						&& (res.b[i].getBatteryLevel() - P[i][j].getCost()) >= teta){
					if(i!=ip){
						Fj.add(i);
					}
				}
			}
			for(Integer i : Fj){
				ArrayPlus tmp = new ArrayPlus();
				tmp.v = P[i][j].getCost();
				tmp.index = i;
				ORD.add(tmp);
			}
			if(Fj.isEmpty()){
				//qsort(ORD, Fjsize, sizeof(struct arrayplus), cmpfuncmin);
				min = minimum(ORD);
				newi=min.index;
				m=min.v;
				if(m < P[ip][j].getCost()){
					Assignment tmp = null;
					for(Assignment a : res.y)
					{
						if(a.js == j)
							tmp=a;
					}
					if(tmp!=null)
						res.y.remove(tmp);
					res.y.add(new Assignment(bckK.get(j), N[newi], j, newi));
					res.b[ip].setBatteryLevel(res.b[ip]
							.getBatteryLevel() + P[ip][j].getCost());
					res.b[newi].setBatteryLevel(res.b[newi]
							.getBatteryLevel() - m);
					wass[ip].setComputationalCost(wass[ip]
							.getComputationalCost() - W[ip][j].getComputationalCost());
					wass[newi].setComputationalCost(wass[newi]
							.getComputationalCost() + W[newi][j].getComputationalCost());
				}
			}
		}
		return res;
	}*/
	
	private QoSMThingStar getThing(String deviceId, List<QoSMThingStar> b) {
		for(QoSMThingStar t : b)
		{
			if(t.getDeviceId().equals(deviceId))
				return t;
		}
		return null;
	}
	
	private QoSMRequestStar getRequest(String serviceId, Integer requestId, ArrayList<QoSMRequestStar> k) {
		for(QoSMRequestStar r : k)
		{
			if(r.getId().getServiceId().equals(serviceId) && r.getId().getRequestId() == requestId)
				return r;
		}
		return null;
	}

	/*private double getF(ArrayList<Assignment> f, ThingService ts) {
		for(Assignment a : f)
		{
			if(a.getThingService().getDeviceId().equals(ts.getDeviceId()))
				return a.getThingService().getQosspec().getBatteryCost();
		}
		return 0;
	}
*/
	/**
	 * Maximum.
	 *
	 * @param A the a
	 * @return the array plus[]
	 */
	private QoSMAssignmentStar[] maximum(List<AssignmentPlus> A) {
		QoSMAssignmentStar[] ret = new QoSMAssignmentStar[2];
		if (A.get(0).ass.getTotalBatteryCost() >= 
				A.get(1).ass.getTotalBatteryCost()){
			ret[0] = A.get(0).ass;
			ret[1] = A.get(1).ass;
		}
		else
		{
			ret[0] = A.get(1).ass;
			ret[1] = A.get(0).ass;
		}
		for(int i=2; i<A.size(); i++){
			if(A.get(i).ass.getTotalBatteryCost() > 
					ret[0].getTotalBatteryCost()){
				ret[1]=ret[0];
				ret[0]=A.get(i).ass;
			}
			else{
				if(A.get(i).ass.getTotalBatteryCost() > 
						ret[1].getTotalBatteryCost()){
					ret[1]=A.get(i).ass;
				}
			}
		}
		return ret;
	}
	
	/**
	 * Minimum.
	 *
	 * @param A the a
	 * @return the array plus
	 */
	private QoSMAssignmentStar minimum(List<AssignmentPlus> A) {
		QoSMAssignmentStar ret = A.get(0).ass;
		for(int i=1; i<A.size(); i++){
			if(A.get(i).ass.getTotalBatteryCost() < ret.getTotalBatteryCost()){
				ret=A.get(i).ass;
			}
		}
		return ret;
	}

	public ReservationResults computeAssured(List<QoSMAssuredRequestStar> k,
			ArrayList<QoSMAssignmentStar> p, List<QoSMThingStar> b2,
			Map<String, QoSMThingServiceStar> ts, double epsilon, Map<String, Double> Cis, 
			Map<String, Double> Tis, Map<String, Double> MRT) {
		Reserveobj[] res = new Reserveobj[3];
		ArrayList<QoSMAssignmentStar> F = new ArrayList<QoSMAssignmentStar>(); // Feasibility matrix
		
		for(int i=0;i<p.size();i++){
			F.add(new QoSMAssignmentStar(p.get(i)));
		}
		
		res[0] = ABGAPAssured(k, F, p, b2, ts, epsilon, true, Cis, Tis, MRT);

		F.clear();
		for(int i=0;i<p.size();i++){
			F.add(new QoSMAssignmentStar(p.get(i)));
		}
		
		res[1]= ABGAPAssured(k, F, p, b2, ts, epsilon, false, Cis, Tis, MRT);

		F.clear();
		for(int i=0;i<p.size();i++){
			QoSMAssignmentStar a = new QoSMAssignmentStar(p.get(i));
			a.setTotalBatteryCost(p.get(i).getTotalComputationalCost());
			F.add(a);
		}

		res[2] = ABGAPAssured(k, F, p, b2, ts, epsilon, false, Cis, Tis, MRT);

		ReservationResults ret = new ReservationResults();
		int imax=0;
		// Gets the best heuristic
		for(int j=1;j<3;j++)
		{
			if(res[imax].z<res[j].z && res[j].feasible)
				imax = j;
		}
		if(res[imax].feasible){
			ret.setFeasible(true);
			ArrayList<QoSMThingStar> b = new ArrayList<QoSMThingStar>();
			for(QoSMThingStar t : res[imax].b)
			{
				b.add(new QoSMThingStar(t));
			}
			ret.setB(b);
			ret.y = new ArrayList<QoSMAssignmentStar>();
			for(QoSMAssignmentStar a : res[imax].y)
			{
				ret.y.add(new QoSMAssignmentStar(a));
			}
			ret.which = imax;
		}
		return ret;
	}

	private Reserveobj ABGAPAssured(List<QoSMAssuredRequestStar> k,
			ArrayList<QoSMAssignmentStar> f, ArrayList<QoSMAssignmentStar> p,
			List<QoSMThingStar> b2, Map<String, QoSMThingServiceStar> ts,
			double epsilon, boolean battery, Map<String, Double> Cis, Map<String, Double> Tis, Map<String, Double> MRT) {
		Reserveobj res = null;
		double upper = 1.0;
		double lower = 0.0;
		double teta = 0;
		double z = 0;
		//System.out.println("teta = "+teta);
		res = GAPAssured(k, f, p, b2, ts, teta, battery, Cis, Tis, MRT);
	
		if(res.feasible == true)
		{
			teta = (upper - lower) / 2;
			while((upper - lower) > epsilon)
			{
				//System.out.println("teta = "+teta);
				res = GAPAssured(k, f, p, b2, ts, teta, battery, Cis, Tis, MRT);
	
				if(res.feasible)
				{
					z = teta;
					lower = teta;
					teta = teta + ((upper-lower) / 2 );
				}
				else
				{
					upper = teta;
					teta = teta - ((upper-lower) / 2 );
				}
	
			}
			if(!res.feasible){
				teta = z;
				//System.out.println("teta = "+teta);
				res = GAPAssured(k, f, p, b2, ts, teta, battery, Cis, Tis, MRT);
			}
		}
		
		res.z = z;
		return res;
	}

	private Reserveobj GAPAssured(List<QoSMAssuredRequestStar> k2,
			ArrayList<QoSMAssignmentStar> f2, ArrayList<QoSMAssignmentStar> p2,
			List<QoSMThingStar> b2, Map<String, QoSMThingServiceStar> s,
			double teta, boolean battery, Map<String, Double> Cis, Map<String, Double> Tis, Map<String, Double> MRT) {
		
		Reserveobj res = new Reserveobj();
		
		ArrayList<AssignmentPlus> Fj = new ArrayList<AssignmentPlus>();
		QoSMAssignmentStar max;
		QoSMAssignmentStar[] ma;
		QoSMAssignmentStar chosen = null;
		
		double ds, d;
		
		double INF = Double.POSITIVE_INFINITY;
		
		ArrayList<QoSMAssuredRequestStar> K = cloneRequestsAssured(k2);
		ArrayList<QoSMAssignmentStar> F = cloneAssignments(f2);
		ArrayList<QoSMAssignmentStar> P = cloneAssignments(p2);
		ArrayList<QoSMThingStar> B = cloneThings(b2);
		
		Map<String, QoSMThingServiceStar> S = cloneThingServices(s);
		
		res.feasible = true;
		res.b.clear();
		res.b = B;

		ArrayList<QoSMAssuredRequestStar> bckK = cloneRequestsAssured(K);

		
		while(res.feasible && !K.isEmpty()){
			ds = -1 * INF;
			for(int j=0;j<K.size();j++){
				QoSMAssuredRequestStar req = K.get(j);
				Fj.clear();
				for(QoSMAssignmentStar a : F)
				{
					if(!(a.getId().getServiceId().equals(req.getId().getServiceId()) && 
							a.getId().getRequestId() == req.getId().getRequestId()))
						continue;
					
					QoSMThingServiceStar ts = S.get(a.getId().getThingServiceId());
					QoSMThingStar t = getThing(ts.getDeviceId(), res.b);
					double upper = Cis.get(ts.getThingServiceId()) / Tis.get(ts.getThingServiceId());
					
					if((a.getTotalComputationalCost() + t.getCapacityUsed()) <= upper 
							&& (t.getBatteryLevel() - a.getTotalBatteryCost()) >= teta 
							&& (t.getTUsed() +  Tis.get(ts.getThingServiceId())) <= MRT.get(ts.getThingServiceId())){
						
						QoSMAssignmentStar ass = new QoSMAssignmentStar(req.getId().getServiceId(),
								req.getId().getRequestId(), ts.getThingServiceId(), a.getTotalBatteryCost(), 
								a.getTotalComputationalCost());

						AssignmentPlus assp = null;
						if(battery == true){
							assp = new AssignmentPlus(ass, t.getBatteryLevel() - ass.getTotalBatteryCost());
						}
						else{
							assp = new AssignmentPlus(ass,ass.getTotalBatteryCost());
						}
						Fj.add(assp);
					}
				}
				if(Fj.isEmpty()){
					res.feasible = false;
				}
				else{
					
					if(Fj.size() <= 1){
						d = INF;
						max = Fj.get(0).ass;
					}
					else{
						ma = maximum(Fj);
						max=ma[0];
						d = max.getTotalBatteryCost() - ma[1].getTotalBatteryCost();
					}
					if(d>ds){
						ds = d;
						chosen=max;
					}
				}
			}// end for
			if(res.feasible){
				QoSMThingServiceStar ts = S.get(chosen.getId().getThingServiceId());
				QoSMThingStar t = getThing(ts.getDeviceId(), res.b);
				res.y.add(new QoSMAssignmentStar(chosen));
				t.setBatteryLevel(t.getBatteryLevel() - chosen.getTotalBatteryCost());
				t.setCapacityUsed((t.getCapacityUsed() + chosen.getTotalComputationalCost()));
				t.setTUsed(t.getTUsed() + Tis.get(chosen.getId().getThingServiceId()));
				int num = t.getNumass();
				num++;
				t.setNumass(num);
				QoSMAssuredRequestStar del=null;
				for(QoSMAssuredRequestStar r : K)
				{
					if(r.getId().getServiceId().equals(chosen.getId().getServiceId()) && 
							r.getId().getRequestId() == chosen.getId().getRequestId())
						del = r;
				}
				K.remove(del);
			}

		}
		if(!battery && res.feasible)
		{
			QoSMAssignmentStar ip, min;
			
			for(int j=0;j<bckK.size();j++){
				ip = res.y.get(j);
				
				Fj.clear();
				QoSMAssuredRequestStar req = getRequestAssured(ip.getId().getServiceId(), ip.getId().getRequestId(), bckK);
				for(QoSMAssignmentStar a : P)
				{
					if(!(a.getId().getServiceId().equals(req.getId().getServiceId()) && 
							a.getId().getRequestId() == req.getId().getRequestId()))
						continue;
					QoSMThingServiceStar ts = S.get(a.getId().getThingServiceId());
					QoSMThingStar t = getThing(ts.getDeviceId(), res.b);
					
					double upper = Cis.get(ts.getThingServiceId()) / Tis.get(ts.getThingServiceId());
					
					if((a.getTotalComputationalCost() + t.getCapacityUsed()) < upper 
							&& (t.getBatteryLevel() - a.getTotalBatteryCost()) >= teta
							&& (t.getTUsed() -  Tis.get(ts.getThingServiceId())) <= MRT.get(ts.getThingServiceId())){
						if(!ip.equals(a)){
							QoSMAssignmentStar ass = new QoSMAssignmentStar(req.getId().getServiceId(),
									req.getId().getRequestId(), ts.getThingServiceId(), a.getTotalBatteryCost(), 
									a.getTotalComputationalCost());
							AssignmentPlus assp = null;
							assp = new AssignmentPlus(ass, t.getBatteryLevel() - ass.getTotalBatteryCost());
							Fj.add(assp);
						}
					}
				}
				
				
				if(!Fj.isEmpty()){
					min = minimum(Fj);
					if(min.getTotalBatteryCost() < ip.getTotalBatteryCost()){
						QoSMAssignmentStar del = null;
						for(QoSMAssignmentStar a : res.y)
						{
							if(a.equals(ip))
								del = a;
						}
						res.y.remove(del);
						res.y.add(new QoSMAssignmentStar(min));
						
						QoSMThingServiceStar ts = S.get(ip.getId().getThingServiceId());
						QoSMThingStar t = getThing(ts.getDeviceId(), res.b);
						
						t.setBatteryLevel(t.getBatteryLevel() + ip.getTotalBatteryCost());
						t.setCapacityUsed(t.getCapacityUsed() - ip.getTotalComputationalCost());
						t.setTUsed(t.getTUsed() - Tis.get(ip.getId().getThingServiceId()));
						
						ts = S.get(min.getId().getThingServiceId());
						QoSMThingStar t2 = getThing(ts.getDeviceId(), res.b);
						t2.setBatteryLevel(t2.getBatteryLevel() - min.getTotalBatteryCost());
						t2.setCapacityUsed(t2.getCapacityUsed() + min.getTotalComputationalCost());
						t2.setTUsed(t.getTUsed() + Tis.get(min.getId().getThingServiceId()));
						
						int num = t.getNumass();
						t.setNumass(num--);
						num = t2.getNumass();
						t2.setNumass(num++);
		
					}
				}
		
			}
	
		}
		if(res.feasible)
		{
			for(QoSMThingStar t : res.b)
			{
				QoSMThingStar origin = getThing(t.getDeviceId(), b2);
				t.setBatteryLevel(t.getBatteryLevel() * origin.getBatteryLevel());
			}
		}
		return res;
	}

	private QoSMAssuredRequestStar getRequestAssured(String serviceId,
			int requestId, ArrayList<QoSMAssuredRequestStar> k) {
		for(QoSMAssuredRequestStar r : k)
		{
			if(r.getId().getServiceId().equals(serviceId) && r.getId().getRequestId() == requestId)
				return r;
		}
		return null;
	}

	private static ArrayList<QoSMAssuredRequestStar> cloneRequestsAssured(
			List<QoSMAssuredRequestStar> k2) {
		ArrayList<QoSMAssuredRequestStar> clone = new ArrayList<QoSMAssuredRequestStar>(k2.size());
	    for(QoSMAssuredRequestStar item: k2) 
	    	clone.add(new QoSMAssuredRequestStar(item));
	    return clone;
	}
}


