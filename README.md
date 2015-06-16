# AdCash Plugin #

### Quick Demo ###

```bash
# install cordova CLI
[sudo] npm install cordova -g

# install a small utility to run all the commands for you
[sudo] npm install plugin-verify -g

# run adcash demo with sample index.html
plugin-verify cordova-plugin-adcash
```

### Show Banner ad with single line of javascript code ###

1. Step 1: Create zone Id for your banner and interstitial, in [AdCash portal](http://www.adcash.com/), then use it in step 2 in your code.

2. Step 2: Want a banner? single line of javascript code.

```javascript
// it will display smart banner at the bottom, using the default options
var AdcashSDK = cordova.plugins.AdcashSDK;

AdcashSDK.createBanner({zoneId: 'ADD_YOUR_BANNER_ZONE_ID_HERE', 
						position: AdcashSDK.AD_POSITION.BOTTOM, 
						autoShow: true});
```

Step 3: Want full screen Ad? Easy, 2 lines of code. 

```javascript
var AdcashSDK = cordova.plugins.AdcashSDK;

// prepare and load ad resource in background, e.g. at begining of game level
AdcashSDK.prepareInterstitial({zoneId:'ADD_YOUR_INTERSTITIAL_ZONE_ID_HERE',
							   autoShow:true} );

// Alternatively, you can set prepare the interstitial
// and show it manually at some later point of time:
AdcashSDK.prepareInterstitial({zoneId:'ADD_YOUR_INTERSTITIAL_ZONE_ID_HERE',
							   autoShow:false},
							   function() {
									// Succeeded, we can now show the interstitial
									AdcashSDK.showInterstitial();
								},
								function(error){
									// Failed to prepare interstitial
								}
```

### Features ###

Platforms supported:

- Android
- iOS

## How to use? ##

> Note: `cordova-plugin-adcash` is available both in [PlugReg.com](http://plugreg.com/plugin/adcash/cordova-plugin-adcash) and [npmjs.com](https://www.npmjs.com/package/cordova-plugin-adcash) plugin registries.

* If use with Cordova CLI:

```bash
cordova plugin add cordova-plugin-adcash
```

* If use with PhoneGap Build, just configure in config.xml:
```javascript
<gap:plugin name="cordova-plugin-adcash" source="npm"/>
```

## Quick start with cordova CLI ##
```bash
	# create a demo project
    cordova create test1 com.example.test1 Test1
    cd test1
    cordova platform add android
    cordova platform add ios

    # now add the plugin
    cordova plugin add cordova-plugin-adcash

    # now remove the default www content, copy the demo html file to www/
    rm -r www/*;
    cp plugins/cordova-plugin-adcash/test/* www/;

	# now build and run the demo in your device or emulator
    cordova prepare; 
    cordova run android; 
    cordova run ios;
    # or import into Xcode / Android Studio
```

## Javascript API Overview ##

Methods:

```javascript
// use banner
createBanner(zoneId/options, success, fail);
loadBanner( success, fail)
removeBanner(success, fail);
showBanner(success, fail);
hideBanner(success, fail);
// use interstitial
prepareInterstitial(zoneId/options, success, fail);
showInterstitial(success, fail);
// set default value for other methods
setOptions(options, success, fail);
// conversion tracking
reportAppOpen(success, fail)
```
Use `setOptions` to set values for the following variables: 

| Option |          Type | Description |
|:---------:|:-------------------:|-------------------|
|`adSize`| enum | Size of the banner. Look at `AdcashSDK.AD_SIZE` for possible values.|
|`position`| enum | Position of the banner. Look at `AdcashSDK.AD_POSITION` for possible values.|
|`autoShow`| boolean| Whether the banner/interstitial should be shown automatically when loaded.|
|`zoneId`| string | Use in options ONLY when passed to `createBanner` or `prepareInterstitial`!!! Otherwise it will be ignored.|

> Note: Take a look at `AdcashSDK.js` for full API usage.