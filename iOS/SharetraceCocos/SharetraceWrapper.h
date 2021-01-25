//
//  SharetraceWrapper.h
//  Sharetrace-cocos-js
//
//  Created by Sharetrace on 2020/12/24.
//

#import <Foundation/Foundation.h>

@interface SharetraceWrapper : NSObject

+ (void)startInit;

+ (BOOL)handleSchemeLinkURL:(NSURL * _Nullable)url;

+ (BOOL)handleUniversalLink:(NSUserActivity * _Nullable)userActivity;

@end

