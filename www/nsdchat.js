var NsdChat = function() {};
NsdChat.prototype.initNsd = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "initNsd", []);
};
NsdChat.prototype.startDiscovery = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "startDiscovery", []);
};
NsdChat.prototype.stopDiscovery = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "stopDiscovery", []);
};
NsdChat.prototype.registerService = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "registerService", ['nsd-android-test', '8000']);
};
NsdChat.prototype.unRegisterService = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "unRegisterService", []);
};
NsdChat.prototype.stopNsd = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "NsdChatPlugin", "stopNsd", []);
};
  
var NsdChat = new NsdChat();  
module.exports = NsdChat;
