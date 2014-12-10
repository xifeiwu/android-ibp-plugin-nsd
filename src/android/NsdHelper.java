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

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

@SuppressLint("NewApi")
public class NsdHelper {

    NsdManager mNsdManager;
    NsdManager.ResolveListener mResolveListener;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;
    private final List<NsdServiceInfo> mServiceInfoList = new ArrayList<NsdServiceInfo>();

    public static final String SERVICE_TYPE = "_http._tcp.";
    private Handler mHandler;

    public NsdHelper(Context context, Handler handler) {
        mHandler = handler;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void initializeNsd() {
        initializeResolveListener();
        initializeDiscoveryListener();
        initializeRegistrationListener();
    }

    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onDiscoveryStarted(String regType) {
                sendNotification("onDiscoveryStarted", "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                sendNotification("onServiceFound", NsdServiceInfoToJSON(service).toString());
                addServerInfo(service);
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                sendNotification("onServiceLost", NsdServiceInfoToJSON(service).toString());
                removeServerInfo(service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                sendNotification("onDiscoveryStopped", serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                sendNotification("onStartDiscoveryFailed", "Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                sendNotification("onStopDiscoveryFailed", "Error code: " + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                sendNotification("onResolveFailed", "Error code: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                String oldName = serviceInfo.getServiceName();
                String newName = oldName.replace("\\032", " ");
                serviceInfo.setServiceName(newName);
                sendNotification("onServiceResolved", NsdServiceInfoToJSON(serviceInfo).toString());
                reWriteServerInfo(serviceInfo);
            }
        };
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                sendNotification("onServiceRegistered", NsdServiceInfoToJSON(NsdServiceInfo).toString());
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
                sendNotification("onRegistrationFailed", NsdServiceInfoToJSON(arg0).toString());
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                sendNotification("onServiceUnregistered", NsdServiceInfoToJSON(arg0).toString());
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                sendNotification("onUnregistrationFailed", "Error code: " + errorCode);
            }
        };
    }

    private boolean isServiceRegistered = false;

    public void registerService(String name, int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(name);
        serviceInfo.setServiceType(SERVICE_TYPE);
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
        isServiceRegistered = true;
    }

    public void unRegisterService() {
        if (isServiceRegistered) {
            mNsdManager.unregisterService(mRegistrationListener);
        }
        isServiceRegistered = false;
    }

    private boolean isDiscoverServicesStarted = false;

    public void startDiscovery() {
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        isDiscoverServicesStarted = true;
    }

    public void stopDiscovery() {
        if (isDiscoverServicesStarted && mDiscoveryListener != null) {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
        // mNsdManager = null;
        mServiceInfoList.clear();
        isDiscoverServicesStarted = false;
    }

    private JSONObject NsdServiceInfoToJSON(NsdServiceInfo info) {
        String name = info.getServiceName();
        String type = info.getServiceType();
        InetAddress host = info.getHost();
        int port = info.getPort();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("type", type);
        map.put("host", ((host == null) ? "null" : host.getHostAddress() + ":" + port));
        JSONObject jsonObj = new JSONObject(map);
        return jsonObj;
    }

    public void addServerInfo(NsdServiceInfo info) {
        Iterator<NsdServiceInfo> iter = mServiceInfoList.iterator();
        NsdServiceInfo element;
        boolean isExist = false;
        while (iter.hasNext()) {
            element = (NsdServiceInfo) iter.next();
            if (element.getServiceName().equals(info.getServiceName())) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            mServiceInfoList.add(info);
        }
    }

    public void reWriteServerInfo(NsdServiceInfo info) {
        int index = 0;
        boolean isExist = false;
        while (index < mServiceInfoList.size()) {
            if (mServiceInfoList.get(index).getServiceName().equals(info.getServiceName())) {
                isExist = true;
                break;
            }
            index++;
        }
        if (isExist) {
            mServiceInfoList.set(index, info);
        } else {
            mServiceInfoList.add(info);
        }
    }

    public void removeServerInfo(NsdServiceInfo info) {
        Iterator<NsdServiceInfo> iter = mServiceInfoList.iterator();
        NsdServiceInfo element = null;
        boolean isExist = false;
        while (iter.hasNext()) {
            element = (NsdServiceInfo) iter.next();
            if (element.getServiceName().equals(info.getServiceName())) {
                isExist = true;
                break;
            }
        }
        if (isExist) {
            mServiceInfoList.remove(element);
        }
    }

    public void showServerInfo() {
        Iterator<NsdServiceInfo> iter = mServiceInfoList.iterator();
        NsdServiceInfo info;
        int cnt = 1;
        StringBuffer sb = new StringBuffer();
        sb.append("========there are " + mServiceInfoList.size() + " elements========\n");
        while (iter.hasNext()) {
            info = (NsdServiceInfo) iter.next();
            sb.append(cnt + ". " + NsdServiceInfoToJSON(info) + "\n");
            cnt++;
        }// sendNotification("showServerInfo", sb.toString());
    }

    private int indexcnt = 0;

    public void resolveServerInfo() {
        if (indexcnt < mServiceInfoList.size()) {
            NsdServiceInfo info = mServiceInfoList.get(indexcnt);
            if (info.getHost() == null) {
                mNsdManager.resolveService(info, mResolveListener);
            } else {
                sendNotification("resolveServerInfo", "No Need To Resolve.");
            }
            indexcnt++;
        } else {
            indexcnt = 0;
        }
    }

    public void resolveInfoByName(String name) {
        int index = 0;
        NsdServiceInfo element = null;
        while (index < mServiceInfoList.size()) {
            if (mServiceInfoList.get(index).getServiceName().equals(name)) {
                element = mServiceInfoList.get(index);
                break;
            }
            index++;
        }
        if (null != element) {
            if (element.getHost() == null) {
                mNsdManager.resolveService(element, mResolveListener);
            } else {
                sendNotification("resolveInfoByName", "No Need To Resolve.");
            }
        }
    }

    public void sendNotification(String type, String msg) {
        Bundle messageBundle = new Bundle();
        messageBundle.putString("type", type);
        messageBundle.putString("msg", msg);
        Message message = new Message();
        message.setData(messageBundle);
        mHandler.sendMessage(message);
    }

    public void sendNotification(String type, JSONObject jsonObj) {
        Bundle messageBundle = new Bundle();
        messageBundle.putString("type", type);
        messageBundle.putString("msg", jsonObj.toString());
        Message message = new Message();
        message.setData(messageBundle);
        mHandler.sendMessage(message);
    }

    public void sendNotification(String type, JSONArray jsonArray) {
        Bundle messageBundle = new Bundle();
        messageBundle.putString("type", type);
        messageBundle.putString("msg", jsonArray.toString());
        Message message = new Message();
        message.setData(messageBundle);
        mHandler.sendMessage(message);
    }
}
