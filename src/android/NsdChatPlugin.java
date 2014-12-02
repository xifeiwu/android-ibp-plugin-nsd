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
	private String serverName;
	private int serverPort;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch(action){
        case "initNsd":
            this.initNsd(callbackContext);
            PluginResult.Status status = PluginResult.Status.NO_RESULT;
            PluginResult pluginResult = new PluginResult(status);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
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
        case "stopNsd":
            stopNsd(callbackContext);
            break;
        }
        return true;
    }
    
    private Handler mHandler;
    private void initNsd(CallbackContext callbackContext){
        final CallbackContext cbc = callbackContext;
        try {
            if(null == mHandler){
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
                        PluginResult result = new PluginResult(PluginResult.Status.OK, data);
                        result.setKeepCallback(true);
                        cbc.sendPluginResult(result);
                    }
                };
                if(null == mNsdHelper){
                    mNsdHelper = new NsdHelper(cordova.getActivity(), mHandler);
                    mNsdHelper.initializeNsd();
                }
                sendJSONObjectByHandler(cbc, "success", "Initialize NSD successful."); 
            }else{
                sendJSONObjectByHandler(cbc, "error", "Nsd Has been Initialized.");      
            }
        } catch(Exception e) {
            sendJSONObjectByHandler(cbc, "error", "Exception: " + e);
        }        
    }
    private void sendJSONObjectByHandler(CallbackContext callbackContext, String type, String data){
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", type);
            obj.put("data", data);
        } catch(JSONException e1) {
        }
        PluginResult result;
        if("error" == type){
            result = new PluginResult(PluginResult.Status.ERROR, data);
        }else{
            result = new PluginResult(PluginResult.Status.OK, data);            
        }
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);        
    }
    
    private void startDiscovery(CallbackContext callbackContext){
        if((null != mNsdHelper) && (null != mHandler)){
            mNsdHelper.startDiscovery();
            callbackContext.success("In startDiscovery");
        }else{
            callbackContext.error("Please init NSD first.");        
        }
    }
    
    private void stopDiscovery(CallbackContext callbackContext){
        if((null != mNsdHelper) && (null != mHandler)){
            mNsdHelper.stopDiscovery();
            callbackContext.success("In  stopDiscovery");
        }else{
            callbackContext.error("Please init NSD first.");            
        }
    }
    
    private void registerService(CallbackContext callbackContext, String name, int port){
        if((null != mNsdHelper) && (null != mHandler)){
            mNsdHelper.registerService(name, port);
            callbackContext.success("In registerService: " + name + ":" + port);
        }else{
            callbackContext.error("Please init NSD first.");            
        }
    }
    private void unRegisterService(CallbackContext callbackContext){
        if((null != mNsdHelper) && (null != mHandler)){
            mNsdHelper.unRegisterService();
            callbackContext.success("In unRegisterService: " + serverName + ":" + serverPort);
        }else{
            callbackContext.error("Please init NSD first.");            
        }
    }
    private void stopNsd(CallbackContext callbackContext) {
        if ((null != mNsdHelper) && (null != mHandler)) {
            mNsdHelper.unRegisterService();
            mNsdHelper.stopDiscovery();
            mNsdHelper = null;
            mHandler = null;
            callbackContext.success("In stopNsd.");
        }else{
            callbackContext.error("Nsd has stopped.");            
        }
    }
    
    public void onResolveService(View v){
        mNsdHelper.resolveServerInfo();
    }
}


