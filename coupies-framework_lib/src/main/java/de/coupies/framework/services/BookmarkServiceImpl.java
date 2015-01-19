package de.coupies.framework.services;

import java.util.List;

import android.content.Context;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Offer;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.services.content.handler.CouponListHandler;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public class BookmarkServiceImpl extends AbstractCoupiesService 
		implements BookmarkService {
	
	public BookmarkServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}

	private void setBookmarkFlagForCoupon(CoupiesSession session, 
			int couponId, boolean inSetBookmark) throws CoupiesServiceException {
		int flag = inSetBookmark ? 1 : 0;
		String url = getAPIUrl("coupons/bookmark", NO_RESULT_HANDLER);
		HttpClient httpClient = createHttpClient(session);
		httpClient.setParameter("couponid", couponId);
		httpClient.setParameter("bookmark", flag);
		consumeService(httpClient.post(url), NO_RESULT_HANDLER);
	}
	
	private void setLikeFlagForCoupon(CoupiesSession session, 
			int couponId, boolean inSetLike) throws CoupiesServiceException {
		int flag = inSetLike ? 1 : 0;
		String url = getAPIUrl("coupons/like", NO_RESULT_HANDLER);
		HttpClient httpClient = createHttpClient(session);
		httpClient.setParameter("couponid", couponId);
		httpClient.setParameter("like", flag);
		consumeService(httpClient.post(url), NO_RESULT_HANDLER);
	}

	private Object getBookmarkedCoupons(Handler handler, CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException {
		String url = getAPIUrl("coupons/bookmarked", handler);
		HttpClient httpClient = createHttpClient(session);
		addCoordinate(coordinate, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Offer> getBookmarkedCoupons(CoupiesSession session, Coordinate coordinate)
			throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		Object result = getBookmarkedCoupons(handler, session, coordinate);
		return (List<Offer>) result;
	}
	
	public void setBookmark(CoupiesSession session, int couponId) throws CoupiesServiceException {
		setBookmarkFlagForCoupon(session, couponId, true);
	}

	public void removeBookmark(CoupiesSession session, int couponId) throws CoupiesServiceException {
		setBookmarkFlagForCoupon(session, couponId, false);
	}
	
	
	public void setLike(CoupiesSession session, int couponId) throws CoupiesServiceException {
		setLikeFlagForCoupon(session, couponId, true);
	}
	
	public void removeLike(CoupiesSession session, int couponId) throws CoupiesServiceException {
		setLikeFlagForCoupon(session, couponId, false);
	}

	public String getBookmarkedCoupons_html(CoupiesSession session, Coordinate coordinate)
			throws CoupiesServiceException {
		return (String) getBookmarkedCoupons(HTML_RESULT_HANDLER, session, coordinate);
	}
}
