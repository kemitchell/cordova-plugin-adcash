//
//  ACError.h
//  adcash-ios-sdk
//
//  Created by Martin on 1/23/15.
//  Copyright (c) 2015 AdCash. All rights reserved.
//
#import <Foundation/Foundation.h>

/**
 Adcash error domain.
 @since 1.2
 */
extern NSString *const ACErrorDomain;



/**
 Error codes for ACErrorDomain error domain
 @since 1.2
 */
typedef NS_ENUM(NSUInteger, ACErrorCode){
    /**
     @brief  Indicates that there is no ad available to show.
     
     @since 1.2
     */
    ACErrorNoFill,
    
    
    /**
     @brief  Internal error.
     
     @since 1.2
     */
    ACErrorInternalError,
    
    /**
     @brief  The ad request is invalid. Typically this is because the ad
     did not have adUnitID or root view controller set.
     
     @since 1.2
     */
    ACErrorInvalidRequest,
};
