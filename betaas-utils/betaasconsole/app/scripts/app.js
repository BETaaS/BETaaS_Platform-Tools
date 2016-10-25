'use strict';

/**
 * @ngdoc overview
 * @name betaasadminuiApp
 * @description
 * # betaasadminuiApp
 *
 * Main module of the application.
 */


var app = angular
    .module('betaasadminuiApp', [
    'ui.router',
    'ui.bootstrap'
  ])
    .factory('AppSettings', function ($q,$http) {
        
        return {
            language: 'en',
            instancemanager: 'localhost:8080',
            monitoringamqp: '10.15.5.55:49155',
            backend: 'localhost:1337',
            sshaddress: 'localhost:8180',
            monitoring: 'off',
            amqptopic: 'betaas_queue',
            versionui: '3.0.2'
        }
    })
   
app.config(function ($stateProvider, $urlRouterProvider) {
	
	$urlRouterProvider.otherwise('/login')

    $stateProvider.state('login', {
        url: '/login',
        views: {
            header: {
                template: '<h1>Welcome to BETaaS UI</h1>'
            },
            content: {
                templateUrl: 'views/login.html',
                controller: 'LoginCtrl'
            }
        }
    })

    $stateProvider.state('instance', {
        url: '/instance',
        views: {
            header: {
                templateUrl: 'views/header.html'
            },
            content: {
                templateUrl: 'views/instance.html',
                controller: 'InstanceCtrl'
            },
            footer: {
                templateUrl: 'views/footer.html',
                controller: 'FooterCtrl'
            }
        }
    })

    $stateProvider.state('thing', {
        url: '/thing',
        views: {
            header: {
                templateUrl: 'views/header.html'
            },
            content: {
                templateUrl: 'views/thing.html',
                controller: 'ThingCtrl'
            },
            footer: {
                templateUrl: 'views/footer.html',
                controller: 'FooterCtrl'
            }
        }
    })

    $stateProvider.state('monitoring', {
        url: '/monitoring',
        views: {
            header: {
                templateUrl: 'views/header.html'
            },
            content: {
                templateUrl: 'views/monitoring.html',
                controller: 'MonitoringCtrl'
            },
            footer: {
                templateUrl: 'views/footer.html'
            }
        }
    })


    $stateProvider.state('service', {
        url: '/service',
        views: {
            header: {
                templateUrl: 'views/header.html'
            },
            content: {
                templateUrl: 'views/service.html',
                controller: 'ServiceCtrl'
            },
            footer: {
                templateUrl: 'views/footer.html',
                controller: 'FooterCtrl'
            }
        }
    })

    $stateProvider.state('task', {
        url: '/task',
        views: {
            header: {
                templateUrl: 'views/header.html'
            },
            content: {
                templateUrl: 'views/task.html',
                controller: 'TaskCtrl'
            },
            footer: {
                templateUrl: 'views/footer.html'
            }
        }
    })
    
    $stateProvider.state('application', {
        url: '/application',
        views: {
            header: {
                templateUrl: 'views/header.html'
            },
            content: {
                templateUrl: 'views/application.html',
                controller: 'ApplicationCtrl'
            },
            footer: {
                templateUrl: 'views/footer.html',
                controller: 'FooterCtrl'
            }
        }
    })
    
    $stateProvider.state('setting', {
        url: '/setting',
        views: {
            header: {
                templateUrl: 'views/header.html'
            },
            content: {
                templateUrl: 'views/setting.html',
                controller: 'SettingCtrl'
            },
            footer: {
                templateUrl: 'views/footer.html',
                controller: 'FooterCtrl'
            }
        }
    })

})


app.service('loginService', function ($q) {

    var _isLogged = false;

    this.loginUser = function (name, pw) {

        var deferred = $q.defer();
        var promise = deferred.promise;
        
        if (name == 'betaas' && pw == 'betaas') {
            
            _isLogged = true;
            deferred.resolve('Welcome ' + name + '!');
        } else {
            console.log('ko ya!');
            _isLogged = false;
            deferred.reject('Wrong credentials.');
        }
        promise.success = function (fn) {
            promise.then(fn);
            return promise;
        }
        promise.error = function (fn) {
            promise.then(null, fn);
            return promise;
        }
        return promise;
    }

    this.islogged = function () {
        console.log('verify login');
        return _isLogged;
    }

})

