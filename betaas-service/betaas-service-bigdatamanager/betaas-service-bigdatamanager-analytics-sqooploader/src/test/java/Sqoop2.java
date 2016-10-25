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
import java.util.List;

import org.apache.sqoop.client.SqoopClient;
import org.apache.sqoop.model.MConnection;
import org.apache.sqoop.model.MConnectionForms;
import org.apache.sqoop.model.MConnector;
import org.apache.sqoop.validation.Status;
import org.junit.Test;



public class Sqoop2 {

	 @Test
	 public void runSqoop2(){
		 
		 SqoopClient client = new SqoopClient("http://betaashadoop:12000/sqoop/");
		 
		MConnection newCon = client.newConnection(1);
		//client.createConnection(1);
		 MConnectionForms conForms = newCon.getConnectorPart();
		 MConnectionForms frameworkForms = newCon.getFrameworkPart();
		//Set connection forms the service interface

			frameworkForms.getIntegerInput("security.maxConnections").setValue(0);
			
			Status status  = client.createConnection(newCon);
	 }
	
}
