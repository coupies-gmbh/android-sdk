package de.coupies.framework.services.async;

import android.content.Context;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.async.tasks.AsyncBookmarkTask.AsyncBookmarkLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncHtmlLoadingTask.AsyncHtmlLoadingListener;

/**
 * @author lars.eimermacher@coupies.de
 *
 */
public class AsyncBookmarkService extends AbstractAsyncServices {
	
	public AsyncBookmarkService(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}
	
	public void getBookmarkedCoupons_async(AsyncBookmarkLoadingListener listener) {
		runAsyncLoadingBookmarkOfferList(listener, "coupons/bookmarked");
		
	}
	
	public void getBookmarkedCoupons_async_html(AsyncHtmlLoadingListener listener) {
		runAsyncLoadingHtml(listener, "coupons/bookmarked");
	}

	public void setBookmark_async(AsyncBookmarkLoadingListener listener, int couponId) {
		setBookmark(true);
		setCouponId(couponId);
		
		runAsyncBookmark(listener, "coupons/bookmark");
	}

	public void removeBookmark_async(AsyncBookmarkLoadingListener listener, int couponId) {
		setBookmark(false);
		setCouponId(couponId);
		
		runAsyncBookmark(listener, "coupons/bookmark");
	}
}
