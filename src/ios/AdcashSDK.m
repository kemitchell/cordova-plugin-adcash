/********* AdcashSDK.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import <Foundation/Foundation.h>
#import <AdcashSDK/AdcashSDK.h>

static NSString * const OPT_AUTO_SHOW = @"autoShow";
static NSString * const OPT_AD_SIZE = @"adSize";
static NSString * const OPT_AD_POSITION = @"position";
static NSString * const OPT_AD_EXTRA = @"adExtra";
static NSString * const OPT_ZONE_ID = @"zoneId";


typedef NS_ENUM(NSUInteger, ACBannerPosition) {
    ACBannerPositionTop = 0,
    ACBannerPositionBottom
};

@interface AdcashSDK : CDVPlugin <ACBannerViewDelegate, ACInterstitialDelegate>
@property (nonatomic, assign) ACAdSize adSize;
@property (nonatomic, retain) NSDictionary* adExtras;

@property (nonatomic, copy) NSDictionary * bannerOpts;
@property (nonatomic, copy) NSDictionary * interstitialOpts;

@property (nonatomic, strong) ACBannerView * banner;
@property (nonatomic, strong) ACInterstitial * interstitial;
@property (nonatomic, copy) NSString * bannerCallbackId;
@property (nonatomic, copy) NSString * interstitialCallbackId;
@end

@implementation AdcashSDK

- (ACAdSize) adSize
{
    return [self.bannerOpts[OPT_AD_SIZE] unsignedIntegerValue];
}

- (ACBannerPosition) adPosition
{
    return [self.bannerOpts[OPT_AD_POSITION] unsignedIntegerValue];
}

- (BOOL) autoShow
{
    return [self.bannerOpts[OPT_AUTO_SHOW] boolValue];
}

#pragma mark - Defaults

- (NSDictionary *) defaultBannerOpts
{
    return @{OPT_AD_SIZE: @(ACAdSizeSmartBanner),
             OPT_AD_POSITION: @(ACBannerPositionBottom),
             OPT_AUTO_SHOW: @YES,
             OPT_AD_EXTRA: @{}};
}

- (NSDictionary *) defaultInterstitialOpts
{
    return @{OPT_AUTO_SHOW: @YES,
             OPT_AD_EXTRA: @{}};
}

#pragma mark -

- (NSDictionary *)bannerOpts
{
    if (!_bannerOpts) {
        self.bannerOpts = [self defaultBannerOpts];
    }
    return _bannerOpts;
}

- (NSDictionary *)interstitialOpts
{
    if (!_interstitialOpts) {
        self.interstitialOpts = [self defaultInterstitialOpts];
    }
    return _interstitialOpts;
}

- (BOOL) __mergeOptionsWithOptions:(NSDictionary *)options error:(NSError **)error;
{
    BOOL success = [self __validateOptions:options error:error];

    NSMutableDictionary *newOpts = [self.bannerOpts mutableCopy];
    [newOpts addEntriesFromDictionary:options];
    // Zone id must not be in opts dict
    [newOpts removeObjectForKey:OPT_ZONE_ID];

    self.bannerOpts = newOpts;
    return success;
}

- (BOOL) __mergeInterstitialOptionsWithOptions:(NSDictionary *)options error:(NSError **)error
{
    BOOL success = [self __validateOptions:options error:error];

    NSMutableDictionary *newOpts = [self.interstitialOpts mutableCopy];
    if (options[OPT_AUTO_SHOW]) {
        newOpts[OPT_AUTO_SHOW] = options[OPT_AUTO_SHOW];
    }
    if (options[OPT_AD_EXTRA]) {
        newOpts[OPT_AD_EXTRA] = options[OPT_AD_EXTRA];
    }

    self.interstitialOpts = newOpts;
    return success;
}

- (BOOL) __validateOptions:(NSDictionary *)options error:(NSError **)error
{
    if (options[OPT_AD_SIZE] != nil &&
        (![options[OPT_AD_SIZE] isKindOfClass:[NSNumber class]] ||
         [options[OPT_AD_SIZE] unsignedIntegerValue] > ACAdSizeSmartBanner)) {
        *error = [NSError errorWithDomain:ACErrorDomain
                                     code:ACErrorInvalidRequest
                                 userInfo:@{NSLocalizedFailureReasonErrorKey: @"Invalid ad size."}];
        return NO;
    }
    if (options[OPT_AD_POSITION] != nil &&
        (![options[OPT_AD_POSITION] isKindOfClass:[NSNumber class]] ||
         [options[OPT_AD_POSITION] unsignedIntegerValue] > ACBannerPositionBottom)) {
        *error = [NSError errorWithDomain:ACErrorDomain
                                     code:ACErrorInvalidRequest
                                 userInfo:@{NSLocalizedFailureReasonErrorKey: @"Invalid ad position."}];
        return NO;
    }
    if (options[OPT_AUTO_SHOW] != nil && ![options[OPT_AUTO_SHOW] isKindOfClass:[NSNumber class]]) {
        *error = [NSError errorWithDomain:ACErrorDomain
                                     code:ACErrorInvalidRequest
                                 userInfo:@{NSLocalizedFailureReasonErrorKey: @"Argument `autoShow` must be of type `boolean`."}];
        return NO;
    }
    if (options[OPT_AD_EXTRA] != nil && ![options[OPT_AD_EXTRA] isKindOfClass:[NSDictionary class]]) {
        *error = [NSError errorWithDomain:ACErrorDomain
                                     code:ACErrorInvalidRequest
                                 userInfo:@{NSLocalizedFailureReasonErrorKey: @"Argument `adExtra` must be of type `object`"}];
        return NO;
    }
    *error = nil;
    return YES;
}

#pragma mark - Banner commands

- (void)createBanner:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult *pluginResult;

    NSMutableDictionary *opts = [[command argumentAtIndex:0
                                              withDefault:@{}
                                                 andClass:[NSDictionary class]]
                                 mutableCopy];
    NSError *error = nil;
    BOOL success = [self __mergeOptionsWithOptions:opts error:&error];

    if (!success) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                         messageAsString:[error localizedFailureReason]];
    }
    else {
        id zoneId = opts[OPT_ZONE_ID];
        if (!zoneId) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                             messageAsString:@"You must provide zoneId argument."];
        } else if (![zoneId isKindOfClass:[NSString class]]) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                             messageAsString:@"Zone id must be of type string."];
        } else {
            [self __removeBanner];
            __weak AdcashSDK *weakSelf = self;
            [self.commandDelegate runInBackground:^{

                weakSelf.banner = [[ACBannerView alloc] initWithAdSize:[weakSelf adSize]
                                                          adUnitID:zoneId
                                                rootViewController:weakSelf.viewController];
                weakSelf.banner.delegate = weakSelf;

                weakSelf.bannerCallbackId = command.callbackId;
                [weakSelf.banner load];
            }];
            return;
        }
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setOptions:(CDVInvokedUrlCommand*)command
{
    NSDictionary *userOptions = [command argumentAtIndex:0 withDefault:@{} andClass:[NSDictionary class]];
    NSError *error = nil;
    BOOL success = [self __mergeOptionsWithOptions:userOptions error:&error];

    // If banner options validate, then interstitial options validate too.
    [self __mergeInterstitialOptionsWithOptions:userOptions error:nil];

    CDVPluginResult *pluginResult;
    if (!success) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                   messageAsString:[error localizedFailureReason]];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) showBanner:(CDVInvokedUrlCommand*)command
{
    [self __toggleBanner:YES command:command];
}

- (void) hideBanner:(CDVInvokedUrlCommand*)command
{
    [self __toggleBanner:NO command:command];
}

- (void) __toggleBanner:(BOOL) shouldShow command:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult *pluginResult;
    if (!self.banner) {
        NSString *message = [NSString stringWithFormat:@"Banner must be created first before `%@` action is performed.",
                             command.methodName];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                         messageAsString:message];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    self.banner.hidden = !shouldShow;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

- (void) __positionBannerForPosition:(ACBannerPosition)position
{
    NSAssert(self.banner, @"Banner must be created first.");
    CGPoint origin;
    if (position == ACBannerPositionBottom) {
        origin = CGPointMake(0, self.viewController.view.frame.size.height - self.banner.frame.size.height);
    }
    else {
        origin = CGPointMake(0, 0);
    }
    CGRect aRect = self.banner.frame;
    aRect.origin = origin;
    self.banner.frame = aRect;
}

- (void) removeBanner:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult *pluginResult;
    if (self.banner) {
        [self __removeBanner];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                         messageAsString:@"Banner must be created first before being removed."];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) __removeBanner
{
    [self.banner removeFromSuperview];
    self.banner.delegate = nil;
    self.banner = nil;
}

#pragma mark - Interstitial commands

- (void) prepareInterstitial:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult *pluginResult;

    NSMutableDictionary *opts = [[command argumentAtIndex:0
                                              withDefault:@{}
                                                 andClass:[NSDictionary class]]
                                 mutableCopy];

    NSError *error = nil;
    BOOL success = [self __mergeInterstitialOptionsWithOptions:opts error:&error];
    if (!success) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                         messageAsString:[error localizedFailureReason]];
    } else {
        id zoneId = opts[OPT_ZONE_ID];

        if (!zoneId) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                             messageAsString:@"You must provide zoneId argument."];
        } else if (![zoneId isKindOfClass:[NSString class]]) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                             messageAsString:@"Zone id must be of type string."];
        } else {
            self.interstitial = nil;
            self.interstitial.delegate = nil;
            __weak AdcashSDK *weakSelf = self;
            [self.commandDelegate runInBackground:^{

                weakSelf.interstitial = [ACInterstitial interstitialWithAdUnitID:zoneId];
                weakSelf.interstitial.delegate = weakSelf;

                weakSelf.interstitialCallbackId = command.callbackId;
                [weakSelf.interstitial load];
            }];
            return;
        }
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) showInterstitial:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult *pluginResult;
    if (!self.interstitial.isReady) {
        NSString *message = [NSString stringWithFormat:@"Interstitial must be prepared first before `%@` action is performed.",
                             command.methodName];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                         messageAsString:message];
    }
    else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.interstitial presentFromRootViewController:self.viewController];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

#pragma mark - ConversionTracker commands

- (void) reportAppOpen:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        NSString *appId = [[NSBundle mainBundle] bundleIdentifier];
        [ACConversionTracker trackApplicationOpenForAppID:appId];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }];
}

#pragma mark - ACBannerView delegate

- (void)bannerViewDidReceiveAd:(ACBannerView *)bannerView
{
    dispatch_async(dispatch_get_main_queue(), ^{
        self.banner.hidden = ![self autoShow];
        UIViewAutoresizing mask = UIViewAutoresizingFlexibleWidth |
        UIViewAutoresizingFlexibleLeftMargin |
        UIViewAutoresizingFlexibleRightMargin;
        switch ([self adPosition]) {
            case ACBannerPositionBottom:
                mask = mask | UIViewAutoresizingFlexibleTopMargin;
                break;
            case ACBannerPositionTop:
                mask = mask | UIViewAutoresizingFlexibleBottomMargin;
                break;
            default:
                break;
        }

        bannerView.autoresizingMask = mask;
        [self __positionBannerForPosition:[self adPosition]];
        [self.viewController.view addSubview:bannerView];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCallbackId];
    });
}

- (void)bannerView:(ACBannerView *)bannerView didFailToReceiveAdWithError:(NSError *)error
{
    dispatch_async(dispatch_get_main_queue(), ^{
        self.banner.hidden = YES; // Failed to load.
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                          messageAsString:[error localizedDescription]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCallbackId];
    });
}

#pragma mark - ACInterstitial delegate

- (void)interstitialDidReceiveAd:(ACInterstitial *)interstitial
{
    dispatch_async(dispatch_get_main_queue(), ^{
        if ([self.interstitialOpts[OPT_AUTO_SHOW] boolValue]) {
            [interstitial presentFromRootViewController:self.viewController];
        }
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCallbackId];
    });
}

- (void)interstitial:(ACInterstitial *)interstitial didFailToReceiveAdWithError:(NSError *)error
{
    dispatch_async(dispatch_get_main_queue(), ^{
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                          messageAsString:[error localizedDescription]];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCallbackId];
    });
}

@end
