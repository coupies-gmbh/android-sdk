package de.coupies.framework.services.async;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Context;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.async.tasks.AsyncHtmlLoadingTask.AsyncHtmlLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncOfferLoadingTask.AsyncOfferListLoadingListener;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.utils.URLUtils;

/**
 * 
 * @author lars.eimermacher@coupies.de
 *
 */

public class AsyncCouponService extends AbstractAsyncServices {

	public AsyncCouponService(HttpClientFactory httpClientFactory, Context context, CoupiesSession session) {
		super(httpClientFactory, context);
		this.session = session;
		this.context = context;
	}
	
	public void getCoupons_async(AsyncOfferListLoadingListener listener){
		runAsyncLoadingOfferList(listener, "coupons");
	}

	public void getCoupons_async_html(AsyncHtmlLoadingListener listener){
		runAsyncLoadingHtml(listener, "coupons");
	}

	public void getCouponsWithHighlights_async(AsyncOfferListLoadingListener listener){
		runAsyncLoadingOfferList(listener, "coupons/highlights");
	}

	public void getCouponsWithHighlights_async_html(AsyncHtmlLoadingListener listener){
		runAsyncLoadingHtml(listener, "coupons/highlights");	
	}

	public void getCouponFeed_async(AsyncOfferListLoadingListener listener){
		runAsyncLoadingOfferList(listener, "coupons/feed");
	}

	public void getCouponFeed_async_html(AsyncHtmlLoadingListener listener){
		runAsyncLoadingHtml(listener, "coupons/feed");
	}

	public void getCoupon_async(AsyncOfferListLoadingListener listener, int couponId){
		runAsyncLoadingOfferList(listener, String.format("coupons/%s", couponId));
	}

	public void getCoupon_async_html(AsyncHtmlLoadingListener listener, int couponId){
		runAsyncLoadingHtml(listener, String.format("coupons/%s", couponId));
	}

	public void getCouponsWithCategory_async(AsyncOfferListLoadingListener listener, int categoryId){
		runAsyncLoadingOfferList(listener, String.format("interests/%s/coupons", categoryId));
	}

	public void getCouponsWithCategory_async_html(AsyncHtmlLoadingListener listener, int categoryId){
		runAsyncLoadingHtml(listener, String.format("interests/%s/coupons", categoryId));
	}

	public void getCouponsWithPosition_async(AsyncOfferListLoadingListener listener, Coordinate coordinate, 
			int inRadius){
		setRadius(inRadius);
		String url = "";
		if(coordinate!=null && coordinate.getLatitude()!=null && coordinate.getLongitude()!=null
				&& coordinate.getLatitude()!=-1 && coordinate.getLongitude()!=-1){
			url = String.format("locations/%s/%s/%d/coupons",
					URLUtils.convertFromFloat(coordinate.getLatitude()),
					URLUtils.convertFromFloat(coordinate.getLongitude()),
					inRadius				
					);
		}
		runAsyncLoadingOfferList(listener, url);
	}

	public void getCouponsWithPosition_async_html(AsyncHtmlLoadingListener listener, Coordinate coordinate, 
			int inRadius){
		setRadius(inRadius);
		String url = "";
		if(coordinate!=null && coordinate.getLatitude()!=null && coordinate.getLongitude()!=null
				&& coordinate.getLatitude()!=-1 && coordinate.getLongitude()!=-1){
			url = String.format("locations/%s/%s/%d/coupons",
					URLUtils.convertFromFloat(coordinate.getLatitude()),
					URLUtils.convertFromFloat(coordinate.getLongitude()),
					inRadius				
					);
		}
		runAsyncLoadingHtml(listener, url);
	}

	public void search_async(AsyncOfferListLoadingListener listener, String inQuery){
		String url = "";
		try {
			url = String.format("coupons/search/%s", URLEncoder.encode(inQuery, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		runAsyncLoadingOfferList(listener, url);
	}

	public void search_async_html(AsyncHtmlLoadingListener listener, String inQuery){
		String url = "";
		try {
			url = String.format("coupons/search/%s", URLEncoder.encode(inQuery, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		runAsyncLoadingHtml(listener, url);
	}

	public void getCouponsWithLocation_async(AsyncOfferListLoadingListener listener, int locationId){
		runAsyncLoadingOfferList(listener, String.format("locations/%d/coupons", locationId));
	}

	public void getCouponsWithLocation_async_html(AsyncHtmlLoadingListener listener, int locationId){
		runAsyncLoadingHtml(listener, String.format("locations/%d/coupons", locationId));
	}

	public void getCouponsWithLocationOrderByCategory_async(AsyncOfferListLoadingListener listener){
		runAsyncLoadingOfferList(listener, "coupons/listwithinterests");
	}

	public void getCouponsWithLocationOrderByCategory_async_html(AsyncHtmlLoadingListener listener){
		runAsyncLoadingHtml(listener, "coupons/listwithinterests");
		
		// TODO include in listener
		/*
		 * // MainCategory 0 hinzufügen zur Anzeige von Coupons ohne Category
			for (int i = 0; i < ((List<Offer>)result).size(); i++) {
				if(((List<Offer>)result).get(i).getMainCategory()==null){
					Category leereMainCategory = new Category();
					leereMainCategory.setId(0);
					((List<Offer>)result).get(i).setMainCategory(leereMainCategory);
				}
			}
		 */
	}

	public void getCouponsWithNotifications_async(AsyncOfferListLoadingListener listener){
		runAsyncLoadingOfferList(listener, "coupons/pushed");
	}

	public void getCouponsWithNotifications_async_html(AsyncHtmlLoadingListener listener){
		runAsyncLoadingHtml(listener, "coupons/pushed");
	}

	public void getCouponsWithNotificationsIncludeRead_async(AsyncOfferListLoadingListener listener, int include_read){
		setIncludeRead(include_read);
		runAsyncLoadingOfferList(listener, "coupons/pushed");
	}

	public void getCouponsWithNotificationsIncludeRead_async_html(AsyncHtmlLoadingListener listener, int include_read){
		setIncludeRead(include_read);
		runAsyncLoadingHtml(listener, "coupons/pushed");
	}

	public void getCashbackCoupons_async(AsyncOfferListLoadingListener listener){
		runAsyncLoadingOfferList(listener, "coupons/cashback");
	}

	public void getCashbackCoupons_async_html(AsyncHtmlLoadingListener listener){
		runAsyncLoadingHtml(listener, "coupons/cashback");
	}

	public void getOnlineCoupons_async(AsyncOfferListLoadingListener listener){
		runAsyncLoadingOfferList(listener, "coupons/online");
	}

	public void getOnlineCoupons_async_html(AsyncHtmlLoadingListener listener){
		runAsyncLoadingHtml(listener, "coupons/online");
	}
}
