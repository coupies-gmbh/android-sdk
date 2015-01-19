package de.coupies.framework.services;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Barcode;
import de.coupies.framework.beans.Redemption;
import de.coupies.framework.http.HttpClient.HttpStatusException;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.services.html.HtmlRedemtionService;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface RedemtionService extends HtmlRedemtionService {

	/**
	 * redeem a coupon
	 * 
	 * @param session coupies session
	 * @param coordinate current position
	 * @param couponId couponId
	 * @return
	 * @throws CoupiesServiceException
	 */
	Barcode redeemCoupon(CoupiesSession session, Coordinate coordinate, 
			int couponId)
			throws CoupiesServiceException;
	
	/**
	 * redeem a coupon
	 * 
	 * @param session coupies session
	 * @param coordinate current position
	 * @param accuracy accuracy of current position in meters
	 * @param couponId couponId
	 * @return
	 * @throws CoupiesServiceException
	 */
	Barcode redeemCoupon(CoupiesSession session, Coordinate coordinate, 
			int accuracy, int couponId)
			throws CoupiesServiceException;
	
	/**
	 * Method to redeem an Cashback coupon
	 * 
	 * @param session coupies session
	 * @param coordinate current position
	 * @param accuracy accuracy of current position in meters
	 * @param couponTypeId couponId
	 * @param image Cashback Bon image
	 * @return	Returns an Redemption-Object
	 * @throws DocumentParseException
	 * @throws HttpStatusException
	 * @throws UnsupportedEncodingException 
	 */
	
	Redemption redeemCashbackCoupon(CoupiesSession session, Coordinate coordinate, int accuracy,
			int couponTypeId, List<File> image, boolean withProgress, int number_articles) throws CoupiesServiceException, UnsupportedEncodingException; 
			
	String redeemCashbackCoupon_html(CoupiesSession session, Coordinate coordinate, int accuracy,
			int couponTypeId, List<File> image, boolean withProgress, int number_articles) throws CoupiesServiceException, UnsupportedEncodingException;
	
	/**
	 * redeem a coupon
	 * 
	 * @param session coupies session
	 * @param coordinate current position
	 * @param couponId couponId
	 * @param stickerCode Code of the sticker in the Location
	 * @return
	 * @throws CoupiesServiceException
	 */
	Barcode redeemCoupon(CoupiesSession session, Coordinate coordinate, 
			int couponId, String stickerCode)
			throws CoupiesServiceException;
	
	/**
	 * redeem a coupon
	 * 
	 * @param session coupies session
	 * @param coordinate current position
	 * @param accuracy accuracy of current position in meters
	 * @param couponId couponId
	 * @param stickerCode Code of the sticker in the Location
	 * @return
	 * @throws CoupiesServiceException
	 */
	Barcode redeemCoupon(CoupiesSession session, Coordinate coordinate, 
			int accuracy, int couponId, String stickerCode)
			throws CoupiesServiceException;

	/**
	 * add coupies speciffic backend parameters to the image url
	 * 
	 * @param session coupies session
	 * @param url image url
	 * @return barcode url
	 */
	public String getBarcodeImageUrl(CoupiesSession session, String url);
	
	/**
	 * Lars
	 * 
	 * @param session coupies session
	 * @param couponId ID des Coupons
	 * @throws HttpStatusException 
	 * @throws DocumentParseException 
	 * @return Return an Reservation-Object
	 */
	public Object newReserve(CoupiesSession session, int couponId) throws CoupiesServiceException;
	
	/**
	 * Get a List of all Cashback-redemptions
	 * 
	 * @param session
	 * @return
	 * @throws DocumentParseException
	 * @throws HttpStatusException
	 */
	public List<Redemption> getCashbackHistorie(CoupiesSession session) throws CoupiesServiceException;
	
	/**
	 * @author larseimermacher
	 * @param coupiesSession
	 * @param couponId
	 * @return
	 * @throws DocumentParseException 
	 */
	public Object deleteReservation(CoupiesSession coupiesSession, int couponId) throws CoupiesServiceException;

}