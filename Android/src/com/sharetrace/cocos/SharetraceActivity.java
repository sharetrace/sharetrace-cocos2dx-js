package com.sharetrace.cocos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import org.cocos2dx.lib.Cocos2dxActivity;

import cn.net.shoot.sharetracesdk.ShareTrace;

public class SharetraceActivity extends Cocos2dxActivity {

    private static Cocos2dxActivity cocos2dxActivity = null;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cocos2dxActivity = this;
        ShareTrace.init(getApplication());
        SharetraceBridge.getWakeupTrace(getIntent(), cocos2dxActivity);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SharetraceBridge.getWakeupTrace(intent, cocos2dxActivity);
    }

    public static void getInstallTrace() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                SharetraceBridge.getInstallTrace(cocos2dxActivity);
            }
        });
    }

    public static void registerWakeupTrace() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                SharetraceBridge.registerWakeupTrace(cocos2dxActivity);
            }
        });
    }

}
