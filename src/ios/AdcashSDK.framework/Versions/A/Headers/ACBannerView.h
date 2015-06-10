//
//  ACBannerView.h
//  adcash-ios-sdk
//
//  Created by Martin on 11/23/14.
//  Copyright (c) 2014 AdCash. All rights reserved.
//

#import <UIKit/UIKit.h>
/**
 @brief  ACAdSize enum denotes the available sizes for banner ads that Adcash supports
 
 @since 1.2
 */
typedef NS_ENUM(NSUInteger, ACAdSize){
    /**
     @brief  Smart banner "smartly" sizes ads by rendering them in screen-wide width.
Smart banners translate into the following:
     
| Size | Device | Device orientation |
|:----:|:------:|:------------------:|
|320x50| iPhone | Portrait, Landscape |
|728x90| iPad   | Portrait, Landscape |

     
     @since 1.2
     */
    ACAdSizeSmartBanner,
};

@class ACBannerView;


/**
 ACBannerViewDelegate is the delegate class for receiving
 state change events from ACBannerView instances. Use it to receive
 state callbacks for banner ad request success, fail, or any other event
 triggered by the user.
 
 @since 1.2
 */
@protocol ACBannerViewDelegate <NSObject>

@optional
/**
 @brief Sent when the ad request has succeeded and the banner is ready to be shown.
 @param bannerView The ACBannerView instance that succeeded to load.
 @since 1.2
 */
- (void) bannerViewDidReceiveAd:(ACBannerView *)bannerView;


/**
 @brief Sent when the banner has failed to load.
 Use error param to determine what is the cause. Usually this is because there is no
 internet connectivity or because there are no more ads to show.
 @param bannerView The ACBannerView instance that failed to load.
 @param error      NSError instance. Use it to determine why the loading has failed.
 
 @since 1.2
 */
- (void) bannerView:(ACBannerView *)bannerView didFailToReceiveAdWithError:(NSError *)error;


/**
 @brief Sent just before the user is presented to a full-screen ad in response to his click action.
 Normally, the user is presented with a full-screen ad and when he taps on the close button, bannerViewWillDismissScreen: will be called.
 @param bannerView An ACBannerView instance.
 @since 1.2
 */
- (void) bannerViewWillPresentScreen:(ACBannerView *)bannerView;


/**
 @brief Sent when the app is about the enter in background because the user
 has clicked on an ad that navigates outside of the app to another application (e.g. App Store).
 Method `applicationDidEnterBackground:` of your app delegate is called immediately after this.
 @param bannerView An ACBannerView instance.
 @since 1.2
 */
- (void) bannerViewWillLeaveApplication:(ACBannerView *)bannerView;

/**
 @brief  Sent just before dismissing a full screen view.
 
 @param bannerView An ACBannerView instance
 
 @since 1.2
 */
- (void) bannerViewWillDismissScreen:(ACBannerView *)bannerView;

@end


/**
 ACBannerView is a UIView subclass that display banner ads. Sample usage:
 
     // Create and initialize an instance
     ACBannerView *bannerView = [[ACBannerView alloc] initWithAdSize:ACAdSizeSmartBanner
                                                            adUnitID:@"YOUR_ZONE_ID_HERE"
                                                  rootViewController:self];
     // Place the ad on screen
     [self.view addSubview:bannerView];
     // Load the banner ad
     [bannerView load];
 
 @since 1.2
 */
@interface ACBannerView : UIView

/**
 This is the value of the zone id that you created in adcash.com portal.
 A new zone id should be created for every unique placement of an ad in your app.
 @since 1.2
 */
@property (nonatomic, copy) NSString * adUnitID;

/**
 An ad size of type ACAdSize that denotes the banner size that is going to be requested.
 @since 1.2
 */
@property (nonatomic, assign, readonly) ACAdSize adSize;

/**
 A UIViewController subclass that is used to show a modal overlay for the banner.
 @since 1.2
 */
@property (nonatomic, weak) UIViewController * rootViewController;

/**
 An object that conforms to the ACBannerViewDelegate and can receive callbacks for banner state change.
 May be nil.
 @since 1.2
 */
@property (nonatomic, weak) id<ACBannerViewDelegate> delegate;

/**
 This is the designated initializer for the ACBannerView class.
 
 @param adSize             ACAdSize type that is used to request banner size.
 @param adUnitID           Unique zone id that is created in Adcash Publisher portal.
 @param rootViewController UIViewController subclass used to show a modal overlay.
 
 @return An initialized ACBannerView instance
 
 @since 1.2
 */
- (instancetype) initWithAdSize:(ACAdSize)adSize adUnitID:(NSString *)adUnitID rootViewController:(UIViewController *)rootViewController;

/**
 Call to start loading the banner.
 @since 1.2
 */
- (void) load;

@end
