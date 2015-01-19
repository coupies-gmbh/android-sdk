package de.coupies.framework.services.html;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface HtmlCouponService {

	String getCouponsWithHighlights_html(CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException;

	String getCoupon_html(CoupiesSession session, Coordinate coordinate, 
			int id) throws CoupiesServiceException;
	
	String getCoupon_html(CoupiesSession session, 
			int id) throws CoupiesServiceException;

	String getCouponsWithCategory_html(CoupiesSession session, 
			Coordinate coordinate, Integer radius, int categoryId)
			throws CoupiesServiceException;

	String getCouponsWithPosition_html(
			CoupiesSession session, Coordinate coordinate, int inRadius, 
			Integer limit) throws CoupiesServiceException;

	String search_html(
			CoupiesSession session, Coordinate coordinate,
			String inQuery, Integer limit) throws CoupiesServiceException;

	String search_html(
			CoupiesSession session, String inQuery, Integer limit) throws CoupiesServiceException;

	
	String getCouponsWithLocation_html(CoupiesSession session,
			Coordinate coordinate, int locationId, Integer limit)
			throws CoupiesServiceException;

	String getCouponsWithCategory_html(CoupiesSession session, Integer radius,
			int categoryId) throws CoupiesServiceException;
	
	String getCouponsWithHighlights_html(CoupiesSession session) 
			throws CoupiesServiceException;

}