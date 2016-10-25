/**

Copyright 2014 ATOS SPAIN S.A.

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors Contact:
Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atos.net
**/

package eu.betaas.service.securitymanager.trustmanager.servicetrustcalculator;

import org.apache.log4j.Logger;

public class OpinionModel 
{
	private double belief;
	private double disbelief;
	private double uncertainty;
	private double certainty;
	private double atomicity;
	private double expectation;
	private double positiveEvidences;
	private double negativeEvidences;	
	
	private Logger logger= Logger.getLogger("betaas.taas");
		
	public OpinionModel()
	{
		belief = 0.0;
		disbelief = 0.0;
		uncertainty =0.0;
		certainty = 1.0;
		atomicity = 0.5;
		expectation = 0.0;
		positiveEvidences = 0.0;
		negativeEvidences = 0.0;		
	}
		
	/**
	 * 
	 * @param r
	 * Positive evidence for the opinion model 
	 * @param s
	 * Negative evidence for the opinion model
	 * @param n
	 * Uncertain evidences for the opinion model
	 */
	public OpinionModel (double r , double s, double n)
	{
		uncertainty = n;
		certainty = 1.0f - uncertainty;
		belief = certainty * (r / (r + s));
		disbelief = certainty * (s / (r + s));		
		atomicity = 0.5f;
		expectation = belief + atomicity * uncertainty;	
		positiveEvidences = r;
		negativeEvidences = s;		
	}
	
	public OpinionModel (double nBelief, double nDisbelief, double nUncertainty, double nAtomicity)
	{
		belief = nBelief;
		disbelief = nDisbelief;
		uncertainty = nUncertainty;
		atomicity = nAtomicity;
		expectation = belief + atomicity * uncertainty;	
	}
		
	public double getBelief()
	{
		return this.belief ;
	}
	
	public double getDisbelief()
	{
		return this.disbelief ;
	}
	
	public double getUncertainty()
	{
		return this.uncertainty ;
	}
	
	public double getRelativeAtomicity()
	{
		return this.atomicity ;
	}
	
	public double getExpectation()
	{
		return this.expectation ;
	}

	public void setPositiveEvidences(double r)
	{
		positiveEvidences = r;
	}
	
	public void setNegativeEvidences(double s)
	{
		negativeEvidences = s;
	}
	
	public void setUncertainties(double n)
	{
		uncertainty = n;
		certainty = 1.0f - n;
	}
	
	public void setRelativeAtomicity(double relativeAtomicity )
	{
		this.atomicity = relativeAtomicity ;
	}
	
	public void calculateExpectation()
	{
		expectation = belief + atomicity * uncertainty ;
	}
	
	public void reCalculateModel ()
	{
		if (Double.isNaN(positiveEvidences) || Double.isNaN(negativeEvidences) || Double.isNaN(uncertainty))
		{
			return;
		}
		
		belief = certainty * (positiveEvidences / (positiveEvidences + negativeEvidences));
		disbelief = certainty * (negativeEvidences / (positiveEvidences + negativeEvidences));		
		expectation = belief + atomicity * uncertainty;	
	}
	
	public OpinionModel product (OpinionModel opinionB) 
	{
		logger.debug("Performing product calculation...");
		
		// Temporary variables from Opinion B
		double beliefB = opinionB.getBelief();
		double disbeliefB = opinionB.getDisbelief();
		double uncertaintyB = opinionB.getUncertainty();
		double atomicityB = opinionB.getRelativeAtomicity();
		
		// Calculate Belief
		double newBelief = belief * beliefB + ((1-atomicity) * atomicityB * belief * uncertaintyB + atomicity * (1-atomicityB) * uncertainty * beliefB) / (1-atomicity * atomicityB);
				
		// Calculate Disbelief
		double newDisbelief = disbelief + disbeliefB - disbelief * disbeliefB;
		
		// Calculate Uncertainty
		double newUncertainty = uncertainty * uncertaintyB + ((1-atomicityB) * belief * uncertaintyB + (1-atomicity) * uncertainty * beliefB) / (1-atomicity * atomicityB);
		
		// Calculate Atomicity
		double newAtomicity = atomicity * atomicityB;
		
		// Generate the Opinion
		return new OpinionModel (newBelief, newDisbelief, newUncertainty, newAtomicity); 
	}
	
}