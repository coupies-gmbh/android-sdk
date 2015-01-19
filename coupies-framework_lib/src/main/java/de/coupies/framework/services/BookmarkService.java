package de.coupies.framework.services;

import java.util.List;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Offer;
import de.coupies.framework.services.html.HtmlBookmarkService;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface BookmarkService extends HtmlBookmarkService {

	/**
	 * get all bookmarked coupons
	 * 
	 * @param session coupies session
	 * @return coupons
	 * @throws CoupiesServiceException
	 */
	List<Offer> getBookmarkedCoupons(CoupiesSession session, Coordinate coordinate)
			throws CoupiesServiceException;

	/**
	 * set bookmark
	 * 
	 * @param session coupies session
	 * @param couponId couponId
	 * @throws CoupiesServiceException
	 */
	void setBookmark(CoupiesSession session, int couponId)
			throws CoupiesServiceException;

	/**
	 * remove bookmark
	 * 
	 * @param session coupies session
	 * @param couponId couponId
	 * @throws CoupiesServiceException
	 */
	void removeBookmark(CoupiesSession session, int couponId)
			throws CoupiesServiceException;
	
	/**
	 * like a coupon
	 * 
	 * @param session coupies session
	 * @param couponId couponId
	 * @throws CoupiesServiceException
	 */
	public void setLike(CoupiesSession session, int couponId) throws CoupiesServiceException;
	
	/**
	 * unlike a coupon
	 * 
	 * @param session coupies session
	 * @param couponId couponId
	 * @throws CoupiesServiceException
	 */
	public void removeLike(CoupiesSession session, int couponId) throws CoupiesServiceException;
}