var exec = require('cordova/exec');


exports.createInterstitial = function(zoneId, success, error) {
    exec(success, error, "AdcashSDK", "createInterstitial", [zoneId]);
};

exports.loadInterstitial = function(success, error) {
    exec(success, error, "AdcashSDK", "loadInterstitial", [null]);
};

exports.showInterstitial = function(success, error) {
    exec(success, error, "AdcashSDK", "showInterstitial", [null]);
};


exports.createBanner = function(zoneId, success, error) {
    exec(success, error, "AdcashSDK", "createBanner", [zoneId]);
};

exports.loadBanner = function(success, error) {
    exec(success, error, "AdcashSDK", "loadBanner", [null]);
};
