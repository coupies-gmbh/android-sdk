package de.coupies.framework.services.html;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface HtmlLocationService {

	String getLocation_html(CoupiesSession session, int id)
			throws CoupiesServiceException;

	String getLocationsWithCoupon_html(CoupiesSession session,
			Coordinate coordinate, int couponId)
			throws CoupiesServiceException;
	
	String getLocationsWithCoupon_html(CoupiesSession session, int couponId)
			throws CoupiesServiceException;

	String getLocationsWithCategories_html(CoupiesSession session, Coordinate coordinate, 
			int inRadius, Integer[] inCategoryIds, Integer limit) 
			throws CoupiesServiceException;
	
	String getLocationsWithPosition_html(CoupiesSession session, Coordinate coordinate, 
			int inRadius, Integer limit) throws CoupiesServiceException;
}