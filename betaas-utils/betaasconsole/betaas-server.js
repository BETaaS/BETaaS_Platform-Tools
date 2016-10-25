/**
 * Main BETaaS Angular application script
 */
var config = require('./config/default.json');

var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var connected = false;
var connection;
var amqpserver = config.amqpserver;
var queue_topic = config.amqptopic;
var ads_ws = config.ads_ws;
var im_ws = config.instancemanager;
var versionui = config.versionui;



var count = 0;
// internal modules
var ampqModule = require('./server/modules/amqpModule');
var soapBDMModule = require('./server/modules/soapBDMModule');
var soapIMModule = require('./server/modules/soapIMModule');
var thingModule = require('./server/modules/thingModule');
var esModule = require('./server/modules/esModule');
var smModule = require('./server/modules/smModule');



app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});


app.use(express.static(__dirname + '/app')); // set the static files location /public/img will be /img for users

app.use(bodyParser.urlencoded({
    'extended': 'true'
})); // parse application/x-www-form-urlencoded

app.use(bodyParser.json()); // parse application/json

app.use(bodyParser.json({
    type: 'application/vnd.api+json'
})); // parse application/vnd.api+json as json

/**
 *
 * Handle interactions with the monitoring AMQP BETaaS message bus
 *
 */

app.get('/getmessage/', function (req, res) {
    res.contentType('application/json');
    var results = ampqModule.getMessages();
    var jsonanswer = results;
    
    
    
    console.log('this '+results);
    res.send(jsonanswer);
});

/**
 *
 * Handle extended services management
 *
 */

app.post('/installextservice/:group/:art/:vers', function (req, res) {
    
    console.log('connecting to install service');
    console.log('id ' + req.params.group+"/"+req.params.art+"/"+req.params.vers);
    esModule.installService(res,req.params.group+"/"+req.params.art+"/"+req.params.vers);
});

app.delete('/uninstallextservice/:group/:art/:vers', function (req, res) {
    res.contentType('application/json');
    console.log('connecting');
    console.log('id ' + req.params.group+"/"+req.params.art+"/"+req.params.vers);
    esModule.uninstallService(res,req.params.group+"/"+req.params.art+"/"+req.params.vers);
});

app.get('/getextservicestatus/:group/:art/:vers', function (req, res) {
    console.log('requesting status via shh');
    res.contentType('application/json');
    console.log('id ' + req.params.group+"/"+req.params.art+"/"+req.params.vers);
    esModule.checkService(res,req.params.group+"/"+req.params.art+"/"+req.params.vers);
    
});

app.get('/startextservice/:group/:art/:vers', function (req, res) {
    res.contentType('application/json');
    console.log('startapp');
    console.log('id ' + req.params.group+"/"+req.params.art+"/"+req.params.vers);
    esModule.startService(res,req.params.group+"/"+req.params.art+"/"+req.params.vers);
});

app.get('/stopextservice/:group/:art/:vers', function (req, res) {
    console.log('stopapp');
    res.contentType('application/json');
    console.log('id ' + req.params.group+"/"+req.params.art+"/"+req.params.vers);
    esModule.stopService(res,req.params.group+"/"+req.params.art+"/"+req.params.vers);
    
});

/**
 *
 * Allows to set the internal components configuration
 *
 */

app.get('/getextservicestatus/:group/:art/:vers', function (req, res) {
    console.log('requesting status via shh');
    res.contentType('application/json');
    console.log('id ' + req.params.group+"/"+req.params.art+"/"+req.params.vers);
    esModule.checkService(res,req.params.group+"/"+req.params.art+"/"+req.params.vers);
    
});

app.get('/getextservicestatus/:group/:art/:vers', function (req, res) {
    console.log('requesting status via shh');
    res.contentType('application/json');
    console.log('id ' + req.params.group+"/"+req.params.art+"/"+req.params.vers);
    esModule.checkService(res,req.params.group+"/"+req.params.art+"/"+req.params.vers);
    
});

app.get('/getextservicestatus/:group/:art/:vers', function (req, res) {
    console.log('requesting status via shh');
    res.contentType('application/json');
    console.log('id ' + req.params.group+"/"+req.params.art+"/"+req.params.vers);
    esModule.checkService(res,req.params.group+"/"+req.params.art+"/"+req.params.vers);
    
});

/**
 *
 * Handle extended services list
 *
 */

app.post('/addextservices/', function (req, res) {
    res.contentType('application/json');
    res.send(esModule.addService(req.body.name, req.body.url));
});

app.delete('/remextservices/:id', function (req, res) {
    res.contentType('application/json');
    res.send(esModule.remService(req.params.id));
});

app.get('/getextservices/', function (req, res) {
     console.log('id ' + req.params.group);
    res.contentType('application/json');
    res.send(esModule.listAvailableServices());
});

/**
 *
 * Handle task and their execution
 *
 */

app.get('/getBDMTasks/', function (req, res) {
    soapBDMModule.requestTaskListWsdl(res);
});

app.get('/runBDMTask/:id', function (req, res) {
    soapBDMModule.runTaskWsdl(req.params.id, res);
});

/**
 *
 * Handle things simulation
 *
 */

app.get('/things/', function (req, res) {
    console.log('Requested list of things');
    res.contentType('application/json');
    thingModule.listThings(res);

});

app.post('/addThing/', function (req, res) {
    console.log('A client requested to add a thing' + req.body);
    res.contentType('application/text');
    thingModule.installThing(req.body,res);
});

app.get('/deleteThing/:id', function (req, res) {
    console.log('A client requested to delete a thing' + req.params.id);
    thingModule.deleteThing(req.params.id,res);
});


/**
 *
 * Handle semantic info
 *
 */

app.get('/checkLocation/:location', function (req, res) {
    console.log('Requested checking this location'+req.params.location);
    res.contentType('application/json');
    thingModule.checkThingLocation(req.params.location,res);

});

app.get('/checkType/:type/:isdigital', function (req, res) {
    console.log('A client requested to check this type' + req.params.type+req.params.isdigital);
    res.contentType('application/text');
    thingModule.checkThingType(req.params.type,req.params.isdigital,res)
});

app.get('/addTerm/:location/:id/:status', function (req, res) {
    console.log('A client requested to add this term' + req.params.location+req.params.id+req.params.status);
    thingModule.addTerm(req.params.location,req.params.id,req.params.status,res);
});


/**
 *
 * Handle instance info
 *
 */

app.get('/instanceinfo/', function (req, res) {
    console.log('A client requested the instance info');
    res.contentType('application/json');
    var data = soapIMModule.getInstanceInfo(res);
});

/**
 *
 * Handle application
 *
 */

app.get('/stopapplication/:id', function (req, res) {
    console.log('Requested stop app');
    res.contentType('application/text');
    smModule.stopApplication(res,req.params.id);
});

app.get('/startapplication/:id', function (req, res) {
    console.log('A client requested to start app');
    res.contentType('application/text');
    smModule.startApplication(res,req.params.id);
});

app.get('/getapplications/', function (req, res) {
    console.log('A client requested list of app');
    res.contentType('application/json');
    smModule.getApplications(res);
});

/**
 *
 * provides a feedback to the UI that backend is working
 *
 */

app.get('/status/', function (req, res) {
    console.log('A client requested the status ok');
    res.contentType('application/json');
    res.send(JSON.stringify('{ready : true}'));
});

/**
 *
 * Allows to set the internal components configuration
 *
 */

app.post('/settings/', function (req, res) {
    // to be revisided, supposed to reconfigure the backend to current deployment environment
    if (!req.body) {
        res.send(config);
        console.log('nothing to save');
    } else {
        console.log('new settings' + amqpserver + ' ' + queue_topic);
        amqpserver = req.body.amqp;
        queue_topic = req.body.queue;
        
        ampqModule.connectionOptions(amqpserver);
        ampqModule.useTopic(queue_topic);
        res.send('Saved with ' + amqpserver + ' ' + queue_topic);
    }
});

app.get('/settings/', function (req, res) {
    console.log('retrieving configuration from the current default.json file');
   res.contentType('application/json');
    res.send(JSON.stringify(config));
});

app.get('*', function (req, res) {
    res.contentType('application/javascript');
    res.sendFile('index.html', {
        root: __dirname + '/app'
    }); 
});

/**
 *
 * Auth fake for sake of POC purposes
 */

app.post('/authenticate', function (req, res) {
    //TODO validate req.body.username and req.body.password
    //if is invalid, return 401
    if (!(req.body.username === 'betaas' && req.body.password === 'betaas')) {
        res.send(401, 'Wrong user or password');
        return;
    }

});

/**
 *
 * Initialization of the internal components and backend server
 *
 */

var server = app.listen(config.port, config.ip,  function () {

    var host = server.address().address;
    var port = server.address().port;
    // initialize AMQP client to retrieve monitoring messages
    ampqModule.connectionOptions(amqpserver);
    ampqModule.useTopic(queue_topic);
    ampqModule.initializeQueue();
    // initialize the WS for Data Task
    soapBDMModule.initializeSoapEP(ads_ws);
    soapIMModule.initializeSoapEP(im_ws);
    // thing module
    //thingModule.initialize("http://private-c1224-dave41.apiary-mock.com");
    console.log('Example app listening at http://%s:%s', host, port)

});