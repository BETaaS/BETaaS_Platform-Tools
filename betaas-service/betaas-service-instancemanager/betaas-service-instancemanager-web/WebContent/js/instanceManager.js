var hostAddress="localhost";
var serverport="1337"
var app = angular.module('betaasApp', []);
var default_settings_vector = {'im': 'localhost:8080', 'amqp': '10.15.5.55:49155', 'microserver': 'localhost:1337', 'queue':'betaas_queue' ,'active': 'off'}
var settings_vector = default_settings_vector;
var starIcon = "<span style=\"background-image: url(css/ui-icons-orange.png)\" style=\"background-image: url(css/ui-icons-orange.png)\" style=\"background-image: url(css/ui-icons-orange.png)\" class=\"ui-icon ui-icon-star\"></span>";

function initPage() {
	timer = window.setInterval(function(){ getPresenceData(); }, 
			                   2000);
}

function requestInstanceInfo() {
  clearUI();	
	//xmlhttpGet("http://"+hostAddress+":8080/InstanceManager/rest/instanceInfo", manageAjaxRespInstanceInfo);
	jsonCall("http://"+settings_vector.im+"/InstanceManager/rest/instanceInfo");
	
	
}

function jsonCall(strURL){
	console.log('new call');
	$.ajax
	({
	  type: "GET",
	  url: strURL,
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
	  success: function (data, textStatus, xhr){
		console.log('new call done'+xhr.status+textStatus);
		if (xhr.status=='200'){
			updateUI(data);
	  	}
	  }
	});

}

function setupMicroserver(){
	var postdata = '{"amqp" : "'+settings_vector.amqp+'", "queue" : "'+settings_vector.queue+'" }';

	console.log(postdata);
	$.ajax
	({
	  type: "POST",
	  url: 'http://'+settings_vector.microserver+'/settings/',
	  data: postdata,
		headers: {
			'Content-Type': 'application/json'
		},
	  success: function (data, textStatus, xhr){
		console.log('I updated the settings'+xhr.status+textStatus);
		if (xhr.status=='200'){
			console.log('updated!');
	  	}
	  }
	});
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
  jsonCall("http://"+settings_vector.im+"/InstanceManager/rest/join?gwId=" + gwid);
}


function disjoin() {
  jsonCall("http://"+settings_vector.im+"/InstanceManager/rest/disjoin" );
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



function MonitoringManager($scope, $http) {

    $scope.messages = [];

    $scope.loadMessages = function() {
        var httpRequest = $http.get('http://'+settings_vector.microserver+'/getmessage/').success(function(data, status) {
        	console.log('it works');
            $scope.messages = data;
        });
    };
    
    $scope.rowForMessage = function(message){
        return message;
    };

}

function FormSettingsController($scope) {
	
  $scope.settings = settings_vector;
	
  $scope.update = function(settings) {
	  $scope.settings = angular.copy(settings);
	  console.log('Update requested '+$scope.settings.queue+$scope.settings.amqp);
	  settings_vector.queue=$scope.settings.queue;
	  settings_vector.amqp=$scope.settings.amqp;
	  setupMicroserver();
	  console.log($scope.settings );
  };
	
  $scope.reset = function() {
	  $scope.settings = angular.copy($scope.settings);
	  settings_vector = default_settings_vector;
	  console.log('restored defaults '  );
  };
	
  $scope.reset();
    

}

function ManageServices($scope) {
	
    

}

function ManageThings($scope) {
	
    

}

function ManageBDM($scope, $http) {
	
	//var adsurl = "http://localhost:18002/ads?wsdl";
	
	//$scope.settings = bdmtasks_vector;
	
	var postdata = '{"url" : "'+'localhost'+'", "port" : "'+'18002'+'" }';
	
	
	$scope.tasks = {};
	
	$scope.getList = function() {
        var httpRequest = $http.get('http://'+settings_vector.microserver+'/getBDMTasks/').success(function(data, status) {
        	console.log('it works');
        	$scope.tasks = data;
        	//alert($scope.tasks);
        });
//		var service = {
//		  url: 'http://localhost',
//		  port: '18002'
//		};
//		var serviceString = JSON.stringify(service);
//		var url = 'http://'+settings_vector.microserver+'/getBDMTasks/';
//		$.ajax
//		({
//		  type: "POST",
//		  url: url,
//		  data: postdata,
//			headers: {
//				'Content-Type': 'application/json'
//			},
//		  success: function (data, textStatus, xhr){
//			console.log('backend call done'+xhr.status+textStatus);
//			if (xhr.status=='200'){
//				$scope.tasks = data;
//				
//				console.log($scope.tasks);
//		  	}
//		  }
//		});
	}
	
	$scope.runTask = function(task) {
		alert(task);
		
		 var httpRequest = $http.get('http://'+settings_vector.microserver+'/runBDMTask/'+task.task).success(function(data, status) {
	        	console.log('it works');
	        });
		
	}
	
    
    $scope.rowForTask = function(task){
        return task;
    };

}

function checkBackend(){
	var urlbe = 'http://'+settings_vector.microserver+'/status/';
	console.log('new call');
	$.ajax
	({
	  type: "GET",
	  url: urlbe,
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
	  success: function (data, textStatus, xhr){
		console.log('backend call done'+xhr.status+textStatus);
		if (xhr.status=='200'){
			document.getElementById('backend').innerHTML = "On";
	  	}
	  }
	});
	
	
}


function updateUI(info) {
		
	if (info.instanceInfo == null) {
	  alert("Unexpected response from BETaaS");
	  clearUI();
	  return;
	}
	
	checkBackend();
	
	
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

