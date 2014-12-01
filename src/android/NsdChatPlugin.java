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

package ibp.plugin.nsdchat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

@SuppressLint({ "HandlerLeak", "SimpleDateFormat" }) 
public class NsdChatPlugin extends CordovaPlugin {
	NsdHelper mNsdHelper;
	public static final String TAG = "NsdChat";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("initNsd")) {
            this.initNsd(callbackContext);
            PluginResult.Status status = PluginResult.Status.NO_RESULT;
            PluginResult pluginResult = new PluginResult(status);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }else{
            if (action.equals("startDiscovery")) {
                this.startDiscovery();
            }    
            if (action.equals("stopDiscovery")) {
                this.stopDiscovery();
            }    
            if (action.equals("registerService")) {
                this.registerService(args.getString(0), args.getInt(1));
            }    
            if (action.equals("unRegisterService")) {
                this.unRegisterService();
            }    
            PluginResult.Status status = PluginResult.Status.NO_RESULT;
            PluginResult pluginResult = new PluginResult(status);
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
        }
        return true;
    }
    private Handler mHandler;
    private void initNsd(CallbackContext callbackContext) {
        final CallbackContext cbc = callbackContext;
        try {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String type = msg.getData().getString("type");
                    String message = msg.getData().getString("msg");

                    JSONObject data = new JSONObject();
                    try {
                        data.put("type", new String(type));
                        data.put("data", new String(message));
                    } catch(JSONException e) {

                    }
                    Log.d(TAG, type + ": " + message);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, data);
                    result.setKeepCallback(true);
                    cbc.sendPluginResult(result);
                }
            };
            mNsdHelper = new NsdHelper(cordova.getActivity(), mHandler);
            mNsdHelper.initializeNsd();
        } catch(Exception e) {
            callbackContext.error("Error " + e);
        }
    }
    private void startDiscovery(){
        if(null != mNsdHelper){
            mNsdHelper.startDiscovery();
        }else{
            
        }
    }
    private void stopDiscovery(){
        mNsdHelper.stopDiscovery();
    }
    private void registerService(String name, int port){
        mNsdHelper.registerService(name, port);
    }
    private void unRegisterService(){
        mNsdHelper.unRegisterService();
    }
    private void stopNsd(CallbackContext callbackContext) {
        if (mNsdHelper != null) {
            unRegisterService();
            stopDiscovery();
        }
    }
    public void onResolveService(View v){
        mNsdHelper.resolveServerInfo();
    }
}

