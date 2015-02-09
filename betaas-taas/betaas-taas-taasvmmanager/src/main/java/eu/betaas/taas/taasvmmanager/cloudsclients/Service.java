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

package eu.betaas.taas.taasvmmanager.cloudsclients;

import java.util.LinkedList;
import java.util.List;


public class Service {
    
    private String id;
    private List<VMProperties> vms;
    
    public Service() {
        vms = new LinkedList<VMProperties>();
    }

    public Service(String id) {
        vms = new LinkedList<VMProperties>();
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<VMProperties> getVms() {
        return vms;
    }

    public void setVms(List<VMProperties> vms) {
        this.vms = vms;
    }
    
    public void addVM(VMProperties vm) {
        vms.add(vm);
    }
}
