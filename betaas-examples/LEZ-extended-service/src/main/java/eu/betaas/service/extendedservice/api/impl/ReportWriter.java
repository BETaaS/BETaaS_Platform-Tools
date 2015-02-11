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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Used to compose a report containing BDM task result on traffic
 * @author Intecs
 */
public class ReportWriter {

	public ReportWriter(String path) {
		mPath = path;
		mOut = null;
	}
	
	public String writeOutputHeader() throws Exception {
		GregorianCalendar now = new GregorianCalendar();
		now.setTime(new Date());
		String fileName = mPath + "/" + "TRAFFIC_REPORT_" + format(now) + ".html";
		
		mOut = new OutputStreamWriter(new FileOutputStream(fileName));
	    mOut.write(
	    		"<!DOCTYPE html>"+
	    		"<html lang=\"en-US\">"+
	    		"<head>"+
	    		  "<title>Traffic Data Analysis</title>"+
	    		  "<style>a.menu_html_tables{font-weight:bold;}</style>"+
	    		  "<style>"+
	    		"#smallnavContainer {display:none;}"+
	    		  "</style>"+
	    		  "<meta charset=\"utf-8\">"+
	    		  "<link rel=\"stylesheet\" type=\"text/css\" href=\"stdtheme.css\">"+
	    		  "<style>"+
	    		".htmltut_table, .htmltut_table th, .htmltut_table td"+
	    		"{"+
	    		"border:1px solid black;"+
	    		"}"+
	    		".htmltut_table1, .htmltut_table1 th, .htmltut_table1 td"+
	    		"{"+
	    		"border:1px solid black;"+
	    		"}"+
	    		".htmltut_table1 th, .htmltut_table1 td"+
	    		"{"+
	    		"padding:5px;"+
	    		"}"+
	    		  "</style>"+
	    		"</head>"+
	    		"<body style=\"background-repeat: no-repeat; background-image: url(data.png);\">"+
	    		"<div style=\"height: 70px;\"></div>"+
	    		"<h2 align=\"center\">Analysis on Traffic Data</h2>"+
	    		"<table class=\"reference\" style=\"width: 700px;\" align=\"center\">"+
	    		  "<tbody>"+
	    		    "<tr>"+
	    		      "<th>"+
	    		      "<p align=\"center\">Location</p>"+
	    		      "</th>"+
	    		      "<th>"+
	    		      "<p align=\"center\">Count<br>"+
	    		"[Cars/min]</p>"+
	    		      "</th>"+
	    		      "<th>"+
	    		      "<p align=\"center\">Min<br>"+
	    		"[Cars/min]</p>"+
	    		      "</th>"+
	    		      "<th>"+
	    		      "<p align=\"center\">Max<br>"+
	    		"[Cars/min]</p>"+
	    		      "</th>"+
	    		      "<th>"+
	    		      "<p align=\"center\">Time Peak</p>"+
	    		      "</th>"+
	    		      "<th>"+
	    		      "<p align=\"center\">Timestamp</p>"+
	    		      "</th>"+
	    		      "<th>"+
	    		      "<p align=\"center\">Last Value<br>"+
	    		"[Cars/min]</p>"+
	    		      "</th>"+
	    		      "<th>"+
	    		      "<p align=\"center\">Last Time</p>"+
	    		      "</th>"+
	    		    "</tr>");	  
              
      return fileName;  
	}
	
	public void close() throws Exception {
		if (mOut != null) mOut.close();
	}
	
	public void writeOutputRecord(String location_identifier, 
			                      String count, 
			                      String min, 
			                      String max, 
			                      String timepeak,
			                      String timestamp, 
			                      String lastvalue, 
			                      String lasttime) throws Exception {
		if (mOut == null) return; 
		mOut.write("<tr>"+
		            "<td>"+location_identifier+"</td>"+
		            "<td>"+count+"</td>"+
		            "<td>"+min+"</td>"+
		            "<td>"+max+"</td>"+
		            "<td>"+timepeak+"</td>"+
		            "<td>"+timestamp+"</td>"+
		            "<td>"+lastvalue+"</td>"+
		            "<td>"+lasttime+"</td>"+
		            "</tr>");
	}
	
	public void writeOutputFooter() throws Exception {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date());
		String now = format2(gc);
		if (mOut != null) {
      mOut.write(
				  "</tbody>"+
				  "</table>"+
				  "<br>"+
				  "<hr>"+
				  "<p align=\"right\">Generated at " + now + "</p>"+
				  "</body>"+
				  "</html>");
      mOut.close();
    }    
	}
	
	private String format(GregorianCalendar cal) {
		return cal.get(Calendar.YEAR) +
				String.format("%02d",(cal.get(Calendar.MONTH)+1)) +
				String.format("%02d",cal.get(Calendar.DAY_OF_MONTH));
//		        + "_" +
//				String.format("%02d",cal.get(Calendar.HOUR_OF_DAY)) + 
//				String.format("%02d",cal.get(Calendar.MINUTE)) + 
//				String.format("%02d",cal.get(Calendar.SECOND));
	}
  
  	private String format2(GregorianCalendar cal) {
		return cal.get(Calendar.YEAR) + "-" +
				String.format("%02d",(cal.get(Calendar.MONTH)+1)) + "-" +
				String.format("%02d",cal.get(Calendar.DAY_OF_MONTH)) + " " +
				String.format("%02d",cal.get(Calendar.HOUR_OF_DAY)) + ":" +
				String.format("%02d",cal.get(Calendar.MINUTE)) + ":" +
				String.format("%02d",cal.get(Calendar.SECOND));
	}
	
	private String mPath;
	private Writer mOut;
}
