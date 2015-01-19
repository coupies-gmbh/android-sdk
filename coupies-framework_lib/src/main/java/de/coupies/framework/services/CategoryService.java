package de.coupies.framework.services;

import java.util.List;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Category;
import de.coupies.framework.services.html.HtmlCategoryService;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface CategoryService extends HtmlCategoryService {

	/**
	 * get categories (interests) offered in a specific radius (in meters)
	 * 
	 * @param session coupies session
	 * @param coordinate  current position
	 * @param radius radius in meters
	 * @return all categories near the coordinate
	 * @throws CoupiesServiceException
	 */
	List<Category> getCategoriesWithPosition(
			CoupiesSession session, Coordinate coordinate, 
			double radius) throws CoupiesServiceException;

	/**
	 * get a category
	 * 
	 * @param session coupies session
	 * @param id categoryId
	 * @return category
	 * @throws CoupiesServiceException
	 */
	Category getCategory(CoupiesSession session, int id)
			throws CoupiesServiceException;

}