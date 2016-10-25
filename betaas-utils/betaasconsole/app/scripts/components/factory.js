angular.module('betaasadminuiApp').factory('instanceFactory', function ($http, $q, AppSettings) {
    var service = {};
    var initialized = false;
    var _currentinstance = '';
    var _currentgw = '';
    var _currentdesc = '';
    var _currenttype = '';
    var _gwlist = {};
    var _url = 'http://' + AppSettings.backend + '/instanceinfo';
    service.getInstance = function () {
        return _currentinstance;
    }

    service.getGW = function () {
        return _currentgw;
    }

    service.getDescription = function () {
        return _currentdesc;
    }

    service.getType = function () {
        return _currenttype;
    }

    service.setCurrent = function (gwInfo) {
        _currentinstance = gwInfo.instance;
        _currentgw = gwInfo.gw;
        _currentdesc = gwInfo.description;
        _currenttype = gwInfo.type;
    }

    service.getCurrent = function () {
        var gwInfo = {};
        gwInfo.instance = _currentinstance;
        gwInfo.gw = _currentgw;
        gwInfo.description = _currentdesc;
        gwInfo.type = _currenttype;
        gwInfo.list = _gwlist;
        return gwInfo;
    }

    service.setUrl = function (url) {
        _url = url;
    }

    service.getInstanceData = function () {
        if (initialized = true) {
            console.log('data loaded no need to request again');
            return this.getCurrent();
        } else {
            this.update();
        }
    }

    service.update = function () {
        var deferred = $q.defer();
        $http.get(_url).
        success(function (data) {
            console.log('got ' + data);
            console.log('got ' + JSON.stringify(data));
            var gwInfo = {};
            gwInfo.instance = data.instanceInfo.instanceid;
            gwInfo.gw = data.instanceInfo.gwid;
            gwInfo.description = data.instanceInfo.gwdescription;
            gwInfo.type = data.instanceInfo.gwtype;
            gwInfo.list = data.instanceInfo.gwlist;
            _gwlist = data.gwlist;
            // we ensure to initialize the info also for this service
            service.setCurrent(gwInfo);
            initialized = true;
            // now provide the result
            deferred.resolve(gwInfo);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
    }
    return service;

})

angular.module('betaasadminuiApp').factory('manageExtendedServices', function ($http, $q, AppSettings) {
    // this factory handle the list of extended services
    var service = {};
    var _extendedservices = [];
    var _url = 'http://' + AppSettings.backend;
    var _getes = '/getextservices/';
    var _addes = '/addextservices/';
    var _remes = '/remextservices/';
    var _initialized = false;

    /**
     *
     * This methods manipulate the list of service available for installation
     *
     */

    service.addExtendedService = function (newservice) {
        console.log('added service' + newservice.name);
        var deferred = $q.defer();
        $http.post(_url + _addes, {
            name: newservice.name,
            url: newservice.url
        }).
        success(function (data) {
            console.log('got ' + data);
            deferred.resolve(data.extendedServices);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
    }

    service.removeExtendedService = function (service) {
        console.log('remove service' + service.name + ' ' + service.url);
        var deferred = $q.defer();
        $http.delete(_url + _remes + service.name).
        success(function (data) {
            console.log('got ' + data);
            deferred.resolve(data);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
    }

    service.getExtendedServices = function () {
        var deferred = $q.defer();
        $http.get(_url + _getes).
        success(function (data) {
            console.log('got ' + data.extendedServices);
            _extendedservices = data;
            deferred.resolve(data.extendedServices);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
    }

    return service;

})

angular.module('betaasadminuiApp').factory('runExtendedServices', function ($http, $q, AppSettings, instanceFactory) {
    // this factory handle the list of extended services
    var service = {};
    var _extendedservices = [];
    var _url = 'http://' + AppSettings.backend;
    var _inses = '/installextservice/';
    var _startes = '/startextservice/';
    var _stopes = '/stopextservice/';
    var _unies = '/uninstallextservice/';
    var _chkes = '/getextservicestatus/';
    var _initialized = false;

    /**
     *
     * This methods manipulate services over an instance of BETaaS
     *
     */
    service.uninstall = function (service) {
        console.log('uninstall');
        var postdata = '{"name" : "' + service.url + '", "host" : "' + service.url + '" }';

        var deferred = $q.defer();
        $http.delete(_url + _unies+service.url).
        success(function (data) {
            console.log('got from uninstall' + data.status);
            deferred.resolve(data.status);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        
    };

    service.install = function (service) {
        console.log('install');

        var postdata = '{"name" : "' + service.name + '", "host" : "' + service.url + '" }';

        var deferred = $q.defer();
        $http.post(_url + _inses+service.url).
        success(function (data) {
            console.log('got from install ' + data.status);
            deferred.resolve(data.status);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        

    };
    
    service.start = function (service) {
        console.log('start');

        var postdata = '{"name" : "' + service.name + '", "host" : "' + service.url + '" }';

        var deferred = $q.defer();
        $http.get(_url + _startes+service.url).
        success(function (data) {
            console.log('got from start ' + data.status);
            deferred.resolve(data.status);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        

    };
    
    service.stop = function (service) {
        console.log('stop');

        var postdata = '{"name" : "' + service.name + '", "host" : "' + service.url + '" }';

        var deferred = $q.defer();
        $http.get(_url + _stopes+service.url).
        success(function (data) {
            console.log('got from stop' + data.status);
            deferred.resolve(data.status);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        

    };

    service.checkstatus = function (service) {
        var postdata = '{"name" : "' + service.name + '", "host" : "' + service.url + '" }';
        console.log('requesting status' + postdata);
        var deferred = $q.defer();
        $http.get(_url + _chkes+service.url).
        success(function (data) {
            console.log('got ' + data.status);
            deferred.resolve(data.status);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
    }


    return service;

})
angular.module('betaasadminuiApp').factory('manageApplication', function ($http, $q, AppSettings, instanceFactory) {
    
    var service = {};
    var _url = 'http://' + AppSettings.backend;
    var _stopapp = '/stopapplication/';
    var _startapp = '/startapplication/';
    var _getapp = '/getapplications/';
    var _initialized = false;
    
    service.setUrl = function (url) {
        _url=url;
    }
    
    service.getApplications = function () {
       var deferred = $q.defer();
       $http.get(_url + _getapp).
        success(function (data) {
            console.log('I got these application' + JSON.stringify(data));
            deferred.resolve(data.applications);
           
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;   
    }
    
    service.startApplication = function (application) {
        var deferred = $q.defer();
        $http.get(_url + _startapp+application.id).
        success(function (data) {
            console.log('got ' + data);
            deferred.resolve(data);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        
    }
    
    
    service.stopApplication = function (application) {
        var deferred = $q.defer();
       $http.get(_url + _stopapp+application.id).
        success(function (data) {
            console.log('got ' + data);
            deferred.resolve(data);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        
    }
    
    return service

})

angular.module('betaasadminuiApp').factory('manageThings', function ($http, $q, AppSettings) {
    
    var service = {};
    var _url = 'http://' + AppSettings.backend;
    var _thingListUrl = '/things/';
    var _thingAddurl = '/addThing/';
    var _thingDeleteurl = '/deleteThing/';
   
    
    service.setUrl = function (url) {
        _url=url;
    }
    
    service.getThings = function () {
       var deferred = $q.defer();
       $http.get(_url + _thingListUrl).
        success(function (data) {
            console.log('I got these things' + JSON.stringify(data));
            deferred.resolve(data);
           
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;   
    }
    
    service.addThing = function (thing) {
        var deferred = $q.defer();
        $http.post(_url + _thingAddurl,thing).
        success(function (data) {
            console.log('got ' + data);
            deferred.resolve(data);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        
    }
    
    
    service.deleteThing = function (thingid) {
        var deferred = $q.defer();
       $http.get(_url + _thingDeleteurl+thingid).
        success(function (data) {
            console.log('got ' + data);
            deferred.resolve(data);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        
    }
    
    return service;

})

angular.module('betaasadminuiApp').factory('manageContext', function ($http, $q, AppSettings) {
    
    var service = {};
    var _url = 'http://' + AppSettings.backend;
    var _checkLocationUrl = '/checkLocation/';
    var _checkTypeUrl = '/checkType/';
    var _addTermUrl = '/addTerm/';
   
    
    service.setUrl = function (url) {
        _url=url;
    }
    
    service.checkLocation = function (location) {
       var deferred = $q.defer();
       $http.get(_url + _checkLocationUrl+location).
        success(function (data) {
            console.log('I got these things' + JSON.stringify(data));
            deferred.resolve(data);
           
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;   
    }
    
    service.checkType = function (type,isdigital) {
        var deferred = $q.defer();
        $http.get(_url + _checkTypeUrl+type+'/'+isdigital).
        success(function (data) {
            console.log('I got these things' + JSON.stringify(data));
            deferred.resolve(data);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        
    }
    
    
    service.addTerm = function (location,termid,description) {
        var deferred = $q.defer();
       $http.get(_url + _addTermUrl+location+'/'+termid+'/'+description).
        success(function (data) {
            console.log('got ' + data);
            deferred.resolve(data);
        }).error(function () {
            deferred.reject('Error while getting instance data');
        })
        return deferred.promise;
        
    }
    
    return service;

})

angular.module('betaasadminuiApp').factory('manageQueue', function ($http, $q, AppSettings) {
    var service = {};
    var scheduler = AppSettings.monitoring;
    var backendurl = AppSettings.backend;
    var promiseReference;
    var messages = {};
    
    service.getPromise = function () {
        return promiseReference;
    }
    
    service.setPromise = function (thepromiseReference) {
        promiseReference = thepromiseReference;
    }
    
    return service;
})