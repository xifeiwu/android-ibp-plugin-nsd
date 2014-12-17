/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ibp.plugin.nsd;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
public class NSDPlugin extends CordovaPlugin {
    NSDHelper mNsdHelper;
    public static final String TAG = "NsdChat";
    private String serverName;
    private int serverPort;
    private CallbackContext initNsdCB;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
        case "initNsd":
            this.initNsd(callbackContext);
            break;
        case "stopNsd":
            stopNsd(callbackContext);
            break;
        case "startDiscovery":
            this.startDiscovery(callbackContext);
            break;
        case "stopDiscovery":
            this.stopDiscovery(callbackContext);
            break;
        case "registerService":
            serverName = args.getString(0);
            serverPort = args.getInt(1);
            this.registerService(callbackContext, serverName, serverPort);
            break;
        case "unRegisterService":
            this.unRegisterService(callbackContext);
            break;
        case "resolveService":
            this.resolveService(callbackContext, args.getString(0));
            break;
        }
        return true;
    }

    private Handler mHandler;

    private void initNsd(CallbackContext callbackContext) {
        final CallbackContext cbc = callbackContext;
        initNsdCB = callbackContext;
        try {
            if (null == mHandler) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        String type = msg.getData().getString("type");
                        String message = msg.getData().getString("msg");

                        JSONObject data = new JSONObject();
                        try {
                            data.put("type", new String(type));
                            data.put("data", new String(message));
                        } catch (JSONException e) {
                        }
                        PluginResult result = new PluginResult(PluginResult.Status.OK, data);
                        result.setKeepCallback(true);
                        cbc.sendPluginResult(result);
                    }
                };
                if (null == mNsdHelper) {
                    mNsdHelper = new NSDHelper(cordova.getActivity(), mHandler);
                    mNsdHelper.initializeNsd();
                }
                PluginResult result = new PluginResult(PluginResult.Status.OK, "initNsd: success.");
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
            } else {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "initNsd: Nsd Has been Initialized.");
                callbackContext.sendPluginResult(result);
            }
        } catch (Exception e) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, "initNsd Exception: " + e);
            callbackContext.sendPluginResult(result);
        }
    }

    private void sendByHandler(String type, String data) {
        if(null != mHandler){
            Bundle messageBundle = new Bundle();
            messageBundle.putString("type", type);
            messageBundle.putString("msg", data);
            Message message = new Message();
            message.setData(messageBundle);
            mHandler.sendMessage(message);
        }
    }

    private void stopNsd(CallbackContext callbackContext) {
        if ((null != mNsdHelper) && (null != mHandler)) {
            mNsdHelper.unRegisterService();
            mNsdHelper.stopDiscovery();
            mNsdHelper = null;
            mHandler = null;
            callbackContext.success("In stopNsd.");
        } else {
            callbackContext.error("Nsd has stopped.");
        }
    }
    private void isNSDInitialized(CallbackContext callbackContext){
        if((null != mNsdHelper) && (null != mHandler)){
            callbackContext.success("true");
        }else{
            callbackContext.success("false");
        }
    }

    private void startDiscovery(CallbackContext callbackContext) {
        if ((null != mNsdHelper) && (null != mHandler)) {
            mNsdHelper.setServiceDiscoveryCB(callbackContext);
            mNsdHelper.startDiscovery();
//            callbackContext.success("In startDiscovery");
        } else {
            callbackContext.error("Please init NSD first.");
        }
    }
    private void stopDiscovery(CallbackContext callbackContext) {
        if ((null != mNsdHelper) && (null != mHandler)) {
            mNsdHelper.setServiceDiscoveryCB(callbackContext);
            mNsdHelper.stopDiscovery();
        } else {
            callbackContext.error("Please init NSD first.");
        }
    }
    private void isDiscoverServicesStarted(CallbackContext callbackContext){
        if(mNsdHelper.isDiscoverServicesStarted){
            callbackContext.success("true");
        }else{
            callbackContext.success("false");
        }
    }

    private void registerService(CallbackContext callbackContext, String name, int port) {
        if ((null != mNsdHelper) && (null != mHandler)) {
            mNsdHelper.setRegisterServiceCB(callbackContext);
            mNsdHelper.registerService(name, port);
        }
        else {
            callbackContext.error("Plugin registerService: Please init NSD first.");
        }
    }
    private void unRegisterService(CallbackContext callbackContext) {
        if ((null != mNsdHelper) && (null != mHandler)) {
            mNsdHelper.setRegisterServiceCB(callbackContext);
            mNsdHelper.unRegisterService();
        } else {
            callbackContext.error("Plugin unRegisterService: Please init NSD first.");
        }
    }

    public void resolveService(CallbackContext callbackContext, String name) {
//        Log.d("device name: ", name);
        mNsdHelper.setResolveServiceCB(callbackContext);
        mNsdHelper.resolveInfoByName(name);
//        callbackContext.success("In resolveService. device name: " + name);
    }

    public void onPause(boolean multitasking) {
        if ((null != mNsdHelper) && (null != mHandler)) {
            this.sendByHandler("onPause", "stopNsd in onPause.");
            mNsdHelper.setRegisterServiceCB(initNsdCB);
            mNsdHelper.unRegisterService();
            mNsdHelper.setServiceDiscoveryCB(initNsdCB);
            mNsdHelper.stopDiscovery();
            mNsdHelper = null;
            mHandler = null;
        }
    }

    public void onResume(boolean multitasking) {
        this.sendByHandler("onResume", "in fucntion onResume.");
    }

    public void onDestroy() {
        if ((null != mNsdHelper) && (null != mHandler)) {
            this.sendByHandler("onDestroy", "stopNsd in onDestroy.");
            mNsdHelper.setRegisterServiceCB(initNsdCB);
            mNsdHelper.unRegisterService();
            mNsdHelper.setServiceDiscoveryCB(initNsdCB);
            mNsdHelper.stopDiscovery();
            mNsdHelper = null;
            mHandler = null;
        }
    }
}