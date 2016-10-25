'use strict';

/**
 * @ngdoc function
 * @name betaasadminuiApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the betaasadminuiApp
 */

angular.module('betaasadminuiApp')
    .controller('ThingCtrl', function ($scope, $http, AppSettings, $modal, manageThings) {

        var backendurl = AppSettings.backend;
        var thingListurl = '/things/';
        

        $scope.currentthing = '';
        $scope.things = {};

        $scope.getThings = function () {
            $scope.selectedtaskstatus = manageThings.getThings()
                .then(function (data) {
                    console.log('Data is ' + data);
                    $scope.things = data;

                }, function (error) {
                    $scope.things = {};
                });
        }

        $scope.deleteThingPanel = function (thing) {
            var modalInstance = $modal.open({
                templateUrl: 'views/popup/deleteThingPopup.html',
                controller: 'DeleteThingCtrl',
                resolve: {
                    thingtodelete: function () {
                        return thing;
                    }
                }
            });

            modalInstance.result.then(function (todeleteItem) {
                console.log(todeleteItem);
                 $modalInstance.dismiss('cancel');
                $scope.getThings();
            }, function () {
                console.log('No extended service provided');
                $scope.getThings();
            });
        }

        $scope.openThingPanel = function (thing) {

            var modalInstance = $modal.open({
                templateUrl: 'views/popup/createThingPopup.html',
                controller: 'CreateThingCtrl'
            });

            modalInstance.result.then(function (newItem) {
                console.log(newItem);
                 $modalInstance.dismiss('cancel');
                $scope.getThings();
            }, function () {
                console.log('No extended service provided');
                $scope.getThings();
            });

        }

        $scope.rowForThing = function (thing) {
            return thing;
        };

        $scope.getThings();

    });

angular.module('betaasadminuiApp').controller('CreateThingCtrl', function ($scope, $modalInstance, AppSettings, manageThings,manageContext) {

    var backendurl = AppSettings.backend;
    var thingAddurl = '/addThing/';
    $scope.thing = {};
    $scope.ischecked = "Check";
    $scope.contextLocation = {};
    $scope.contextType = {};

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.addThing = function (thingdata) {
        console.log('scope '+$scope.ischecked );
        if ($scope.ischecked  == "Register"){
            console.log('Should now register');
            
            if (thingdata.isdigital==undefined)thingdata.isdigital=false;
            if (thingdata.output==undefined)thingdata.output=false;
            if (thingdata.environment==undefined)thingdata.environment=false;
            
            
            $scope.selectedtaskstatus = manageThings.addThing(thingdata)
            .then(function (data) {
                console.log('response is is ' + data);
                 $modalInstance.dismiss('cancel');
            }, function (error) {

            });
        }else{
             $scope.verify(thingdata);
        }
        
    }

    $scope.verify = function (thingdata) {
        console.log('Now this change '+thingdata.locationKeyword + thingdata.isdigital + thingdata.type );
        $scope.ischecked  = "Checking..";
        $scope.isambiguouslocation = false;
        $scope.isambiguoustype = false;
        // add a check with the CM
        // check location
        
        console.log('This is thingdata '+thingdata.isdigital);
        console.log('is undefined?'+(thingdata.isdigital==undefined));
        if (thingdata.isdigital==undefined)thingdata.isdigital=false;

        manageContext.checkLocation(thingdata.locationKeyword).then(function (data) {
                console.log('ambiguous is ' + data.disambiguation);
                 if (!(data.disambiguation)){
                     $scope.contextLocation = {}
                     $scope.isambiguouslocation = false;
                     if (!($scope.isambiguoustype)) $scope.ischecked  = "Register";
                 } else {
                     $scope.ischecked  = "Check";
                     console.log('ambiguous is ' + data.synonyms);
                     $scope.contextLocation = data.synonyms;
                     $scope.isambiguouslocation = true;
                }
        }, function (error) {
               console.log('issue with the request to the CM');
        });
       
        // check thing
        
        manageContext.checkType(thingdata.type,thingdata.isdigital).then(function (data) {
                console.log('ambiguous is ' + data.disambiguation);
                 if (!(data.disambiguation)){
                     $scope.isambiguoustype = false;
                     $scope.contextType = {}
                     if (!($scope.isambiguouslocation)) $scope.ischecked  = "Register";
                 } else {
                     $scope.ischecked  = "Check";
                     console.log('ambiguous is ' + data.synonyms);
                     $scope.contextType = data.synonyms;
                     $scope.isambiguoustype = true;
                 }
        }, function (error) {
                console.log('issue with the request to the CM');
        });
        
    }
    
    $scope.disambiguateLocation = function (term,synet,definition) {
        console.log('got this'+synet+term+definition);
        if (term){
            manageContext.addTerm(term,synet,definition).then(function (data) {
                console.log('ambiguous is ' + data);
                if (data==true){
                   $scope.contextLocation = {}
                   $scope.isambiguouslocation = false;
                    if (!($scope.isambiguoustype)) $scope.ischecked  = "Register";
                }
            }, function (error) {
                    console.log('issue with the request to the CM');
            });
        } else {
            console.log('got invalid selection');
        }
    }
    
    $scope.disambiguateType = function (term,synet,definition) {
        console.log('got this'+synet+term+definition);
        if (term){
            manageContext.addTerm(term,synet,definition).then(function (data) {
                console.log('ambiguous is ' + data);
                if (data==true){
                    $scope.isambiguoustype = false;
                    $scope.contextType = {}
                     if (!($scope.isambiguouslocation)) $scope.ischecked  = "Register";
                }
            }, function (error) {
                    console.log('issue with the request to the CM');
            });
        } else {
            console.log('got invalid selection');
        }
    }
    
});

angular.module('betaasadminuiApp').controller('DeleteThingCtrl', function ($scope, $modalInstance, AppSettings, thingtodelete, manageThings) {
    

    $scope.thingtodelete = thingtodelete;

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.deletething = function (todeletething) {
        $scope.selectedtaskstatus = manageThings.deleteThing(todeletething.id)
            .then(function (data) {
                console.log('response is is ' + data);
                 $modalInstance.dismiss('cancel');
            }, function (error) {

            });
    }

   
    
});