'use strict';

/**
 * @ngdoc function
 * @name betaasadminuiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the betaasadminuiApp
 */
 
angular.module('betaasadminuiApp')
  .controller('MainCtrl', function ($scope,$http) {
    
    $scope.info = {};
	
	$scope.init = function () {
		$http.get('http://localhost:1337/instanceinfo/').
		success(function(data) {
			console.log('got '+data);
			$scope.info = data;
		});
	}

	$scope.init();	
	
	
  });
