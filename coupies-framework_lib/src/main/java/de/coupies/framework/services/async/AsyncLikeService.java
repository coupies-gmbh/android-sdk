package de.coupies.framework.services.async;

import android.content.Context;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.async.tasks.AsyncLikeTask.AsyncLikeListener;

/**
 *@author lars.eimermacher@coupies.de
 */
public class AsyncLikeService extends AbstractAsyncServices {
	
	public AsyncLikeService(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}

	public void setLike(AsyncLikeListener listener, int couponId){
		setCouponId(couponId);
		setLike(true);
		
		runAsyncLike(listener, "coupons/like");
	}
	
	public void removeLike(AsyncLikeListener listener, int couponId){
		setCouponId(couponId);
		setLike(false);
		
		runAsyncLike(listener, "coupons/like");
	}
}
