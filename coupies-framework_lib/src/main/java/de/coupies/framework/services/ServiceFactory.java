package de.coupies.framework.services;

import de.coupies.framework.http.HttpClientFactory;
/**
 * service factory
 * 
 * @author thomas.volk@denkwerk.com
 * @since 15.12.2010
 *
 */
public interface ServiceFactory {

	HttpClientFactory getHttpClientFactory();
	
	String getAPIBaseUrl();

	void setHttpClientFactory(HttpClientFactory httpClientFactory);
	
	AuthentificationService createAuthentificationService();
	
	BookmarkService createBookmarkService();
	
	CategoryService createCategoryService();
	
	CouponService createCouponService();
	
	LocationService createLocationService();
	
	NewsService createNewsService();
	
	RedemtionService createRedemtionService();
	
	ResourceService createResourceService();
	
	ResourceService createCachedResourceService();
	
	ReceiptService createUploadReceiptService();
	
	PayoutService createPayoutService();
	
	SamsungWalletService createSamsungWalletService();
}
