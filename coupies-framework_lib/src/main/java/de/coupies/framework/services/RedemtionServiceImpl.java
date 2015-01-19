package de.coupies.framework.services;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.content.Context;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Barcode;
import de.coupies.framework.beans.Redemption;
import de.coupies.framework.beans.Reservation;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClient.HttpStatusException;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.services.content.handler.RedemptionHandler;
import de.coupies.framework.services.content.handler.RedemptionListHandler;
import de.coupies.framework.services.content.handler.ReservationHandler;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.utils.URLUtils;

/**
 * @author thomas.volk@denkwerk.com
 * @since 30.08.2010
 * 
 */
public class RedemtionServiceImpl extends AbstractCoupiesService 
		implements RedemtionService {
	
	public RedemtionServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}


	private Object newRedemption(Handler handler,
			CoupiesSession session, Coordinate coordinate, int accuracy,
			int couponTypeId, String stickerCode) throws CoupiesServiceException,
			HttpStatusException {
		String url = getAPIUrl("redemptions/new", handler);
		HttpClient client = createHttpClient(session);
		client.setParameter("latitude", coordinate.getLatitude());
		client.setParameter("longitude", coordinate.getLongitude());
		if (accuracy > 0) {
			client.setParameter("accuracy", accuracy);
		}
		client.setParameter("coupontype_id", String.valueOf(couponTypeId));
		if (stickerCode != null) {
			client.setParameter("sticker_code", stickerCode);
		}
		Object result = consumeService(client.post(url), handler);
		return result;
	}
	
	/**
	 * @author larseimermacher
	 */
	public Object newReserve(CoupiesSession session, int couponId) throws CoupiesServiceException{
		String url = getAPIUrl("reservations/new", new ReservationHandler());
		HttpClient client = createHttpClient(session);
		client.setParameter("couponid", String.valueOf(couponId));
		Object result = consumeService(client.post(url), new ReservationHandler());
		return (Reservation)result;
	}
			
	/**
	 * @author larseimermacher
	 * @throws DocumentParseException 
	 */
	public Object deleteReservation(CoupiesSession session, int couponId) throws CoupiesServiceException{
		String url = getAPIUrl("reservations/delete", new RedemptionHandler());
		HttpClient client = createHttpClient(session);
		client.setParameter("couponid", String.valueOf(couponId));
		Object result = client.post(url);
		return result;
	}
	
	/**
	 * Redeem a Cashback-Coupon with this method. If no progressBar needed you can fill this parameter with null
	 */
	public Redemption redeemCashbackCoupon(CoupiesSession session, Coordinate coordinate, int accuracy,
			int couponTypeId, List<File> image, boolean withProgress, int number_articles) throws CoupiesServiceException, UnsupportedEncodingException {
		DocumentHandler handler = new RedemptionHandler();
		
		String url = getAPIUrl("redemptions/new", handler);
		HttpClient client = createHttpClient(session);
		client.setParameter("latitude", coordinate.getLatitude());
		client.setParameter("longitude", coordinate.getLongitude());
		if (accuracy > 0) {
			client.setParameter("accuracy", accuracy);
		}
		client.setParameter("quantity", String.valueOf(number_articles));
		client.setParameter("coupontype_id", String.valueOf(couponTypeId));
		Object result = consumeService(client.postWithProgress(url, image, withProgress), handler);

		return (Redemption)result;
	}
	
	/**
	 * Redeem a Cashback-Coupon with this method. If no progressBar needed you can fill this parameter with null
	 */
	public String redeemCashbackCoupon_html(CoupiesSession session, Coordinate coordinate, int accuracy,
			int couponTypeId, List<File> image, boolean withProgress, int number_articles) throws CoupiesServiceException, UnsupportedEncodingException {
		
		String url = getAPIUrl("redemptions/new", HTML_RESULT_HANDLER);
		HttpClient client = createHttpClient(session);
		client.setParameter("latitude", coordinate.getLatitude());
		client.setParameter("longitude", coordinate.getLongitude());
		if (accuracy > 0) {
			client.setParameter("accuracy", accuracy);
		}
		client.setParameter("quantity", String.valueOf(number_articles));
		client.setParameter("coupontype_id", String.valueOf(couponTypeId));
		Object result = consumeService(client.postWithProgress(url, image, withProgress), HTML_RESULT_HANDLER);

		return (String)result;
	}
	
	public Barcode redeemCoupon(CoupiesSession session, Coordinate coordinate, 
			int couponId)
			throws CoupiesServiceException {
		return redeemCoupon(session, coordinate, -1, couponId, null);
	}
	
	public Barcode redeemCoupon(CoupiesSession session, Coordinate coordinate, 
			int accuracy, int couponId)
			throws CoupiesServiceException {
		return redeemCoupon(session, coordinate, accuracy, couponId, null);
	}
	
	public Barcode redeemCoupon(CoupiesSession session, Coordinate coordinate, 
			int couponId, String stickerCode)
			throws CoupiesServiceException {
		return redeemCoupon(session, coordinate, -1, couponId, stickerCode);
	}
	
	public Barcode redeemCoupon(CoupiesSession session, Coordinate coordinate, 
			int accuracy, int couponId, String stickerCode)
			throws CoupiesServiceException {
		DocumentHandler handler = new RedemptionHandler();
		Object result = newRedemption(handler, session, coordinate, accuracy, couponId, stickerCode);
		Barcode barcode = new Barcode();
		barcode.setCouponCodeType(((Redemption)result).getType().toString());
		if(((Redemption)result).getType().equals(Redemption.Type.coupiescode))
			barcode.setCoupiesCode(((Redemption)result).getText());
		else
			barcode.setImageUrl(((Redemption)result).getText());
		return barcode;
	}

	public String redeemCoupon_html(CoupiesSession session,
			Coordinate coordinate, int couponTypeId)
			throws CoupiesServiceException {
		return redeemCoupon_html(session, coordinate, -1, couponTypeId, null);
	}
	
	public String redeemCoupon_html(CoupiesSession session,
			Coordinate coordinate, int accuracy, int couponTypeId)
			throws CoupiesServiceException {
		return redeemCoupon_html(session, coordinate, accuracy, couponTypeId, null);
	}

	public String redeemCoupon_html(CoupiesSession session,
			Coordinate coordinate, int couponTypeId, String stickerCode)
			throws CoupiesServiceException {
		return redeemCoupon_html(session, coordinate, -1, couponTypeId, stickerCode);
	}
	
	public String redeemCoupon_html(CoupiesSession session,
			Coordinate coordinate, int accuracy, int couponTypeId, String stickerCode)
			throws CoupiesServiceException {
		return (String) newRedemption(HTML_RESULT_HANDLER, session, 
				coordinate, accuracy, couponTypeId, stickerCode);
	}
	
	@SuppressWarnings("unchecked")
	public List<Redemption> getCashbackHistorie(CoupiesSession session) throws CoupiesServiceException{
		DocumentHandler handler = new RedemptionListHandler();
		
		String url = getAPIUrl("redemptions/cashback", handler);
		HttpClient client = createHttpClient(session);
		
		Object result = consumeService(client.get(url), handler);
		return (List<Redemption>)result;
	}

	public String getBarcodeImageUrl(CoupiesSession session, String url) {		
		String apiKey = getHttpClientFactory().getConnection().getApiKey();
		String apiLevel = getHttpClientFactory().getConnection().getApiLevel();
		
		url = URLUtils.addParameter(url, "api_level", apiLevel);
		url = URLUtils.addParameter(url, "key", apiKey);
		url = URLUtils.addParameter(url, 
				session.getIdentification().getParameterName(), 
				session.getIdentification().getValue());
		return url;
	}
}
