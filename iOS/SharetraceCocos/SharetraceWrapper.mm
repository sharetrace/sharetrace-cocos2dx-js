//
//  SharetraceWrapper.m
//  Sharetrace-cocos-js
//
//  Created by Sharetrace on 2020/12/24.
//

#import "SharetraceWrapper.h"
#import <SharetraceSDK/SharetraceSDK.h>
#import "SharetraceBridge.h"

@implementation SharetraceWrapper

+ (void)startInit {
    [Sharetrace initWithDelegate: [SharetraceBridge shareInstance]];
}

+ (BOOL)handleSchemeLinkURL:(NSURL * _Nullable)url {
    return [Sharetrace handleSchemeLinkURL:url];
}

+ (BOOL)handleUniversalLink:(NSUserActivity * _Nullable)userActivity {
    return [Sharetrace handleUniversalLink:userActivity];
}

@end
