package de.coupies.framework.services.html;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface HtmlBookmarkService {

	String getBookmarkedCoupons_html(CoupiesSession session, Coordinate coordinate)
			throws CoupiesServiceException;



}