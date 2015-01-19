package de.coupies.framework.utils;

import java.io.IOException;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import android.content.Context;
import android.content.SharedPreferences;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.ServiceFactoryImpl.AdvertiseListener;

public class StoreAdvertisingID {
	static final String TAG = "Store AdvertiseId";
	static final String ANDROID_AID = "coupies_advertise_session";
	static final String COUPIES_ADVERTISER_PREFERENCES = "advertiser_id_preferences";
	SharedPreferences prefs;
	Context mContext;
	HttpClientFactory inFactory;
	String advertiseId;
	AdvertiseListener listener;
    
    public StoreAdvertisingID(Context mContext, HttpClientFactory httpClientFactory) {
		this.mContext=mContext;
		inFactory= httpClientFactory;
	}
    
	public String getAdvertiseId(AdvertiseListener inListener){
		advertiseId = isAdvertiseIdAvailable(mContext);
		
        if (advertiseId == null || advertiseId.length()<=0 || advertiseId.equals("")) {
        	advertiseId = getAdvertiseIdFromGServices(mContext);
        }
        
        inListener.storeAdvertiserId(advertiseId);
        return advertiseId;
	}
	
	private String isAdvertiseIdAvailable(Context adContext){
		prefs = adContext.getSharedPreferences(COUPIES_ADVERTISER_PREFERENCES, Context.MODE_PRIVATE);
    	
        String mAdvertId = prefs.getString(ANDROID_AID, null);
        if (mAdvertId == null || mAdvertId.length()<=0 || mAdvertId.equals("")) {
        	return "";
        }
        return mAdvertId;
	}

	
    private String getAdvertiseIdFromGServices(Context context) {
		Info adInfo = null;
		  try {
			  adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
		  } catch (IllegalStateException e) {
			  e.printStackTrace();
		  } catch (GooglePlayServicesRepairableException e) {
			  e.printStackTrace();
		  }catch (IOException e) {
		    // Unrecoverable error connecting to Google Play services (e.g.,
		    // the old version of the service doesn't support getting AdvertisingId).
			  e.printStackTrace();
		  } catch (GooglePlayServicesNotAvailableException e) {
		    // Google Play services is not available entirely.
			  e.printStackTrace();
		  }
		  
		  @SuppressWarnings("unused")
		  boolean isLAT;
		  
		  if(adInfo!=null){
			  advertiseId = adInfo.getId();
			  isLAT = adInfo.isLimitAdTrackingEnabled();
			   
			  prefs = context.getSharedPreferences(COUPIES_ADVERTISER_PREFERENCES, Context.MODE_PRIVATE);
			  prefs.edit().putString(ANDROID_AID, advertiseId).commit();
	
			  return advertiseId;
		  }
		  
		  return null;
    }	
}
