package de.coupies.framework.services;

import android.content.Context;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.utils.StoreAdvertisingID;
/**
 * service factory
 * 
 * @author thomas.volk@denkwerk.com
 * @since 15.12.2010
 *
 */
public class ServiceFactoryImpl implements ServiceFactory {
	private HttpClientFactory httpClientFactory;
	private Context context;
	private static String advertiserId;
	
	public class AdvertiseListener {
		public void storeAdvertiserId(String advertiserId){
			ServiceFactoryImpl.advertiserId = advertiserId;
		}
	}

	public ServiceFactoryImpl() {
		super();
	}

	public ServiceFactoryImpl(final HttpClientFactory httpClientFactory, final Context context) {
		this.httpClientFactory = httpClientFactory;
		this.context = context;
		
		if(advertiserId == null){
			new Thread(new Runnable() {
				@Override
				public void run() {
					new StoreAdvertisingID(context, httpClientFactory).getAdvertiseId(new AdvertiseListener());
				}
			}).start();	
		}
	}

	public HttpClientFactory getHttpClientFactory() {
		return httpClientFactory;
	}

	public void setHttpClientFactory(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}
	
	public AuthentificationService createAuthentificationService() {
		return new AuthentificationServiceImpl(getHttpClientFactory(), context, advertiserId);
	}
	
	public BookmarkService createBookmarkService() {
		return new BookmarkServiceImpl(getHttpClientFactory(), context);
	}
	
	public CategoryService createCategoryService() {
		return new CategoryServiceImpl(getHttpClientFactory(), context);
	}
	
	public CouponService createCouponService() {
		return new CouponServiceImpl(getHttpClientFactory(), context);
	}
	
	public LocationService createLocationService() {
		return new LocationServiceImpl(getHttpClientFactory(), context);
	}
	
	public NewsService createNewsService() {
		return new NewsServiceImpl(getHttpClientFactory(), context);
	}
	
	public RedemtionService createRedemtionService() {
		return new RedemtionServiceImpl(getHttpClientFactory(), context);
	}
	
	public ReceiptService createUploadReceiptService() {
		return new ReceiptServiceImpl(getHttpClientFactory(), context);
	}
	
	public ResourceService createResourceService() {
		return new ResourceServiceImpl(getHttpClientFactory());
	}
	
	public ResourceService createCachedResourceService() {
		return new CachedResourceServiceWrapper(createResourceService());
	}
	
	public PayoutService createPayoutService(){
		return new PayoutServiceImpl(getHttpClientFactory(), context);
	}
	
	public SamsungWalletService createSamsungWalletService(){
		return new SamsungWalletServiceImpl(getHttpClientFactory(), context);
	}

	public String getAPIBaseUrl() {
		return getHttpClientFactory().getConnection().getAPIBaseUrl();
	}
}
