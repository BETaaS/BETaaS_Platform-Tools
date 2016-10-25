// Module for AMQP BETaaS management

var amqp = require('amqplib');
var message_vector=new Array();
var queue = 'betaas_UI_queue';
var amqpserver;
var topic;
var count = 0
module.exports = {
  initializeQueue: function () {

	   amqp.connect('amqp://'+amqpserver).then(function(conn) {
	    process.once('SIGINT', function() { conn.close(); });
	    return conn.createChannel().then(function(ch) {
	    	
	    	
	        var ok = ch.assertExchange('betaas_bus', 'topic', {durable: false});
	        
	        ok = ok.then(function() {
	            return ch.assertQueue(queue, {exclusive: false});
	          });
	        
	          ok = ok.then(function(qok) {
	            return ch.bindQueue(qok.queue, 'betaas_bus', topic).then(function() {
	              return qok.queue;
	            });
	          });
	          ok = ok.then(function(queue) {
	            return ch.consume(queue, parseMessage, {noAck: true});
	          });
	          

	          function parseMessage(msg) {
                  console.log('checking messages'+msg.content.toString());
                  
	        	  var date = new Date();
             
                     var isvalid = true;
                    try {
                        var jsondata = JSON.parse(msg.content);
                    } catch (e) {
                        console.log(msg.content + ' discarded is not valid json message!');
                        isvalid = false;
                    }


                  
                if (isvalid){
                    //console.log(" [x] Received "+ jsondata.layer+" "+jsondata.level+" "+jsondata.timestamp+" "+jsondata.descritpion+" "+date);
                    message_vector[count]={'date' : date , 'layer' : jsondata.layer, 'level' : jsondata.level.toUpperCase(), 'timestamp' : jsondata.timestamp, 'description' : jsondata.descritpion, 'origin' : jsondata.origin.toUpperCase()};
                                      console.log(" Generated "+message_vector[count]);
		          count=count+1;
                     }

	          }	      
	      return ok.then(function(_consumeOk) {
	        console.log(' [*] Waiting for messages. To exit press CTRL+C');
	      });
	    });
	  }).then(null, console.warn);
  },
  getMessages: function (res) {
      
      
    return message_vector;
  },
  connectionOptions: function (server) {
	  amqpserver=server;
  },
  useTopic: function (queuetopic) {
	  topic=queuetopic;
  }
};

