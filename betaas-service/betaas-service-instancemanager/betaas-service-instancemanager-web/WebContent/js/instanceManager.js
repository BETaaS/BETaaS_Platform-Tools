var hostAddress="192.168.31.45";

var starIcon = "<span style=\"background-image: url(css/ui-icons-orange.png)\" style=\"background-image: url(css/ui-icons-orange.png)\" style=\"background-image: url(css/ui-icons-orange.png)\" class=\"ui-icon ui-icon-star\"></span>";

function initPage() {
	timer = window.setInterval(function(){ getPresenceData(); }, 
			                   2000);
}

function requestInstanceInfo() {
  clearUI();	
	xmlhttpGet("http://"+hostAddress+":8080/InstanceManager/rest/instanceInfo", manageAjaxRespInstanceInfo);
}

function xmlhttpGet(strURL, manageFunc) {
    var xmlhttp;
    
    if (window.XMLHttpRequest) {
    	// code for IE7+, Firefox, Chrome, Opera, Safari
    	xmlhttp=new XMLHttpRequest();
    } else {
    	// code for IE6, IE5
    	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState==4 && xmlhttp.status==200) {
        	manageFunc(xmlhttp.responseText);
        }
    };
    xmlhttp.open('GET', strURL, true);
    xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xmlhttp.send(""); // request parameters go here
}

function manageAjaxRespInstanceInfo(str) {
    //alert(str);

    if ((str.length >= 6) && (str.substr(0, 6) == "error:")) {
		  alert("Cannot get info: " + str.substr(6));
		  return;
		}
		
    var resp = eval('(' + str + ')');
    if (resp == null) {
			alert("Cannot get the instance info from BETaaS");
			return;
		}
    
    updateUI(resp);
}

function manageAjaxRespJoin(str) {
	alert(str);
	document.location.reload(true);
	
}

function manageAjaxRespDisjoin(str) {
	alert(str);
	document.location.reload(true);
}

function zpad(str) {
  if (str == null) return "00";
  var res = "";
  for (var i=0; i < 2-str.length; i++) {
    res += '0';
	}
	res += str;
	
	return res;
}

function join(gwid) {
  xmlhttpGet("http://"+hostAddress+":8080/InstanceManager/rest/join?gwId=" + gwid, manageAjaxRespJoin);
}


function disjoin() {
  xmlhttpGet("http://"+hostAddress+":8080/InstanceManager/rest/disjoin", manageAjaxRespDisjoin);
}

function clearUI() {
	document.getElementById('subtitle').innerHTML = "N/A";
	// Admin GW
	document.getElementById('adminGWID').innerHTML = "N/A";
  document.getElementById('adminGWType').innerHTML = "N/A";
	document.getElementById('adminGWDescr').innerHTML = "N/A";
	
	// Instance
  document.getElementById('instanceheading').innerHTML = "No info available";
  document.getElementById('disjoinbutton').style.display = "none";
  document.getElementById('gwlist').innerHTML = "";
	
	// Visible Instances
	  document.getElementById('visibleinstancesheading').innerHTML = 
		         "<span class=\"inline\" style=\"text-align: center\">Info not available"	  
}

function updateUI(info) {
		
	if (info.instanceInfo == null) {
	  alert("Unexpected response from BETaaS");
	  clearUI();
	  return;
	}
	
	document.getElementById('subtitle').innerHTML = info.instanceInfo.gwid;
	
	// Admin GW
	document.getElementById('adminGWID').innerHTML = info.instanceInfo.gwid;
	
	if (info.instanceInfo.gwtype == "star") {
	  document.getElementById('adminGWType').innerHTML = starIcon;
	} else {
	  document.getElementById('adminGWType').innerHTML = info.instanceInfo.gwtype;
	}
	
	document.getElementById('adminGWDescr').innerHTML = info.instanceInfo.adminGWDescr = info.instanceInfo.gwdescription;
	
	// Instance
	if ((!info.instanceInfo) || (info.instanceInfo.instanceid == null) || (info.instanceInfo.length == 0)) {
	  document.getElementById('instanceheading').innerHTML = "This GW does not belongs to any instance";
	  document.getElementById('disjoinbutton').style.display = "none";
	  document.getElementById('gwlist').innerHTML = "";
	  
	} else {
	  document.getElementById('instanceheading').innerHTML = "This GW belongs to the instance: <span style=\"color: #f1cc4b; font-weight: bold\">I-" + info.instanceInfo.instanceid + "</span>";
	  
		if (info.instanceInfo.instanceid == info.instanceInfo.gwid) {
			document.getElementById('disjoinbutton').style.display = "none";
		} else {
			document.getElementById('disjoinbutton').style.display = "inline";
		}
	  
	  if (info.instanceInfo.gwlist != null) {
	    tableContent = "";
		  for (var i=0; i < info.instanceInfo.gwlist.length; i++) {
			  // 2 elements per line
							 
		    if (i % 2 == 0) {
		      // new line
		      tableContent += "<tr>";
				}
				
				tableContent += 
							"<td width=\"50%\">" +		  
								"<table>" +	      
									"<tr>" +	      
									 "<td><a href=\"" + info.instanceInfo.gwlist[i].address + "\"><img title=\"Go to admin page\" src=\"img/gw.png\" style=\"width: 50px\"/></a></td>" +		     
									 "<td>" +			
											"<span class=\"inline\">ID: " + info.instanceInfo.gwlist[i].gwid + " &nbsp;</span>" +
											"<a class=\"inline\" href=\"" + info.instanceInfo.gwlist[i].address + "\">" +
												"<span title=\"Go to admin page\" style=\"background-image: url(css/ui-icons-lightOrange.png)\" class=\"inline ui-icon ui-icon-wrench\"></span></a>" +			
											"<br><p class=\"inline\">" + info.instanceInfo.gwlist[i].description + "</p>" +
									 "</td>" +	          
									"</tr>" +		   		
								"</table>" +	
							"</td>";

		    if (i % 2 == 1) {
		      // end line
		      tableContent += "</tr>";
				}      
      }
      if (info.instanceInfo.gwlist.length % 2 != 0) {
         // complete the line
			   tableContent += "<td></td></tr>";
			}
      
      document.getElementById('gwlist').innerHTML = tableContent;
		}
	}
	
	// Visible Instances
	if (info.instanceInfo.gwstarlist == null) {
	  document.getElementById('visibleinstancesheading').innerHTML = 
		         "<span class=\"inline\" style=\"text-align: center\">No GW"+
						 "</span>"+
						 "<span style=\"background-image: url(css/ui-icons-orange.png)\" class=\"inline ui-icon ui-icon-star\">" +
						 "</span> found"	  
	} else {
	  document.getElementById('visibleinstancesheading').innerHTML = 
		         "<span class=\"inline\" style=\"text-align: center\">These are the visible BETaaS Instances managed by a GW"+
						 "</span>"+
						 "<span style=\"background-image: url(css/ui-icons-orange.png)\" class=\"inline ui-icon ui-icon-star\">" +
						 "</span>:";	  
	  
    tableContent = "";
    canJoinSomeInstance = false;
    for (var i=0; i < info.instanceInfo.gwstarlist.length; i++) {
    	tableContent += 
						"<tr style=\"vertical-align: middle\">" +
						  "<td>" +
								"<img src=\"img/network.png\" style=\"width: 70px\"/></td>" +
							"<td>" +
								"<span class=\"inline\">Gateway</span>" +
								"<span style=\"background-image: url(css/ui-icons-orange.png)\" class=\"inline ui-icon ui-icon-star\">" +
								"</span>" +		
								"<span style=\"color: #f1cc4b; font-weight: bold\">" + info.instanceInfo.gwstarlist[i].gwid +
								"</span></td>" +		
							"<td style=\"padding-left: 30px\">";
			if (info.instanceInfo.gwstarlist[i].gwid == info.instanceInfo.gwid) {
			    tableContent += "It is this gateway instance";
			} else if ((!info.instanceInfo) || (info.instanceInfo.instanceid == null) || (info.instanceInfo.length == 0)) {
			    // Currently not joined to any instance
			    canJoinSomeInstance = true;
					tableContent += "<button onclick=\"join('" + info.instanceInfo.gwstarlist[i].gwid + "')\");\" title=\"Join this GW instance\" class=\"join-button ui-state-default ui-corner-all\" style=\"color: black; background: #f1cc4b;\">Join</button>";
		  }
			tableContent += "</td>" +	     
						"</tr>";	  
    }
	  document.getElementById('gwstartable').innerHTML = tableContent;

    if ((info.instanceInfo) && (info.instanceInfo.instanceid != null)) {
       document.getElementById('visibleinstancesfooter').innerHTML = "In order to join existing instances, disjoin from the current one (Instance tab)";
    }
	}
}

