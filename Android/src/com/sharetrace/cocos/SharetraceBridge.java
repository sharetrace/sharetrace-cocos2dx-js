package com.sharetrace.cocos;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.json.JSONObject;

import cn.net.shoot.sharetracesdk.AppData;
import cn.net.shoot.sharetracesdk.ShareTrace;
import cn.net.shoot.sharetracesdk.ShareTraceInstallListener;
import cn.net.shoot.sharetracesdk.ShareTraceWakeUpListener;

public class SharetraceBridge {
    private static final String TAG = "SharetraceBridge";
    private static final String KEY_CODE = "code";
    private static final String KEY_MSG = "msg";
    private static final String KEY_PARAMSDATA = "paramsData";
    private static final String KEY_CHANNEL = "channel";

    // CocosCreator较低版本需要使用这句，如无法回调尝试手动切换以下这句
//        private static final String REQUIRE = "var sharetrace = require(\"Sharetrace\");";

    // CocosCreator(>=2.2.0)较高版本需要使用这句，如无法回调尝试手动切换以下这句
    private static final String REQUIRE = "var sharetrace = window.__require(\"Sharetrace\");";

    private static final String CALLBACK_PATTERN = "sharetrace.%s(%s);";
    private static final String JS_INSTALL_CALLBACK = "js_installCallback";
    private static final String JS_WAKEUP_CALLBACK = "js_wakeupCallback";

    private static AppData cacheWakeupData = null;
    private static boolean hasWakeupRegister = false;

    public static void registerWakeupTrace(final Cocos2dxActivity cocos2dxActivity) {
        hasWakeupRegister = true;

        if (cacheWakeupData != null) {
            AppData appData = cacheWakeupData;
            JSONObject json = extractToResult(200, "Success",
                    appData.getParamsData(),
                    appData.getChannel());
            wakeupCallback(json.toString(), cocos2dxActivity);
            cacheWakeupData = null;
        }

    }

    public static void getWakeupTrace(Intent intent, final Cocos2dxActivity cocos2dxActivity) {
        ShareTrace.getWakeUpTrace(intent, new ShareTraceWakeUpListener() {
            @Override
            public void onWakeUp(AppData appData) {
//                    Log.d(TAG, "onWakeUp: " + appData.toString());
                if (appData == null) {
                    return;
                }

                if (hasWakeupRegister) {
                    JSONObject json = extractToResult(200, "Success",
                            appData.getParamsData(),
                            appData.getChannel());
                    wakeupCallback(json.toString(), cocos2dxActivity);
                    cacheWakeupData = null;
                } else {
                    cacheWakeupData = appData;
                }
            }
        });
    }

    public static void getInstallTrace(final Cocos2dxActivity cocos2dxActivity) {
        ShareTrace.getInstallTrace(new ShareTraceInstallListener() {
            @Override
            public void onInstall(AppData appData) {
                if (appData == null) {
                    JSONObject json = extractToResult(-1, "Extract data fail.", "", "");
                    installCallback(json.toString(), cocos2dxActivity);
                    return;
                }
                JSONObject json = extractToResult(200, "Success",
                        appData.getParamsData(),
                        appData.getChannel());
                installCallback(json.toString(), cocos2dxActivity);
            }

            @Override
            public void onError(int code, String message) {
                JSONObject json = extractToResult(code, message, "", "");
                installCallback(json.toString(), cocos2dxActivity);
            }
        });
    }

    private static void installCallback(final String data, Cocos2dxActivity cocos2dxActivity) {
        if (cocos2dxActivity == null) {
            return;
        }
        cocos2dxActivity.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                commonCallback(JS_INSTALL_CALLBACK, data);
            }
        });

    }

    private static void wakeupCallback(final String data, Cocos2dxActivity cocos2dxActivity) {
        if (cocos2dxActivity == null) {
            return;
        }
        cocos2dxActivity.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                commonCallback(JS_WAKEUP_CALLBACK, data);
            }
        });
    }

    private static JSONObject extractToResult(int code, String msg, String paramsData, String channel) {
        JSONObject json = new JSONObject();

        try {
            json.put(KEY_CODE, code);
            json.put(KEY_MSG, msg);
            json.put(KEY_PARAMSDATA, defaultValue(paramsData));
            json.put(KEY_CHANNEL, defaultValue(channel));
        } catch (Throwable e) {
            Log.e(TAG, "extractToResult error, " + e.getMessage());
        }

        return json;
    }

    private static String defaultValue(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }

        return str;
    }

    private static void commonCallback(String method, String data) {
        String callbackJs = REQUIRE + String.format(CALLBACK_PATTERN, method, data);
        try {
            Cocos2dxJavascriptJavaBridge.evalString(callbackJs);
            Log.d(TAG, "callbackJs: " + callbackJs);
        } catch (Throwable e) {
            Log.d(TAG, "callbackJs: " + callbackJs + ", error: " + e.getMessage());
        }
    }

}
