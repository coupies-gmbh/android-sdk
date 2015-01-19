package de.coupies.framework.services;

import java.util.List;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Offer;
import de.coupies.framework.services.html.HtmlCouponService;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface CouponService extends HtmlCouponService {

	/** 
	 * <em>This isn´t the default function.</br><b>Use getCouponFeed() instead.</b></em>
	 * </br></br>
	 * Native function requesting a list of offers highlighted by COUPIES
	 * 
	 * @param session CoupiesSession or an PartnerSession 
	 * @param coordinate current position
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCouponsWithHighlights(CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException;
	
	/**
	 * <em>This is the <b>default function</b> for partners using the native list.</em>
	 * </br></br>
	 * Native function requesting a list of offers with highest success
	 *  
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCoupons(CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException;
	
	/**
	 * <em>This is the <b>default function</b> for partners using the native list.</em>
	 * </br></br>
	 * Native function requesting a list of offers with highest success
	 *  
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCouponFeed(CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException;
	
	/**
	 * <em>This is the <b>default function</b> for partners using the HTML list.</em>
	 * </br></br>
	 * HTML function requesting a list of offers with highest success
	 * 
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @return HTML list of offers
	 * @throws CoupiesServiceException
	 */
	public String getCouponFeed_html(CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException;
	
	/**
	 * <em>This is the <b>default function</b> for partners to request an coupon.</em>
	 * </br></br>
	 * Native function requesting a coupon with the couponId
	 *  
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @param id couponId
	 * @return a coupon
	 * @throws CoupiesServiceException
	 */
	Offer getCoupon(CoupiesSession session, Coordinate coordinate, 
			int id) throws CoupiesServiceException;

	/**
	 * <em>Don´t use this function because it´s <b>deprecated</b></em>
	 * 
	 * @deprecated use getCoupon(session, coordinate, id) instead
	 * @param session CoupiesSession or an PartnerSession
	 * @param id couponId
	 * @return offers
	 * @throws CoupiesServiceException
	 */
	@Deprecated
	Offer getCoupon(CoupiesSession session, 
			int id) throws CoupiesServiceException;

	
	/**
	 * Native function requesting a list of coupons with an specified categoryId
	 * 
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @param categoryId the id of the category
	 * @param radius defines a radius in meters
	 * @return a list of offers
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCouponsWithCategory(CoupiesSession session, 
			Coordinate coordinate, Integer radius, int categoryId)
			throws CoupiesServiceException;

	/**
	 * Native function requesting a list of coupons inside the specified radius
	 * 
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @param inRadius defines a radius in meters
	 * @param limit defines the maximal size of results.</br>Insert null for all
	 * @return a list of offers
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCouponsWithPosition(
			CoupiesSession session, Coordinate coordinate, int inRadius, 
			Integer limit) throws CoupiesServiceException;

	/**
	 * Native function requesting a list of coupons with an search query
	 * 
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @param inQuery search query
     * @param limit maximal size of result list
	 * @return a list of search results (offers)
	 * @throws CoupiesServiceException
	 */
	List<Offer> search(
			CoupiesSession session, Coordinate coordinate,
			String inQuery, Integer limit) throws CoupiesServiceException;

	/**
	 * <em>Don´t use this function because it´s <b>deprecated</b></em>
	 * 
	 * @deprecated use search(session, coordinate, inQuery, limit) instead
	 * @param session CoupiesSession or an PartnerSession
	 * @param inQuery search query
     * @param limit maximal size of result list
	 * @return a list of search results (offers)
	 * @throws CoupiesServiceException 
	 */
	
	List<Offer> search(
			CoupiesSession session,
			String inQuery, Integer limit) throws CoupiesServiceException;

	/**
	 * <em>Used for <b>Maps</b></em></br>
	 * Native function to request offers from a location.</br>The store is meant by "location". 
	 * 
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @param locationId the id of the shop
	 * @param limit maximal size of result list.</br>Insert null for all
	 * @return a list of offers
	 * @throws CoupiesServiceException 
	 */
	List<Offer> getCouponsWithLocation(CoupiesSession session,
			Coordinate coordinate, int locationId, Integer limit)
			throws CoupiesServiceException;
	
	/**
	 * <em>Used for <b>CustomerCoupons</b></em></br>
	 * Native function to request all offers from one customer.</br>. 
	 * 
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @param customerId the id of the customer
	 * @param limit maximal size of result list.</br>Insert null for all
	 * @return a list of offers
	 * @throws CoupiesServiceException 
	 */
	List<Offer> getCustomerCoupons(CoupiesSession session,
			Coordinate coordinate, int customerId, Integer limit)
			throws CoupiesServiceException;
	
	/**
	 * <b>Requesting offers without coordinate is deprecated</b>
     * 
     * @deprecated use getCouponsWithCategory(session, coordinate, radius, categoryId) instead 
	 * @param session CoupiesSession or an PartnerSession
	 * @param radius radius
	 * @param categoryId category
	 * @return offers  
	 * 
	 */
	
	List<Offer> getCouponsWithCategory(CoupiesSession session, Integer radius,
			int categoryId) throws CoupiesServiceException;
	
	/**
	 * <b>Requesting offers without coordinate is deprecated</b>
     * 
     * @deprecated use getCouponsWithCategory(session, coordinate, radius, categoryId) instead 
	 * @param session CoupiesSession or an PartnerSession
	 * @param categoryId category
	 * @return offers  
	 * 
	 */
	
	List<Offer> getCouponsWithCategory(CoupiesSession session,
			int categoryId) throws CoupiesServiceException;
	
	/**
	 * <b>Requesting offers without coordinate is deprecated</b>
	 * 
	 * @deprecated use getCouponsWithHighlights(session, coordinate, limit) instead
	 * @param session CoupiesSession or an PartnerSession
	 * @return offers
	 * @throws CoupiesServiceException
	 */
	
	List<Offer> getCouponsWithHighlights(CoupiesSession session) 
		throws CoupiesServiceException;

	/**
	* <b>Requesting offers without coordinate is deprecated</b>
	 * 
	 * @deprecated use getCouponsWithHighlights(session, coordinate, limit) instead
	 * @param session session
	 * @param limit limit
	 * @return offers
	 * @throws CoupiesServiceException
	 */
	
	List<Offer> getCouponsWithHighlights(CoupiesSession session, Integer limit) 
		throws CoupiesServiceException;

	/** 
	 * <em>This isn´t the default function.</br><b>Use getCouponFeed() instead.</b></em>
	 * </br></br>
	 * Native function requesting a list of offers highlighted by COUPIES
	 * 
	 * @param session CoupiesSession or an PartnerSession 
	 * @param coordinate current position
	 * @param limit maximal size of result list.
	 * </br>Insert null or use getCouponsWithHighlights(session, coordinate) for all
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCouponsWithHighlights(CoupiesSession session, 
			Coordinate coordinate, Integer limit) throws CoupiesServiceException;
	
	/**
	 * Native function to request offers with a location order by category.
	 * </br>An store is meant by "location".  
	 * 
	 * @param session CoupiesSession or an PartnerSession 
	 * @param coordinate current position
	 * @param inLimit maximal size of result list.
	 * </br>Insert null or use getCouponsWithHighlights(session, coordinate) for all
	 * </br>default limit is 300
	 * @param radius defines a radius in meters
	 * @return a list of offer objects
	 * @throws CoupiesServiceException 
	 */
	List<Offer> getCouponsWithLocationOrderByCategory(CoupiesSession session, 
			Coordinate coordinate, Integer inLimit, Integer radius
			) throws CoupiesServiceException;

	/** 
	 * <em>This isn´t the default function.</br><b>Use getCouponFeed() instead.</b></em>
	 * </br></br>
	 * Native function requesting a list of offers highlighted by COUPIES
	 * 
	 * @param session CoupiesSession or an PartnerSession 
	 * @param coordinate current position
	 * @param limit maximal size of result list
	 * @param radius defines a radius in meters
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCouponsWithHighlights(CoupiesSession session, 
			Coordinate coordinate, Integer limit, Integer radius) throws CoupiesServiceException;
	
	/**
	 * <b>Requesting offers without coordinate is deprecated</b>
	 * 
	 * @deprecated use CouponFeed(session, coordinate) instead
	 * @param session CoupiesSession or an PartnerSession 
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	@Deprecated
	List<Offer> getCouponFeed(CoupiesSession session) throws CoupiesServiceException;
	
	/**
	 * Native function requesting a list of offers pushed for this user.
	 * 
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCouponsWithNotifications(CoupiesSession session, Coordinate coordinate) throws CoupiesServiceException;
	
	/**
	 * Native function requesting a list of offers pushed for this user, including the already read Notifications.
	 * 
	 * @param session CoupiesSession or an PartnerSession
	 * @param coordinate current position
	 * @param include_read decides whether the already read messages are displayed or not
	 * </br> 1 = with
	 * </br> 0 = without
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCouponsWithNotificationsIncludeRead(CoupiesSession coupiesSession, Coordinate currentLocation, int include_read) throws CoupiesServiceException;
	
	/**
	 * Native function requesting a list of cashback offers
	 * 
	 * @param coupiesSession CoupiesSession or an PartnerSession
	 * @param currentLocation current position
	 * @param limit maximal size of result list
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getCashbackCoupons(CoupiesSession coupiesSession, Coordinate currentLocation, int limit) throws CoupiesServiceException;
	
	/**
	 * Native function requesting a list of online offers and deals
	 * 
	 * @param coupiesSession CoupiesSession or an PartnerSession
	 * @param currentLocation current position
	 * @param limit maximal size of result list
	 * @return a list of offer objects
	 * @throws CoupiesServiceException
	 */
	List<Offer> getOnlineCoupons(CoupiesSession coupiesSession, Coordinate currentLocation, int limit) throws CoupiesServiceException;
}