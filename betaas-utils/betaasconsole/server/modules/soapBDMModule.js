var soap = require('soap');
var adsurl = "http://localhost:18002/ads?wsdl";
var args = {};

module.exports = {
		initializeSoapEP: function (address) {
			adsurl = "http://"+address+"/ads?wsdl";
		},
		requestTaskListWsdl: function (res) {
			 console.log('A client requested the list of tasks');
			 soap.createClient(adsurl, function(err, client) {
				 client.getTaskList(args, function(err, result) {
			          var element;
			          var response = [];
			          for (element in result.return.TaskInfo){
			        	  response.push({task: result.return.TaskInfo[element].taskname, description: result.return.TaskInfo[element].description});
			          }
			          console.log('This I got from the BDM '+response);
			          res.send(response);	  
			      });
			  });
		},
		runTaskWsdl: function (taskName,res) {
			console.log(taskName);
			args = {arg0: taskName};
			soap.createClient(adsurl, function(err, client) {
				 client.taskData1(args, function(err, result) {
			         console.log(result.return);
			         res.send(result.return);
			      });
			  });
		}
		
};



