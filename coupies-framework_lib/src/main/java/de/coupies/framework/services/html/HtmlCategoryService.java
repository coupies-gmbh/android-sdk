package de.coupies.framework.services.html;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface HtmlCategoryService {

	String getCategoriesWithPosition_html(
			CoupiesSession session, Coordinate coordinate, 
			double radius) throws CoupiesServiceException;

	
	String getCategory_html(CoupiesSession session, int id)
			throws CoupiesServiceException;

}