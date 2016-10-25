var soap = require('soap');
var immurl = "http://localhost:9302/im-service?wsdl";
var args = {};
var datainfo = {};
module.exports = {
		initializeSoapEP: function (address) {
			immurl = "http://"+address+"/im-service?wsdl";
		},
		getInstanceInfo: function (res) {
			 console.log('A client requested the instance info');
			 
			 soap.createClient(immurl, function(err, client) {
				  client.getInstanceInfo(args, function(err, result) {
			          console.log(result.return);
					  datainfo = result.return;
			          res.send(datainfo);
			      });
			 });
		}
		
};



