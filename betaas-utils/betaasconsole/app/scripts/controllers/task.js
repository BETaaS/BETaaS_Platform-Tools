'use strict';

/**
 * @ngdoc function
 * @name betaasadminuiApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the betaasadminuiApp
 */
angular.module('betaasadminuiApp')
    .controller('TaskCtrl', function ($scope, $http, $modal, AppSettings) {
        var backend = AppSettings.backend;
        var postdata = '{"url" : "' + 'localhost' + '", "port" : "' + '18002' + '" }';
        $scope.currentTask = '';
        $scope.tasks = {};

        $scope.getList = function () {
            var httpRequest = $http.get('http://' + backend + '/getBDMTasks/').success(function (data, status) {
                console.log('it works'+data);
                $scope.tasks = data;
            });
        }



        $scope.runTask = function (task) {
            console.log('requested to open task list');
            $scope.currentTask = task;

            console.log('whyme');
            var modalInstance = $modal.open({
                templateUrl: 'views/popup/runTaskPopup.html',
                controller: 'RunTaskCtrl',
                resolve: {
                    task: function () {
                        return $scope.currentTask;
                    }
                }
            });

            modalInstance.result.then(function () {

                console.log('No extended service provided');



            }, function () {
                console.log('No extended service provided');
            });

        }


        $scope.rowForTask = function (task) {
            return task;
        };

        $scope.init = function () {
            var httpRequest = $http.get('http://' + backend + '/getBDMTasks/').success(function (data, status) {
                console.log('it works');
                $scope.tasks = data;
            });
        }

        $scope.init()
    });

angular.module('betaasadminuiApp').controller('RunTaskCtrl', function ($scope, $http, $modalInstance, AppSettings, task) {

    var backend = AppSettings.backend;
    $scope.result;

    $scope.currentTask = task;
    console.log()

    $scope.ok = function (newservice) {
        console.log(newservice);
        $modalInstance.close(newservice);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.runTaskNow = function (task) {

        var httpRequest = $http.get('http://' + backend + '/runBDMTask/' + task.task).success(function (data, status) {

            console.log('it has been run the requested task');
            $scope.result = 'I got this JSON ' + JSON.stringify(data, null, "    ");

        });

    }


});