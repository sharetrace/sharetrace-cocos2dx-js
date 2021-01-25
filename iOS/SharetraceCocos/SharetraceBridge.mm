//
//  SharetraceBridge.m
//  Sharetrace-cocos-js
//
//  Created by Sharetrace on 2020/12/23.
//

#import "SharetraceBridge.h"

using namespace cocos2d;

@implementation SharetraceBridge

static NSString * const key_code = @"code";
static NSString * const key_msg = @"msg";
static NSString * const key_paramsData = @"paramsData";
static NSString * const key_channel = @"channel";

static NSString * const js_installCallback = @"js_installCallback";
static NSString * const js_wakeupCallback = @"js_wakeupCallback";

static SharetraceBridge *shareInstance = nil;
+ (instancetype)shareInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        shareInstance = [[self alloc]init];
    });
    return shareInstance;
}

+ (void) getInstallTrace {
    [Sharetrace getInstallTrace:^(AppData * _Nullable appData) {
        if (appData == nil) {
            NSDictionary* dict = [SharetraceBridge parseToResultDict:-1 :@"Extract data fail." :@"" :@""];
            [SharetraceBridge installCallback:dict];
            return;
        }

        NSDictionary* dict = [SharetraceBridge parseToResultDict:200 :@"Success" :appData.paramsData :appData.channel];
        [SharetraceBridge installCallback:dict];
    } :^(NSInteger code, NSString * _Nonnull msg) {
        NSDictionary* dict = [SharetraceBridge parseToResultDict:code :msg :@"" :@""];
        [SharetraceBridge installCallback:dict];
    }];
}

+ (void) installCallback:(NSDictionary*) dict {
    [self commonCallback:dict :js_installCallback];
}

+ (void) registerWakeupTrace {
    SharetraceBridge * cacheHolder = [SharetraceBridge shareInstance];
    cacheHolder.hasWakeupRegister = YES;
    if (cacheHolder.wakeUpData != nil) {
        AppData* appData = cacheHolder.wakeUpData;
        NSDictionary* dict = [SharetraceBridge parseToResultDict:200 :@"Success" :appData.paramsData :appData.channel];
        [SharetraceBridge wakeupTraceCallback:dict];
    }
}

+ (void) wakeupTraceCallback:(NSDictionary*) dict {
    [self commonCallback:dict :js_wakeupCallback];
}

- (void)getWakeUpTrace:(AppData *)appData {
    if (appData == nil) {
        return;
    }
    SharetraceBridge * cacheHolder = [SharetraceBridge shareInstance];
    if (cacheHolder.hasWakeupRegister) {
        NSDictionary* dict = [SharetraceBridge parseToResultDict:200 :@"Success" :appData.paramsData :appData.channel];
        [SharetraceBridge wakeupTraceCallback:dict];
        cacheHolder.wakeUpData = nil;
    } else {
        cacheHolder.wakeUpData = appData;
    }
    
    self.wakeUpData = appData;
}

+ (NSString*)dictToJSONString:(NSDictionary*) dict {
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict
                                                       options:0
                                                         error:&error];
    
    if (jsonData != nil) {
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        return jsonString;
    } else {
        return @"";
    }
}

+ (NSDictionary*)parseToResultDict:(NSInteger)code :(NSString*)msg :(NSString*)paramsData :(NSString*)channel {
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];
    dict[key_code] = [NSNumber numberWithInteger:code];
    dict[key_msg] = msg;
    dict[key_paramsData] = paramsData;
    dict[key_channel] = channel;
    return dict;
}

+ (void) commonCallback:(NSDictionary*)dict :(NSString*)method {
    NSString *json = [SharetraceBridge dictToJSONString:dict];
    
    std::string jsonStr = [json UTF8String];
#ifndef HAVE_INSPECTOR
    NSString *func = [NSString stringWithFormat: @"var sharetrace = require(\"Sharetrace\");sharetrace.%@", method];
#else
    NSString *func = [NSString stringWithFormat: @"var sharetrace = window.__require(\"Sharetrace\");sharetrace.%@", method];
#endif
    std::string funcName = [func UTF8String];
    std::string jsCallStr = cocos2d::StringUtils::format("%s(%s);", funcName.c_str(),jsonStr.c_str());

#if CC_FIX_ARTIFACTS_BY_STRECHING_TEXEL_TMX
    BOOL success = se::ScriptEngine::getInstance()->evalString(jsCallStr.c_str());
#else
    BOOL success = ScriptingCore::getInstance()->evalString(jsCallStr.c_str());
#endif
    
    if (success) {
        NSLog(@"SharetraceBridge : %@ success.", method);
    } else {
        NSLog(@"SharetraceBridge : %@ start backup callback...", method);
        
        std::string funcName = [method UTF8String];
        std::string jsCallStr = cocos2d::StringUtils::format("%s(%s);", funcName.c_str(), jsonStr.c_str());
        
#if CC_FIX_ARTIFACTS_BY_STRECHING_TEXEL_TMX
        BOOL backupSuccess = se::ScriptEngine::getInstance()->evalString(jsCallStr.c_str());
#else
        BOOL backupSuccess = ScriptingCore::getInstance()->evalString(jsCallStr.c_str());
#endif
        if (backupSuccess) {
            NSLog(@"SharetraceBridge : %@ backup callback success...", method);
        } else {
            NSLog(@"SharetraceBridge : %@ backup callback fail...", method);
        }
    }
}

@end
