package de.coupies.framework.services.async;

import android.content.Context;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.async.tasks.AsyncLocationLoadingTask.AsyncLocationListLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncLocationLoadingTask.AsyncLocationLoadingListener;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.utils.URLUtils;

/**
 * @author larseimermacher
 */

public class AsyncLocationService extends AbstractAsyncServices {
	
	public AsyncLocationService(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}
	

	public void getLocation_async(AsyncLocationLoadingListener listener, int locationId) {	
		runAsyncLoadingLocation(listener, String.format("locations/%s", locationId));
	}

	public void getLocationsWithCoupon_async(AsyncLocationListLoadingListener listener, int couponId) {
		runAsyncLoadingLocationList(listener, String.format("coupons/%s/locations", couponId));
	}

	public void getLocationWithPosition_async(AsyncLocationListLoadingListener listener, Coordinate coordinate,
			int radius, Integer limit) {
		setLimit(limit);
		String url = "";
		
		if(coordinate!=null){
			url = String.format("locations/%s/%s/%d",
				URLUtils.convertFromFloat(coordinate.getLatitude()),
				URLUtils.convertFromFloat(coordinate.getLongitude()),
				radius				
				);
		}else{
			url = String.format("locations/%s/%s/%d",""+50, ""+6, radius);
		}
		
		runAsyncLoadingLocationList(listener, url);
	}
}
