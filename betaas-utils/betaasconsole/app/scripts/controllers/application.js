'use strict';

/**
 * @ngdoc function
 * @name betaasadminuiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the betaasadminuiApp
 */


angular.module('betaasadminuiApp')
    .controller('ApplicationCtrl', function ($scope, $http, $modal, AppSettings, instanceFactory, manageApplication) {

        $scope.applications = {};

        $scope.rowForApplication = function (application) {
            return application;
        };

        $scope.init = function () {
            manageApplication.getApplications().then(function (data) {
                console.log('i got ' + JSON.stringify(data));
                $scope.applications = data;
            }, function (error) {
                console.log('something wrong');
            });

        };

       $scope.manageApplication = function (application) {
            $scope.currentapp = application;

            console.log('open box');

            var modalInstance = $modal.open({
                templateUrl: 'views/popup/applicatioManagePopup.html',
                controller: 'AppMgrServiceCtrl',
                resolve: {
                    application: function () {
                        return $scope.currentapp;
                    }
                }
            })

            modalInstance.result.then(function (newItem) {
                console.log(newItem);
                

            }, function () {
                console.log('No extended service provided');
            })

        };     

        $scope.init();

    });

angular.module('betaasadminuiApp').controller('AppMgrServiceCtrl', function ($scope, $modalInstance, application,manageApplication) {
    $scope.thisapp = application;
    $scope.type = "Application";
    $scope.requesting = false;
    $scope.selectedtaskstatus = 'Unknown';
        
    $scope.stop = function (application) {
        console.log('request to stop');
        manageApplication.stopApplication(application).then(function (data) {
            console.log('i got ' + JSON.stringify(data));
            $scope.applications = data;
        }, function (error) {
            console.log('something wrong');
        });
    };

    $scope.start = function (application) {
         console.log('request to start');
        manageApplication.startApplication(application).then(function (data) {
            console.log('i got ' + JSON.stringify(data));
            $scope.applications = data;
        }, function (error) {
            console.log('something wrong');
        });
    };  

    $scope.checkstatus = function (application) {
        if (application.extended == "true") $scope.type = "Extended Service";
        $scope.selectedtaskstatus = 'Checking';
        $scope.requesting = true;
    }

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

});

