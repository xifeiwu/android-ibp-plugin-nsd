var HelloCallback = function() {};  
  
HelloCallback.prototype.start = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "HelloCallback", "start", ["Hi"]);
};
HelloCallback.prototype.stop = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, "HelloCallback", "stop", []);
};  
  
var HelloCallback = new HelloCallback();  
module.exports = HelloCallback;
