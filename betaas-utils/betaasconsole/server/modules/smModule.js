var durl = 'localhost';
var dport = 8181;
var pathlist = "/cxf/sm/application";
var pathstart = "/cxf/sm/application/start";
var pathstop = "/cxf/sm/application/stop";
durl = 'http://'+durl;
var rest = require('restler');

module.exports = {
    getApplications: function (res) {
        console.log('app list  ');
        getApplications(res);
    },
    startApplication: function (res,id) {
        console.log('app start  '+id);
        startApplication(res,id);
    },
    stopApplication: function (res,id) {
        console.log('app stop  '+id);
        stopApplication(res,id);
    },
    defaultValues: function (url, port) {
        console.log('returning array  ');
        durl = 'http://'+url;
        dport = port;
    }
};

function getApplications(res) {
    console.log('path will be '+durl+':'+dport + pathlist);
    rest.get(durl+':'+dport + pathlist+"/").
    on('complete', function (data, response) {
        resetarray();
        console.log(data);
        var responsenew = {};
        console.log(response.statusCode);

        if (typeof data.InstalledApplications === 'undefined'){
            console.log('p');
           
        }else {
             if (typeof data.InstalledApplications.Application === 'undefined'){ console.log('no application currently installed'); } else {
                  for (var i = 0;i<data.InstalledApplications.Application.length;i++){
                      var application = {};
                      application.name = data.InstalledApplications.Application[i].Name[0];
                      application.id = data.InstalledApplications.Application[i].ID[0];
                      application.extended = data.InstalledApplications.Application[i].IsExtended[0];
                      
                      console.log(application);
                      appJsonArray.applications.push(application);
                  }
             }
        }
        console.log(appJsonArray);
        res.send(appJsonArray);            

    });
}

function startApplication(res,id) {
    console.log('start '+durl+':'+dport + pathstart+"/"+id);
    rest.put(durl+':'+dport + pathstart+"/"+id).
    on('complete', function (data, response) {
        console.log(response.raw.toString());
        //console.log(response);
        res.send(response.raw.toString());

    });
}

function stopApplication(res,id) {
    console.log('stop '+durl+':'+dport + pathstop+"/"+id);
    
    rest.put(durl+':'+dport + pathstop+"/"+id).
    on('complete', function (data, response) {
        console.log(response.raw);
        
        console.log(response.statusCode);
        res.send(response.raw.toString());

    });
}

function resetarray(){
    appJsonArray = {
    "applications": []
    };
}

var appJsonArray = {
    "applications": []
};

