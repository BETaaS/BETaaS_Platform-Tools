var durl = 'localhost';
var dport = 8181;
var pathstatus = "/cxf/es/status";
var pathinstall = "/cxf/es/install";
var pathuninstall = "/cxf/es/uninstall";
var pathstart = "/cxf/es/start";
var pathstop = "/cxf/es/stop";
durl = 'http://'+durl;
var rest = require('restler');


module.exports = {
    installService: function (res,url) {
        console.log('intalling  '  + url);
        installService(res,url);
    },
    uninstallService: function (res,url) {
        console.log('uninstalling  ' + url );
        uninstallService(res,url);
    },
    checkService: function (res, url) {
        console.log('checking ' + url);
        checkService(res,url);
    },
    addService: function (servicename, url) {
        console.log('adding  ' + servicename + ' with url ' + url);
        var newServiceItem = {};
        newServiceItem.name = servicename;
        newServiceItem.url = url;
        esJsonArray.extendedServices.push(newServiceItem);
        console.log('added');
        return esJsonArray;
    },
    remService: function (servicename) {
        console.log('removing  ' + servicename);
        removeservice(servicename);
        console.log('removed');
        return esJsonArray;
    },
    startService: function (res,url) {
        console.log('starting  ' );
        startService(res,url);
        console.log('started');
        
    },
    stopService: function (res,url) {
        console.log('stopping  ' );
        stopService(res,url);
        console.log('stopped');
        
    },
    listAvailableServices: function () {
        return esJsonArray;
    },
    defaultValues: function (url, port) {
        console.log('returning array  ');
        durl = 'http://'+url;
        dport = port;
    }
};

function checkService(res,url) {
    console.log('path will be '+durl+':'+dport + pathstatus+"/"+url);
    rest.get(durl+':'+dport + pathstatus+"/"+url).
    on('complete', function (data, response) {
        console.log(data);
        console.log(response.statusCode);
        res.send(data);

    });
}

function uninstallService(res,url) {
rest.del(durl+':'+dport + pathuninstall+"/"+url).
    on('complete', function (data, response) {
        console.log(data);
        console.log(response.statusCode);
        res.send(data);

    });
}

function installService(res,url) {
rest.post(durl+':'+dport + pathinstall+"/"+url).
    on('complete', function (data, response) {
        console.log(data);
        console.log(response.statusCode);
        res.send(data);

    });
}

function removeservice(searchedname) {
    console.log('Going to check for ' + esJsonArray.extendedServices.length);
    for (i = 0; i < esJsonArray.extendedServices.length; i++) {
        if (esJsonArray.extendedServices[i].name == searchedname) {
            esJsonArray.extendedServices.splice(i, 1);
            console.log('removed');
        }
    }
}

function startService(res,url) {
   rest.get(durl+':'+dport + pathstart+"/"+url).
    on('complete', function (data, response) {
        console.log(data);
        console.log(response.statusCode);
        res.send(data);

    });
}

function stopService(res,url) {
    rest.get(durl+':'+dport + pathstop+"/"+url).
    on('complete', function (data, response) {
        console.log(data);
        console.log(response.statusCode);
        res.send(data);

    });
}

var esJsonArray = {
    "extendedServices": [
        {
            "name": "LEZ",
            "url": "eu.betaas/LEZ-extended-service/3.0.3-SNAPSHOT"
        },
        {
            "name": "parking-extended-service",
            "url": "eu.betaas/parking-extended-service/3.0.3-SNAPSHOT"
        },
        {
            "name": "city-parking-extended-service",
            "url": "eu.betaas/city-parking-extended-service/3.0.3-SNAPSHOT"
        }]
};
