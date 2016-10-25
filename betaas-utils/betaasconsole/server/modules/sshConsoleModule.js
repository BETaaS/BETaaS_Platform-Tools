var duser = 'karaf';
var dpwd = 'karaf';
var dport = 8101;
var output;
var TheClient = require('ssh2').Client;
var TheConnection = new TheClient();


module.exports = {
    installService: function (ip, servicename, res) {
        console.log('intalling  ' + commandsJsonArray.install + servicename + ' on ' + ip);
        commandNInstall(commandsJsonArray.install + ip, res);
    },
    uninstallService: function (ip, servicename, res) {
        console.log('uninstalling  ' + servicename + ' on ' + ip);
        commandNUninstall(ip, res);
    },
    checkService: function (name, res) {
        console.log('checking ' + name);
        connectprepare();
        commandNCheck(commandsJsonArray.checkBundle + name, res);
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
    stopService: function (servicename) {
        console.log('stop  ' + servicename);
        stopservice(servicename);
        console.log('stopped');
        return esJsonArray;
    },
    startService: function (servicename) {
        console.log('start  ' + servicename);
        startservice(servicename);
        console.log('started');
        return esJsonArray;
    },
    installFeature: function (ip, servicedeploymenturl, res) {
        connectAndShell(ip, commandsJsonArray.feature + serviceid);
    },
    listAvailableServices: function () {
        return esJsonArray;
    },
    initConnection: function () {
        connectprepare();
    },
    defaultValues: function (port, user, pwd) {
        dport = port;
        duser = user;
        dpwd = pwd;
    }
};

function getserviceid(ip, command, res) {

    console.log('Connection now');
    //var Connection = require('ssh2'); // npm install ssh2
    gs = null;
    //var conn = new Connection();
    var out = '';
    TheConnection.on('ready', function () {
        console.log('Connection :: ready');
        conn.shell(function (err, stream) {
            if (err) {
                console.log(err);
                return;
            }
            console.log('Shellon');
            stream.on('close', function () {
                conn.end();
                if (out.search('Bundle ID') > 0) {
                    var pos = out.search('Bundle ID');
                    console.log('I found it as BUNDLE:' + out.substring(pos + 10, pos + 20));
                    connectAndUninstall(ip, out.substring(pos + 10, pos + 20), res);
                    return;
                } else {
                    connectAndUninstall(ip, "ko", res);
                }
            }).on('data', function (data) {
                if (!gs) gs = stream;
                if (gs._writableState.sync == false) process.stdout.write('' + data);
                out = out + data;
            }).stderr.on('data', function (data) {
                console.log('STDERR: ' + data);

            });
            stream.end(commandsJsonArray.install + command + '\n');
        });
    }).connect({
        host: ip,
        port: dport,
        username: duser,
        privateKey: require('fs').readFileSync('karaf.id_dsa')
            //password: dpwd
    });

}

function connectprepare() {




}

function commandNCheck(command, res) {


    TheConnection = new TheClient();

    TheConnection.on('ready', function () {

        TheConnection.shell(function (err, stream) {
            if (err) {
                console.log(err);
                console.log('SSH problem detected trying to fix it');
                return;
            }
            console.log('Shell is son');

            var out = '';
            stream.on('close', function () {
                console.log('Stream :: close');
                if (out.search('Active') > 0) {
                    res.send('{"status" : "Active"}');
                    return;
                }
                if (out.search('Installed') > 0) {
                    res.send('{"status" : "Installed"}');
                    return;
                }
                if (out.search('Resolved') > 0) {
                    res.send('{"status" : "Ready"}');
                    return;
                }
                res.send('{"status" : "Not installed"}');

            }).on('data', function (data) {
                //if (!gs) gs = stream;
                //if (gs._writableState.sync == false) process.stdout.write(''+data);
                out = data + out;

            }).stderr.on('data', function (data) {
                console.log('STDERR: ' + data);

            });
            stream.end(command + '\n logout \n');
        });
    });

    TheConnection.connect({
        host: '127.0.0.1',
        port: 8101,
        username: 'karaf',
        privateKey: require('fs').readFileSync('./server/config/karaf.id_dsa'),
        keepaliveInterval: 10000
    });

}


function commandNInstall(command, res) {
    console.log('command ' + command);

    TheConnection = new TheClient();

    TheConnection.on('ready', function () {
        console.log('Installing');
        TheConnection.shell(function (err, stream) {
            if (err) {
                console.log(err);
                console.log('SSH problem detected trying to fix it');
                return;
            }
            stream.on('close', function () {
                console.log('######## ' + output);

                if (res == null) {
                    console.log('res is null');
                    return;
                }
                console.log('Installed');
                res.send('{"status" : "Done"}');
                //stream.end(' logout \n');
            }).on('data', function (data) {
                //console.log('STDIN: ' + data);
                output = output + data;
            }).stderr.on('data', function (data) {
                console.log('ERR: ' + data);
            });
            stream.end(command + ' \n logout \n');

        });
    });

    TheConnection.connect({
        host: '127.0.0.1',
        port: 8101,
        username: 'karaf',
        privateKey: require('fs').readFileSync('./server/config/karaf.id_dsa'),
        keepaliveInterval: 10000
    });

}


function commandNUninstall(command, res) {
    console.log('running id search');
    var out = '';
    TheConnection = new TheClient();

    TheConnection.on('ready', function () {
        TheConnection.shell(function (err, stream) {
            if (err) {
                console.log(err);
                console.log('SSH problem detected trying to fix it');
            }
            console.log('Shellon');
            stream.on('close', function () {
                var id;
                if (out.search('Bundle ID') > 0) {
                    var pos = out.search('Bundle ID');
                    console.log('out ' + out);
                    console.log('I found it as BUNDLE:' + out.substring(pos + 10, pos + 20));
                    id = out.substring(pos + 10, pos + 20);
                    console.log('Found and now Unininstalling');
                    TheConnection.shell(function (err, stream) {
                        if (err) {
                            console.log('Cannot connect' + err);
                            return;
                        }
                        stream.on('close', function () {
                            console.log('removed');
                            res.send('{"status" : "Uninstalled"}');
                        }).on('data', function (data) {
                            output = output + data;
                        }).stderr.on('data', function (data) {
                            console.log('STDERR: ' + data);
                        });
                        stream.end(commandsJsonArray.uninstall + id + '\n logout \n');
                        console.log('getting the ID');
                    });
                } else {
                    id = "ko";
                    res.send('{"status" : "Uninstalled"}');
                    console.log('Not Installed');
                    return;
                }
            }).on('data', function (data) {
                out = out + data;
            }).stderr.on('data', function (data) {
                console.log('STDERR: ' + data);
            });
            stream.end(commandsJsonArray.install + command + '\n ');
        });

    });

    TheConnection.connect({
        host: '127.0.0.1',
        port: 8101,
        username: 'karaf',
        privateKey: require('fs').readFileSync('./server/config/karaf.id_dsa'),
        keepaliveInterval: 10000
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

var esJsonArray = {
    "extendedServices": [
        {
            "name": "LEZ",
            "url": "eu.betaas/LEZ-extended-service/3.0.0-SNAPSHOT"
        },
        {
            "name": "Another-extended-service",
            "url": "eu.betaas/another-extended-service/3.0.0-SNAPSHOT"
        }]
};

var commandsJsonArray = {
    "install": "install -s mvn:",
    "uninstall": "uninstall ",
    "checkBundle": "list | grep ",
    "feature": "features:install "
};