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
public class AppServicePK implements Serializable {
    protected String id;
    protected String app_id;

    public AppServicePK() {}

    public AppServicePK(String id, String app_id) {
        this.id = id;
        this.app_id = app_id;
    }
    
    private void setId(String id) {
        this.id = id;
    }
    
    private String getId() {
        return id;
    }
    
    private void setApp_id(String app_id) {
        this.app_id = app_id;
    }
    
    private String getApp_id() {
        return app_id;
    }
    
    
}
