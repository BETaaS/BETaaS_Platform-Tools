function initPage() {
	timer = window.setInterval(function(){ getPresenceData(); }, 
			                   2000);
}

function getPresenceData() {
	xmlhttpGet("http://localhost:8080/IntrusionDetection/rest/presence", manageAjaxRespPresence);
}

function runTask() {
	xmlhttpGet("http://localhost:8080/IntrusionDetection/rest/task", manageAjaxRespTask);
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

function manageAjaxRespTask(str) {
    var resp = eval('(' + str + ')');
    if (resp == null) {
		alert("Cannot get the result from BETaaS");
		return;
	}
    
    makeTable(resp);
}

function manageAjaxRespPresence(str) {
	var status = document.getElementById("lastStatus");
    var resp = eval('(' + str + ')');

    if (resp.mStartDate != null) {
      document.getElementById("startDate").innerHTML = resp.mStartDate;
    } else {
      document.getElementById("startDate").innerHTML = "Info not available";
    }
    
    //alert(resp.mLastStatus + " " + (resp.mLastStatus == 'true'));
    if (resp.mConnected != null && resp.mConnected) {
	    if (resp.mLastStatus.toString() == "true") {
	      status.innerHTML = "PRESENCE DETECTED";
	      status.style.color = '#FF5555';
	      document.getElementById("warning").style.visibility = 'visible';
	    } else {
	      status.innerHTML = "Presence Not Detected";
	      status.style.color = '#33A033';
	      document.getElementById("warning").style.visibility = 'hidden';
	    }
	    document.getElementById("lastDate").innerHTML = resp.mLastDate;
    } else {
    	status.innerHTML = "CANNOT RECEIVE DATA FROM BETaaS";
	    status.style.color = '#FF5555';
	    document.getElementById("lastDate").innerHTML = "-";
	    document.getElementById("warning").style.visibility = 'visible';
    }
    
    if (resp.mLastPresence != null) {
    	document.getElementById("lastPresence").innerHTML = resp.mLastPresence;
    } else {
    	document.getElementById("lastPresence").innerHTML = "Not available";
    }
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

function makeTable(taskResp) {
    var table;
    
    if (taskResp.res == null) {
    		alert("Unexpected response from BETaaS");
    		return;
		}
    
    table = "<table><tr><th style='width:20%;text-align:center'>Source ID</th><th style='width:40%;text-align:center'>Location</th><th style='width:40%;text-align:center'>Last Presence</th></tr>";
    for (var i=0; i<taskResp.res.length; i++) {
    	if ((taskResp.res[i].id == null) || (taskResp.res[i].location == null) || (taskResp.res[i].timestamp == null)) {
    		alert("Unexpected response from BETaaS");
    		return;
    	}
    	if (i%2 == 0) {
			  color = "background-color:#BBBBAA";
			} else {
			  color = "background-color:#DDDDCC";
			}
    	table += "<tr><td style='" + color + "'>" + taskResp.res[i].id + 
			        "</td><td style='" + color + "'> " + taskResp.res[i].location + 
							"</td><td style='text-align:center;" + color + "'>" + taskResp.res[i].timestamp  + "</td></tr>";
    }
    var now = new Date();
    var nowStr = now.getFullYear() + "-" 
                 + zpad((now.getMonth()+1).toString()) + "-"
								 + zpad(now.getDay().toString()) + " "  
                 + zpad(now.getHours().toString()) + ":"  
                 + zpad(now.getMinutes().toString()) + ":" 
                 + zpad(now.getSeconds().toString());
		table += "<tr><td id='tableFooter' colspan=3>Generated at " + nowStr + "</td></tr>";	
    table += "</table>";
    
    resDiv = document.getElementById("taskResult");
    resDiv.innerHTML = table;
}

