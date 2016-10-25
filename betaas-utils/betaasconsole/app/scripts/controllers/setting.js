'use strict';

angular.module('betaasadminuiApp')
  .controller('SettingCtrl', function ($scope, $http, AppSettings) {
	
	  $scope.settings = AppSettings;
		
	  $scope.update = function(settings) {
		  $scope.settings = angular.copy(settings);
		  console.log('Update requested '+$scope.settings.amqptopic+$scope.settings.monitoringamqp);
		  AppSettings = $scope.settings;

	  };
		
	  $scope.reset = function() {
          
          AppSettings.amqptopic='betaas_queue';
          AppSettings.backend = 'localhost:1337';
          AppSettings.instancemanager='localhost:9302';
          AppSettings.monitoringamqp = '10.15.5.55:49155';
          AppSettings.sshaddress = 'localhost:8180';
          AppSettings.monitoring = 'off';
		  $scope.settings = angular.copy(AppSettings);
		  console.log('restored defaults '  );
	  };
		

});

