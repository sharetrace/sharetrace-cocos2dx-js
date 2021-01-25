
var sharetrace = {

    wakeupCallback: function(appData) {

    },

    installCallback: function(appData) {

    },

    getInstallTrace: function (callback) {
        this.installCallback = callback;
        if (cc.sys.OS_ANDROID == cc.sys.os) {
            jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity",
                "getInstallTrace", "()V");
        } else if(cc.sys.OS_IOS == cc.sys.os) {
            jsb.reflection.callStaticMethod("SharetraceBridge","getInstallTrace");
        }
    },

    registerWakeupTrace: function (callback) {
        this.wakeupCallback = callback;
        if (cc.sys.OS_ANDROID == cc.sys.os) {
            jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity",
                "registerWakeupTrace", "()V");
        } else if (cc.sys.OS_IOS == cc.sys.os) {
            jsb.reflection.callStaticMethod("SharetraceBridge","registerWakeupTrace");
        }
    },

    js_installCallback: function (appData) {
        this.installCallback(appData);
    },

    js_wakeupCallback: function (appData) {
        this.wakeupCallback(appData);
    },

};

module.exports = sharetrace;

