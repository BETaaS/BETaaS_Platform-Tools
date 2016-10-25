angular.module('betaasadminuiApp')
  .controller('LoginCtrl', function ($scope,$http,loginService,$state,$rootScope,AppSettings){


 
    $scope.urlbackend = 'http://' + AppSettings.backend+'/settings';
    console.log('load settings from backend'+$scope.urlbackend);
               
	$scope.data = {};
 
    $scope.login = function(username,password) {
		    $scope.data.username = username;
			$scope.data.password = password;
			loginService.loginUser($scope.data.username, $scope.data.password).success(function(data) {
				$scope.message ='';
             
                        $state.go('instance');
               
                
			}).error(function(data) {
				$scope.message ='Login Failed, Please check Username and Password';
			});     

	 
    }
	
	$scope.message ='';

});

