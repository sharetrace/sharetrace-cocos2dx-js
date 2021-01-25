//
//  SharetraceBridge.h
//  Sharetrace-cocos-js
//
//  Created by Sharetrace on 2020/12/23.
//

#import <Foundation/Foundation.h>
#import "cocos2d.h"
#ifdef CC_FIX_ARTIFACTS_BY_STRECHING_TEXEL_TMX
#include "cocos/scripting/js-bindings/jswrapper/SeApi.h"
#else
#include "scripting/js-bindings/manual/ScriptingCore.h"
#endif
#ifndef HAVE_INSPECTOR
#include "ScriptingCore.h"
#endif
#import <SharetraceSDK/SharetraceSDK.h>

@interface SharetraceBridge : NSObject <SharetraceDelegate>

@property(nonatomic, retain) AppData * _Nullable wakeUpData;
@property(nonatomic, assign) BOOL hasWakeupRegister;

+ (SharetraceBridge *_Nonnull) shareInstance;

+ (void) getInstallTrace;

+ (void) registerWakeupTrace;

@end

