/**

Copyright 2013 ATOS SPAIN S.A. 

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.betaas.taas.taasvmmanager.api.datamodel;

public class VMRequest
{
	
	//occi.compute
    private String architecture;
    private double speed;
    private long memory;
    private int cores;
    
    //optimis.occi
    private String image;
    private int instances;

    public VMRequest() {}
    
    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }
    
    /**
     * compute architecture of the VM instances (i.e. "x86")
     */
    public static final String OCCI_COMPUTE_ARCHITECTURE = "occi.compute.architecture";

    /**
     * the individual CPU speed for the VM instances (i.e. "1.33")
     */
    public static final String OCCI_COMPUTE_SPEED = "occi.compute.speed";

    /**
     * the required individual memory of each compute instance (i.e. "2.0")
     */
    public static final String OCCI_COMPUTE_MEMORY = "occi.compute.memory";

    /**
     * the individual CPU cores of each VM instance (i.e. "2")
     */
    public static final String OCCI_COMPUTE_CORES = "occi.compute.cores";
    
    /**
     * the status of the instance
     */
    public static final String OCCI_COMPUTE_STATUS = "occi.compute.state";
    
    /**
     * the IP address of the virtual machine
     */
    public static final String OCCI_COMPUTE_HOSTNAME = "occi.compute.hostname";

    /**
     * the URI of the VM image to start for each instance (i.e.
     * http://datamanager.optimis.eu/vm#e3ac-a34l-1234)
     */
    public static final String OPTIMIS_VM_IMAGE = "optimis.occi.optimis_compute.image";
    
    /**
     * the number of VM instances to provide for this component (i.e. "4")
     */
    public static final String OPTIMIS_SERVICE_ID = "optimis.occi.optimis_compute.service_id";

	@Override
	public String toString() {
		return cores + " " + memory;
	}
    
    public static VMRequest valueOf (String value) {
    	VMRequest ret;
    	String[] values = value.split(" ");
    	Integer parsedCores = Integer.valueOf(values[0]);
    	Long parsedMemory = Long.valueOf(values[1]);
    	
    	ret = new VMRequest();
    	ret.setCores(parsedCores);
    	ret.setMemory(parsedMemory);
    	
    	return ret;
    }
}
