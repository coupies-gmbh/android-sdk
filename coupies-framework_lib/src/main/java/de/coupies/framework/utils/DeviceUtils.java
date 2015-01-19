package de.coupies.framework.utils;

import java.util.List;

import de.coupies.coupies_framework_lib.R;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceUtils {

	public static Location getLastBestLocation(Context context, long maxDelay) {
		Location bestResult = null;
	    float bestAccuracy = Float.MAX_VALUE;
	    long bestTime = Long.MIN_VALUE;
        long minTime = System.currentTimeMillis() - (maxDelay * 1000);
	    
		LocationManager locationManager;
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider: matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();
        
				if ((time > minTime && accuracy < bestAccuracy)) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				}
				else if (time < minTime && 
						bestAccuracy == Float.MAX_VALUE && time > bestTime){
					bestResult = location;
					bestTime = time;
				}
			}
		}
		
		//fallback location
		if (bestResult == null) {
			bestResult = new Location("");
			bestResult.setLatitude(Float.valueOf(context.getResources().getString(R.string.fallback_latitude)));
			bestResult.setLongitude(Float.valueOf(context.getResources().getString(R.string.fallback_longitude)));
			bestResult.setAccuracy(Float.valueOf(context.getResources().getString(R.string.fallback_accuracy)));
		}
		return bestResult;
	}
	
	public static String getDeviceId(Context context) {
		if(context == null)
			return null;
		try {
			TelephonyManager tm  = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if(tm == null)
				return null;
	 		String deviceId = tm.getDeviceId();
			
	 		if(deviceId==null || deviceId.equals("")){
	 			deviceId = getFallbackId(context); 
	 		}
	 		return deviceId;
			
		} catch (SecurityException se){ 
			return getFallbackId(context);
		}catch (Exception e) {
			return null;
		}
	}
	
	private static String getFallbackId(Context context){
		String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		
		if(deviceId==null || deviceId.equals("")){
 			WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
 			if(wm == null || wm.getConnectionInfo() == null)
 				return null;
 			deviceId = wm.getConnectionInfo().getMacAddress(); 
 		}
		return deviceId;
	}

}
