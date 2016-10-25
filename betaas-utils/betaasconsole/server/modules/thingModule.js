var server = "";
var rest = require('restler');
var restserver = 'http://localhost:8181/cxf';

var getthingsUrl = '/ts/things/';
var addthingUrl = '/ts/thing/';
var deletethingUrl = '/ts/delete/';


var checkThingLocationUrl = '/taas/checkThingLocation/';
var checkThingTypeUrl = '/taas/checkThingType/';
var addTermUrl = '/taas/addTerm/';

module.exports = {
		  listThings: function (res) {

			  console.log(server);
				rest.get(restserver+getthingsUrl).
				    on('complete', function(data, response){
				        console.log('This is '+data);
				        console.log(response.statusCode);
				        res.send(data);
	
				 });
			
			  
		  },
          installThing: function (thing,res) {
              
			  console.log('GOT '+JSON.stringify(thing));
				rest.postJson(restserver+addthingUrl, thing).
				    on('complete', function(data, response){
				        console.log('GOT '+data);
				        console.log(response.statusCode);
				        res.send(data);
	
				 });
			  
		  },
          deleteThing: function (thingid,res) {

			  console.log(server);
				rest.del(restserver+deletethingUrl+thingid).
				    on('complete', function(data, response){
				        console.log(data);
				        console.log(response.statusCode);
				        res.send(data);
	
				 });
			
			  
		  },
         checkThingLocation: function (location,res) {

			  console.log(server);
				rest.get(restserver+checkThingLocationUrl+location).
				    on('complete', function(data, response){
				        console.log(data);
				        console.log(response.statusCode);
				        res.send(data);
	
				 });
			
			  
		  },
        checkThingType: function (type,isdigital,res) {

			  console.log(server);
				rest.get(restserver+checkThingTypeUrl+type+'/'+isdigital).
				    on('complete', function(data, response){
				        console.log(data);
				        console.log(response.statusCode);
				        res.send(data);
	
				 });
			
			  
		  },
          addTerm: function (type,id,status,res) {

			  console.log(server);
				rest.get(restserver+addTermUrl+type+'/'+id+'/'+status).
				    on('complete', function(data, response){
				        console.log(data);
				        console.log(response.statusCode);
				        res.send(data);
	
				 });
			
			  
		  },
		  initialize: function (serverurl) {
			  server = serverurl;	
		  }

};





