/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

module.exports = function ($rootScope, $scope) {
    $scope.applications = $rootScope.applications;

    $scope.remove = function (application) {
        application.$remove();
    };

    $scope.order = {
        column: 'name',
        descending: false
    };

    $scope.orderBy = function (column) {
        if (column === $scope.order.column) {
            $scope.order.descending = !$scope.order.descending;
        } else {
            $scope.order.column = column;
            $scope.order.descending = false;
        }
    };

    $scope.orderByCssClass = function (column) {
        if (column === $scope.order.column) {
            return 'sorted-' + ($scope.order.descending ? 'descending' : 'ascending');
        } else {
            return '';
        }
    };

    /*Disables Monitoring on application*/
    $scope.disableMonitoring = function(application){
        application.disableMonitoring()
            .success(function (response) {
                $scope.loadData();
            })
            .error(function (error) {
                $scope.error = error;
            });
    };

    /*Enables Monitoring on application*/
    $scope.enableMonitoring = function(application){
        application.enableMonitoring()
            .success(function (response) {
                $scope.loadData();
            })
            .error(function (error) {
                $scope.error = error;
            });
    };

    $scope.isMonitoringDisabled = function(application){
        if(application.statusInfo.status === "MONITORING-DISABLED"){
            return true;
        }else{
            return false;
        }
    };

    $scope.isUp = function(application){
        if(application.statusInfo.status === "UP"){
            return true;
        }else{
            return false;
        }
    }
};
