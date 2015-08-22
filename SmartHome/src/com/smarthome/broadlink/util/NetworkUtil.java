package com.smarthome.broadlink.util;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkUtil {
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private DhcpInfo dhcpInfo;
	public NetworkUtil(Context context) {
		// Get the instance of the WifiManager
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	public void startScan() {
		mWifiManager.startScan();
		// Get the WifiInfo
		mWifiInfo = mWifiManager.getConnectionInfo();

	}

	public String getWiFiSSID() {

		String CurInfoStr = mWifiInfo.toString() + "";
		String CurSsidStr = mWifiInfo.getSSID().toString() + "";
		if (CurInfoStr.contains(CurSsidStr)) {
			return CurSsidStr;
		} else {
			return CurSsidStr.replaceAll("\"", "") + "";
		}

	}
	public String getGatewayaddr(){
		dhcpInfo = mWifiManager.getDhcpInfo();
		int gatewayip = dhcpInfo.gateway;
	    if(gatewayip==0) return "";  
        return ((gatewayip & 0xff)+"."+(gatewayip>>8 & 0xff)+"."  
                +(gatewayip>>16 & 0xff)+"."+(gatewayip>>24 & 0xff));  
	}
	
}
