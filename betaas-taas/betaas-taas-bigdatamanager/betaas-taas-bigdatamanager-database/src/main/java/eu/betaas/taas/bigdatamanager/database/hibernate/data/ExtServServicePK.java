/*
 Copyright 2014-2015 Hewlett-Packard Development Company, L.P.

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
package eu.betaas.taas.bigdatamanager.database.hibernate.data;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class ExtServServicePK implements Serializable {
    protected String id;
    protected String ext_serv_id;

    public ExtServServicePK() {}

    public ExtServServicePK(String id, String ext_serv_id) {
        this.id = id;
        this.ext_serv_id = ext_serv_id;
    }
    
    private void setId(String id) {
        this.id = id;
    }
    
    private String getId() {
        return id;
    }
    
    private void setExt_serv_id(String app_id) {
        this.ext_serv_id = app_id;
    }
    
    private String getExt_serv_id() {
        return ext_serv_id;
    }
    
    
}
