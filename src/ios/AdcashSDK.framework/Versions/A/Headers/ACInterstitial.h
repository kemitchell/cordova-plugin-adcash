//
//  ACInterstitialAd.h
//  adcash-ios-sdk
//
//  Created by Martin on 11/23/14.
//  Copyright (c) 2014 AdCash. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class ACInterstitial;

/**
 ACInterstitialDelegate is the delegate class for receiving
 state change events from ACInterstitial instances. Use it to receive
 state callbacks for interstitial ad request success, fail, or any other event
 triggered by the user.
 @since 1.2
 */
@protocol ACInterstitialDelegate <NSObject>

@optional
/**
 @brief  Sent when the ad request has succeeded and the interstitial is ready to be shown.
 
 @param interstitial An ACInterstitial instance that succeeded to load.
 @discussion
 When the interstitial is loaded, you can use this callback to present the interstitial in
 your view controller, like so:
 
    - (void) interstitialDidReceiveAd:(ACInterstitial *)interstitial
    {
        [interstitial presentFromViewController:self];
    }

 @since 1.2
 */
- (void) interstitialDidReceiveAd:(ACInterstitial *)interstitial;

/**
 @brief  Sent when the interstitial has failed to load.
 Use error param to determine what is the cause. Usually this is because there is no
 internet connectivity or because there are no more interstitial ads to show.
 
 @param interstitial The ACInterstitial instance that failed to load.
 @param error        A NSError instance. Use it to determine why the loading has failed.
 
 @since 1.2
 */
- (void) interstitial:(ACInterstitial *)interstitial didFailToReceiveAdWithError:(NSError *)error;

/**
 @brief  Sent just before presenting the interstitial.
 
 @param interstitial An ACInterstitial instance that is about to be shown.
 
 @since 1.2
 */
- (void) interstitialWillPresentScreen:(ACInterstitial *)interstitial;

/**
 @brief  Sent just before the interstitial is to be dismissed.
 
 @param interstitial An ACInterstitial instance that is about to be dismissed.
 
 @since 1.2
 */
- (void) interstitialWillDismissScreen:(ACInterstitial *)interstitial;


/**
 @brief  Sent when the app is about the enter in background because the user
 has clicked on an ad that navigates outside of the app to another application (e.g. App Store).
 Method `applicationDidEnterBackground:` of your app delegate is called immediately after this.
 
 @param interstitial An ACInterstitial instance.
 
 @since 1.2
 */
- (void) interstitialWillLeaveApplication:(ACInterstitial *)interstitial;

@end



/**
 Class to display an Interstitial Ad.
 Interstitials are full screen advertisements that are shown at natural
 transition points in your application such as between game levels, when
 switching news stories, in general when transitioning from one view controller
 to another. It is best to request for an interstitial several seconds before
 when it is actually needed, so that it can preload its content and become
 ready to present, and when the time comes, it can be immediately presented to
 the user with a smooth experience.
 @since 1.2
 */
@interface ACInterstitial : NSObject

/**
 This is the value of the zone id that you created in adcash.com portal.
 A new zone id should be created for every unique placement of an ad in your app.
 @since 1.2
 */
@property (nonatomic, copy, readonly) NSString * adUnitID;

/**
 Indicates whether the interstitial is ready to be presented.
 @since 1.2
 */
@property (nonatomic, readonly, assign) BOOL isReady;

/**
 An object that conforms to the ACInterstitialDelegate and can receive callbacks
 for interstitial state change. May be nil.
 @since 1.2
 */
@property (nonatomic, weak) id<ACInterstitialDelegate> delegate;

/**
 This is the designated initializer for the ACInterstitial class.
 @param adUnitID Unique zone id that is created in Adcash Publisher portal.
 
 @return An initialized ACInterstitial instance
 
 @since 1.2
 */
- (instancetype) initWithAdUnitId:(NSString *)adUnitID;

/**
 This is a convenience class method that calls initWithAdUnitId: internally.
 @param adUnitID Unique zone id that is created in Adcash Publisher portal.
 
 @return An initialized ACInterstitial instance.
 
 @since 1.2
 */
+ (instancetype) interstitialWithAdUnitID:(NSString *)adUnitID;

/**
 Call to start loading the interstitial.
 @since 1.2
 */
- (void) load;

/**
 Call when the interstitial is loaded successfully to present it on screen.
 @param rootViewController An UIViewController instance that is used to present modal overlay.
 
 @since 1.2
 */
- (void) presentFromRootViewController:(UIViewController *)rootViewController;

@end
