angular.module('betaasadminuiApp')
  .controller("MenuCtrl", function($scope, $state) {
  $scope.menuClass = function(page) {
    var current = $state.current.name;
    return page === current ? "active" : "";
  };
});