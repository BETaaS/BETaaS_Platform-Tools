angular.module('betaasadminuiApp')
.controller('MonitoringCtrl', function ($scope, $http, $interval, manageQueue, AppSettings) {

    var backendurl = AppSettings.backend;
    var reloading = AppSettings.monitoring;
    var reference;
    $scope.messages = [];
    $scope.searchText = '';
    $scope.itemsPerPage = 10;
    $scope.currentPage = 0;
    
    console.log('reload '+reloading);

    $scope.loadMessages = function () {
        var httpRequest = $http.get('http://' + backendurl + '/getmessage/').success(function (data, status) {
            console.log('data is '+JSON.stringify(data));
            $scope.messages = data;
            
        });
    };

    var httpRequest = $http.get('http://' + backendurl + '/getmessage/').success(function (data, status) {
        
        $scope.messages = data;
    });
        
    if (reloading=='on'){  
        console.log('schedule now');
        reference = $interval($scope.loadMessages, 5000);
        manageQueue.setPromise(reference);
        
    }else{
        
        console.log('configured manual reloading');
        if (angular.isDefined(manageQueue.getPromise())) {
            console.log('removing schedule');
            $interval.cancel(manageQueue.getPromise());
            stop = undefined;
          }
       
    }
    

    $scope.rowForMessage = function (message) {
        return message;
    };
    
     $scope.prevPage = function() {
    if ($scope.currentPage > 0) {
      $scope.currentPage--;
    }
  };

  $scope.prevPageDisabled = function() {
    return $scope.currentPage === 0 ? "disabled" : "";
  };

  $scope.pageCount = function() {
    return Math.ceil($scope.messages.length/$scope.itemsPerPage)-1;
  };

  $scope.nextPage = function() {
    if ($scope.currentPage < $scope.pageCount()) {
      $scope.currentPage++;
    }
  };

  $scope.nextPageDisabled = function() {
    return $scope.currentPage === $scope.pageCount() ? "disabled" : "";
  };

});

app.filter('offset', function() {
  return function(input, start) {
    start = parseInt(start, 10);
    return input.slice(start);
  };
});