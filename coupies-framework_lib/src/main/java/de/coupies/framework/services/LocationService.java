package de.coupies.framework.services;

import java.util.List;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Location;
import de.coupies.framework.services.html.HtmlLocationService;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface LocationService extends HtmlLocationService {

	/**
	 * 
	 * @param session coupies session
	 * @param id locationId
	 * @return
	 * @throws CoupiesServiceException
	 */
	Location getLocation(CoupiesSession session, int id)
			throws CoupiesServiceException;

	/**
	 * Finding locations, in which a specific coupon is offered
	 * 
	 * @param session coupies session
	 * @param coordinate current position
	 * @param couponId couponId
	 * @return locations 
	 * @throws CoupiesServiceException
	 */
	List<Location> getLocationsWithCoupon(CoupiesSession session,
			Coordinate coordinate, int couponId)
			throws CoupiesServiceException;

	/**
	 * Finding locations, in which a specific coupon is offered
	 * 
	 * @param session coupies session
	 * @param coordinate current position
	 * @param couponId couponId
	 * @return locations 
	 * @throws CoupiesServiceException
	 */
	List<Location> getLocationsWithCoupon(CoupiesSession session, int couponId)
			throws CoupiesServiceException;

	
	/**
	 * @param session coupies session
	 * @param coordinate current position
	 * @param radius radius
	 * @param categoryIds category ids
	 * @param limit result limit
	 * @return locations
	 * @throws CoupiesServiceException 
	 */
	List<Location> getLocationsWithCategories(CoupiesSession session, Coordinate coordinate, 
			int inRadius, Integer[] categoryIds, Integer limit) 
			throws CoupiesServiceException;

	/**
	 * Finding locations around a specific position
	 * 
	 * @param session coupies session
	 * @param coordinate current position
	 * @param radius radius
	 * @param limit
	 * @return coupons
	 * @throws CoupiesServiceException 
	 */
	List<Location> getLocationWithPosition(CoupiesSession session, Coordinate coordinate,
			int radius, Integer limit) throws CoupiesServiceException;
	
	void reportLocation(CoupiesSession session, int couponId, int locationId) throws CoupiesServiceException;
	
	List<Location> getLocationSearch(CoupiesSession session, Coordinate coordinate, int couponId, String searchString) throws CoupiesServiceException;
}