angular.module('betaasadminuiApp')
    .controller('ServiceCtrl', function ($scope, $http, $modal, AppSettings, instanceFactory, manageExtendedServices, runExtendedServices) {

        var backendurl = AppSettings.backend;
        $scope.services = {};

        $scope.currentservice;
        $scope.selectedgw = instanceFactory.getGW();
        $scope.selectedtaskstatus = 'Not Installed';
        $scope.operation = 'Install';

        $scope.rowForService = function (service) {
            return service;
        };

        $scope.init = function () {
            manageExtendedServices.getExtendedServices().then(function (data) {
                console.log('i got ' + JSON.stringify(data));
                $scope.services = data;
            }, function (error) {
                console.log('something wrong');
            })
        };

 

        $scope.open = function () {

            var modalInstance = $modal.open({
                templateUrl: 'views/popup/createServicePopup.html',
                controller: 'AddServiceCtrl'
            });

            modalInstance.result.then(function (newItem) {
                // reload the service list
                console.log(newItem);
                $scope.init();

            }, function () {
                console.log('No extended service provided');
            });

        };

        $scope.removeService = function (service) {
            // remove and reload the list
            manageExtendedServices.removeExtendedService(service);
            $scope.init();
        };

        $scope.manageService = function (currservice) {
            $scope.currentservice = currservice;
            console.log('requesting service status' + currservice);
            console.log('open box');

            var modalInstance = $modal.open({
                templateUrl: 'views/popup/runServicePopup.html',
                controller: 'ExecutionServiceCtrl',
                resolve: {
                    service: function () {
                        return $scope.currentservice;
                    }
                }
            })

            modalInstance.result.then(function (newItem) {
                console.log(newItem);
                if (typeof newItem != 'undefined') {
                    console.log('Inserting a new extended service');
                    manageExtendedServices.addExtendedService(newItem);
                    $scope.init();
                } else {
                    console.log('No extended service provided');
                }

            }, function () {
                console.log('No extended service provided');
            })

        };    
    
    
        $scope.init();

    });


angular.module('betaasadminuiApp').controller('AddServiceCtrl', function ($scope, $modalInstance, manageExtendedServices) {

    $scope.newservice;

    $scope.ok = function (newservice) {
        console.log(newservice);
        if (typeof newservice != 'undefined') {
            console.log('Inserting a new extended service');
            manageExtendedServices.addExtendedService(newservice);
        } else {
            console.log('No extended service provided');
        }
        $modalInstance.close(newservice);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

});

angular.module('betaasadminuiApp').controller('ExecutionServiceCtrl', function ($scope, $modalInstance, service, runExtendedServices) {
    $scope.thiservice = service;
    $scope.requesting = false;
    $scope.selectedtaskstatus = 'Unknown';
    
    $scope.uninstall = function (service) {
        $scope.requesting = true;
        console.log('uninstall');
        $scope.selectedtaskstatus = runExtendedServices.uninstall(service)
        .then(function (data) {
                console.log('I checked and it is '+data);
                $scope.selectedtaskstatus = data;
                $scope.requesting = false;
            }, function (error) {
                // promise rejected, could log the error with: console.log('error', error);
                $scope.selectedtaskstatus = 'Unknown';
                $scope.requesting = false;
            });
        //$scope.checkstatus(service);

    };

    $scope.install = function (service) {
        console.log('install');
        $scope.requesting = true;
        $scope.selectedtaskstatus = runExtendedServices.install(service)
        .then(function (data) {
                console.log('I checked and it is '+data);
                $scope.selectedtaskstatus = data;
                $scope.requesting = false;
            }, function (error) {
                // promise rejected, could log the error with: console.log('error', error);
                $scope.selectedtaskstatus = 'Unknown';
                $scope.requesting = false;
            });
        //$scope.checkstatus(service);
    };
    
    $scope.start = function (service) {
        console.log('start');
        $scope.requesting = true;
        $scope.selectedtaskstatus = runExtendedServices.start(service)
        .then(function (data) {
                console.log('I checked and it is '+data);
                $scope.selectedtaskstatus = data;
                $scope.requesting = false;
            }, function (error) {
                // promise rejected, could log the error with: console.log('error', error);
                $scope.selectedtaskstatus = 'Unknown';
                $scope.requesting = false;
            });;
        //$scope.checkstatus(service);
    };
    
    $scope.stop = function (service) {
        console.log('stop');
        $scope.requesting = true;
        $scope.selectedtaskstatus = runExtendedServices.stop(service)
        .then(function (data) {
                console.log('I checked and it is '+data);
                $scope.selectedtaskstatus = data;
                $scope.requesting = false;
            }, function (error) {
                // promise rejected, could log the error with: console.log('error', error);
                $scope.selectedtaskstatus = 'Unknown';
                $scope.requesting = false;
            });
        //$scope.checkstatus(service);
    };

    $scope.checkstatus = function (service) {
        $scope.selectedtaskstatus = 'Checking';
        $scope.requesting = true;
        $scope.selectedtaskstatus = runExtendedServices.checkstatus(service)
            .then(function (data) {
                console.log('I checked and it is '+data);
                $scope.selectedtaskstatus = data;
                $scope.requesting = false;
            }, function (error) {
                // promise rejected, could log the error with: console.log('error', error);
                $scope.selectedtaskstatus = 'Unknown';
                $scope.requesting = false;
            });

    }

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.checkstatus(service);

});