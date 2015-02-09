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
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;

import org.apache.hadoop.fs.Path;
import org.junit.Test;




public class Hive {


	 @Test
	 public void runHiveTest(){
			Configuration conf = new Configuration();
			conf.set("fs.default.name", "hdfs://betaashadoop:8020/output/");
			Path path = new Path("hdfs://betaashadoop:8020/output/");
			
			FileSystem hdfs;
			try {
				hdfs = FileSystem.get(conf);
				ContentSummary cs = hdfs.getContentSummary(path);
				System.out.println("DIRS "+cs.getDirectoryCount());
			
		            
		            FileStatus[] status = hdfs.listStatus(path);
		            

		            for (int i=0;i<status.length;i++){
		            	System.out.println(i+"file path" + status[i].getPath());
		            	
		            	
		            	
		            	
		            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
	 }
	
}
