var NSD = function() {};
NSD.prototype.initNsd = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NSDPlugin", "initNsd", []);
};
NSD.prototype.stopNsd = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NSDPlugin", "stopNsd", []);
};
NSD.prototype.startDiscovery = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NSDPlugin", "startDiscovery", []);
};
NSD.prototype.stopDiscovery = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NSDPlugin", "stopDiscovery", []);
};
NSD.prototype.registerService = function(successCallback, errorCallback, serviceInfo) {
    cordova.exec(successCallback, errorCallback, "NSDPlugin", "registerService", serviceInfo);
};
NSD.prototype.unRegisterService = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NSDPlugin", "unRegisterService", []);
};
NSD.prototype.resolveService = function(successCallback, errorCallback, username) {
    cordova.exec(successCallback, errorCallback, "NSDPlugin", "resolveService", [username]);
};
var NSDModule = new NSD();  
module.exports = NSDModule;

