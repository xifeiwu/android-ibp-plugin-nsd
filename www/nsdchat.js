var NsdChat = function() {};
NsdChat.prototype.initNsd = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "initNsd", []);
};
NsdChat.prototype.stopNsd = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "stopNsd", []);
};
NsdChat.prototype.startDiscovery = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "startDiscovery", []);
};
NsdChat.prototype.stopDiscovery = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "stopDiscovery", []);
};
NsdChat.prototype.registerService = function(successCallback, errorCallback, serviceInfo) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "registerService", serviceInfo);
};
NsdChat.prototype.unRegisterService = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "unRegisterService", []);
};
NsdChat.prototype.resolveService = function(successCallback, errorCallback, username) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "resolveService", [username]);
};
var NsdChat = new NsdChat();  
module.exports = NsdChat;

