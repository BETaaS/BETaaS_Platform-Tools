'use strict';

/**
 * @ngdoc function
 * @name betaasadminuiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the betaasadminuiApp
 */

angular.module('betaasadminuiApp')
    .controller('InstanceCtrl', function ($scope, $http, AppSettings, instanceFactory) {

        $scope.info = {};

        $scope.id = {};

        $scope.gws = {};

        $scope.connected = false;

        var checkfunction = function () {

            var urlbe = 'http://' + AppSettings.backend + '/status/';
            console.log('contacting the backend on ' + urlbe);

            $http.get(urlbe).
            success(function (data) {
                console.log(data);
                $scope.connected = true;
            });

        }

        $scope.init = function () {
            instanceFactory.update()
                .then(function (data) {
                    console.log('i got ' + JSON.stringify(data));
                    $scope.info = data;
                    $scope.gws = data.list;
                    checkfunction();
                }, function (error) {
                    console.log('something wrong');
                })

        }

        $scope.check = checkfunction;

        $scope.join = function (gw) {
            console.log(gw);
        }

        $scope.disjoin = function (gw) {
            console.log(gw);
        }

        $scope.init();

        $scope.rowForGw = function (gw) {
            return gw;
        };

    });