angular.module('betaasadminuiApp')
  .controller("FooterCtrl", function($scope, $state, AppSettings) {
    
    console.log('i load this '+AppSettings.versionui);
    
    $scope.versionui = AppSettings.versionui;
    
    
    $scope.$watch('AppSettings', function (newVal, oldVal) {
      console.log('there is a change'+AppSettings.versionui);
        
    });
});