var amqp = require('amqplib');

amqp.connect('amqp://10.15.5.55:49155').then(function(conn) {
  process.once('SIGINT', function() { conn.close(); });
  return conn.createChannel().then(function(ch) {
    
    var ok = ch.assertQueue('betaas_queue', {durable: true});
    
    ok = ok.then(function(_qok) {
      return ch.consume('betaas_queue', function(msg) {
        console.log(" [x] Received '%s'", msg.content.toString());
      }, {noAck: true});
    });
    
    return ok.then(function(_consumeOk) {
      console.log(' [*] Waiting for messages. To exit press CTRL+C');
    });
  });
}).then(null, console.warn);