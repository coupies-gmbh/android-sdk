package de.coupies.framework.services.html;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

public interface HtmlRedemtionService {

	String redeemCoupon_html(CoupiesSession session, Coordinate coordinate, 
			int couponTypeId, String stickerCode)
			throws CoupiesServiceException;
	
	String redeemCoupon_html(CoupiesSession session, Coordinate coordinate, 
			int couponTypeId)
			throws CoupiesServiceException;
	
	String redeemCoupon_html(CoupiesSession session, Coordinate coordinate, 
			int accuracy, int couponTypeId, String stickerCode)
			throws CoupiesServiceException;
	
	String redeemCoupon_html(CoupiesSession session, Coordinate coordinate, 
			int accuracy, int couponTypeId)
			throws CoupiesServiceException;

	String getBarcodeImageUrl(CoupiesSession session, String url);
}