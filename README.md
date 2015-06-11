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

### Show Mobile Ad with single line of javascript code ###

Step 1: Create zone Id for your banner and interstitial, in [AdCash portal](http://www.adcash.com/), then use it in step 2 in your code.

Step 2: Want a banner? single line of javascript code.

```javascript
// it will display smart banner at top center, using the default options
var AdcashSDK = cordova.plugins.AdcashSDK;

if(AdcashSDK) AdcashSDK.createBanner( {
	zoneId: 'ADD_YOUR_BANNER_ZONE_ID_HERE', 
	position: POSITION_BOTTOM, 
	autoShow: true } );
```

Step 3: Want full screen Ad? Easy, 2 lines of code. 

```javascript
var AdcashSDK = cordova.plugins.AdcashSDK;

// prepare and load ad resource in background, e.g. at begining of game level
if(AdcashSDK) AdcashSDK.prepareInterstitial( {zoneId:'ADD_YOUR_INTERSTITIAL_ZONE_ID_HERE', autoShow:false} );

// show the interstitial later, e.g. at end of game level
if(AdcashSDK) AdcashSDK.showInterstitial();
```

Or, you can just copy this [adcash_simple.js](https://github.com/adcash/cordova-adcash/master/test/adcash_simple.js) to your project, and ref in your index.html.

### Features ###

Platforms supported:
- [x] Android
- [x] iOS

## How to use? ##

Notice: 
* Cordova team announce that the plugin registry is being migrated to npm. Get it using this name: cordova-plugin-adcash

* If use with Cordova CLI:
```bash
cordova plugin add cordova-plugin-adcash
```

* If use with PhoneGap Buid, just configure in config.xml:
```javascript
<gap:plugin name="cordova-plugin-adcash" source="npm"/>
```

## Quick start with cordova CLI ##
```bash
	# create a demo project
    cordova create test1 com.adcash.test1 Test1
    cd test1
    cordova platform add android
    cordova platform add ios

    # now add the plugin, cordova CLI will handle dependency automatically
    cordova plugin add cordova-plugin-adcash

    # now remove the default www content, copy the demo html file to www
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
Use setOptions to set values for the following variables: 
autoShow (boolean) - if true then shows the banner/interstitial when it is loaded, 
zoneId (string) - used to set zone id for interstitial or banner, 
position (integer) - used to set banner's position
