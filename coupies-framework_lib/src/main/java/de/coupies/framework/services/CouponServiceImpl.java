package de.coupies.framework.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Category;
import de.coupies.framework.beans.Coupon;
import de.coupies.framework.beans.Offer;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClient.HttpStatusException;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.services.content.handler.CouponListHandler;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.utils.URLUtils;

public class CouponServiceImpl extends AbstractCoupiesService 
		implements CouponService {

	public CouponServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}

	private Object getCouponsWithHighlights(Handler handler,
			CoupiesSession session, Coordinate coordinate, Integer limit)
			throws CoupiesServiceException {
		return getCouponsWithHighlights(handler, session, coordinate, limit, null);
	}
	
	private Object getCouponsWithHighlights(Handler handler,
			CoupiesSession session, Coordinate coordinate, Integer limit, Integer radius)
			throws CoupiesServiceException {
		String url = getAPIUrl("coupons/highlights", handler);
		HttpClient httpClient = createHttpClient(session);
		addCoordinate(coordinate, httpClient);
		addLimit(limit, httpClient);
		if(radius != null) {
			httpClient.setParameter("radius", radius);
		}
		Object result = consumeService(httpClient.get(url), handler);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Offer> getCoupons(CoupiesSession session, 
			Coordinate coordinate) throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		Object result = getCoupons(handler, session, coordinate);
		return (List<Offer>) result;
	}
	
	private Object getCoupons(Handler handler, CoupiesSession session, Coordinate coordinate)
			throws CoupiesServiceException {
		String url = getAPIUrl("coupons", handler);
		HttpClient httpClient = createHttpClient(session);
//		addCoordinate(coordinate, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return result;
	}
	
	private Object getCouponFeed(Handler handler,
			CoupiesSession session, Coordinate coordinate)
			throws CoupiesServiceException {
		String url = getAPIUrl("coupons/feed", handler);
		HttpClient httpClient = createHttpClient(session);
		addCoordinate(coordinate, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return result;
	}
	
	private Object getCoupon(Handler handler, CoupiesSession session,
			Coordinate coordinate, int id)
			throws CoupiesServiceException {
		String url = getAPIUrl(String.format("coupons/%s", id), handler);
		HttpClient httpClient = createHttpClient(session);
		addCoordinate(coordinate, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return result;
	}
	
	/**
	 * @author larseimermacher
	 * @param handler
	 * @param session
	 * @param coordinate
	 * @param limit Default=300
	 * @return
	 * @throws DocumentParseException
	 * @throws HttpStatusException
	 */
	private Object getCouponsWithLocationOrderByCategory(Handler handler,
			CoupiesSession session, Coordinate coordinate, 
			Integer limit, Integer radius) throws CoupiesServiceException {
		String url = getAPIUrl("coupons/listwithinterests", handler);
		HttpClient client = createHttpClient(session);
		addCoordinate(coordinate, client);
		addLimit(limit, client);
		client.setParameter("radius", radius);
		Object result = consumeService(client.get(url), handler);
		return result;
	}
	
	private Object getCouponsWithCategory(Handler handler,
			CoupiesSession session, Coordinate coordinate, 
			Integer radius, int categoryId) throws CoupiesServiceException {
		String url = getAPIUrl(String.format("interests/%s/coupons",
				categoryId), handler);
		HttpClient client = createHttpClient(session);
		addCoordinate(coordinate, client);
		if(radius != null) {
			client.setParameter("radius", radius);
		}
		Object result = consumeService(client.get(url),handler);
		return result;
	}
	private Object getCoupons(Handler handler,
			CoupiesSession session, Coordinate coordinate, int inRadius, 
			Integer limit) throws CoupiesServiceException {
		HttpClient client = createHttpClient(session);
		addLimit(limit, client);
		String url;
		if(coordinate!=null && coordinate.getLatitude()!=null && coordinate.getLongitude()!=null
				&& coordinate.getLatitude()!=-1 && coordinate.getLongitude()!=-1){
			url = getAPIUrl(String.format("locations/%s/%s/%d/coupons",
					URLUtils.convertFromFloat(coordinate.getLatitude()),
					URLUtils.convertFromFloat(coordinate.getLongitude()),
					inRadius				
					), handler);
		}else{
			addCoordinate(coordinate, client);
			url = getAPIUrl("/coupons", handler);
		}
		
		Object result = consumeService(client.get(url), handler);
		return result;
	}
	private Object search(Handler handler,
			CoupiesSession session, Coordinate coordinate, 
			String inQuery, Integer limit) throws CoupiesServiceException {
		String url;
		try {
			url = getAPIUrl(String.format("coupons/search/%s", URLEncoder.encode(inQuery, "UTF-8")), handler);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		HttpClient client = createHttpClient(session);
		addCoordinate(coordinate, client);
		addLimit(limit, client);
		Object result = consumeService(client.get(url), handler);
		return result;
	}
	private Object getCouponsWithLocation(Handler handler,
			CoupiesSession session, Coordinate coordinate, int locationId, 
			Integer limit) throws CoupiesServiceException {
		String url = getAPIUrl(String.format("locations/%d/coupons", locationId), handler);
		HttpClient client = createHttpClient(session);
		addCoordinate(coordinate, client);
		addLimit(limit, client);
		Object result = consumeService(client.get(url), handler);
		return result;
	}
	
	/**
	 * The native function to request an coupon from the coupies-api
	 * 
	 * @param session: The PartnerSession
	 * @param coordinate: The current coordinate of the user
	 * @param id: The id of the coupon
	 * 
	 * @return return the native coupon object requested at coupies-api 
	 */
	public Offer getCoupon(CoupiesSession session, Coordinate coordinate, 
			int id) throws CoupiesServiceException {
		
		DocumentHandler handler = new DocumentHandler() {
			public Object handleDocument(Document doc)
					throws CoupiesServiceException {
				NodeList errorNode = doc.getElementsByTagName("error");
				if(errorNode != null && errorNode.item(0)!=null){
					errorNode = errorNode.item(0).getChildNodes();
					if(errorNode != null){
						ValidationParser mValidationParser = new ValidationParser();
						mValidationParser.parseAndThrow(doc);
					}
				}
				
				// Coupon auslesen
				NodeList couponNodes = doc.getElementsByTagName("coupon");
				Node couponNode = couponNodes.item(0);
				if(couponNode == null) {
					couponNodes = doc.getElementsByTagName("deal");
					couponNode = couponNodes.item(0);
				}
				if (couponNode == null){
					couponNodes = doc.getElementsByTagName("promotion");
					couponNode = couponNodes.item(0);
				}
				if(couponNode == null) {
					couponNodes = doc.getElementsByTagName("offer");
					couponNode = couponNodes.item(0);
					if(couponNode == null) {
						getValidationParser().parseAndThrow(doc);
					}
				}
				
				return CouponListHandler.parseOffer(couponNode);
			}
		};
		if(coordinate != null && coordinate.getLatitude() == -1 && coordinate.getLongitude() == -1)
			coordinate = null;
		Object result = getCoupon(handler, session, coordinate, id);
		if(result instanceof Coupon){
			return (Coupon)result;
		}else{
			return (Offer)result;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Offer> getCouponsWithCategory(CoupiesSession session, 
			Coordinate coordinate, Integer radius, int categoryId) 
		throws CoupiesServiceException  {
		CouponListHandler handler = new CouponListHandler();
		Object result = getCouponsWithCategory(handler, session, 
				coordinate, radius, categoryId);
		return (List<Offer>) result;
	}

	@SuppressWarnings("unchecked")
	public List<Offer> getCouponsWithPosition(CoupiesSession session, Coordinate coordinate, 
			int inRadius, Integer limit) throws CoupiesServiceException  {
		CouponListHandler handler = new CouponListHandler();
		Object result = getCoupons(handler, session, 
				coordinate, inRadius, limit);
		return (List<Offer>) result;
	}
	
	// Neue Methode für die DemoApp
	public String getCouponsWithPositionHTML(CoupiesSession session, Coordinate coordinate, 
			int inRadius, Integer limit) throws CoupiesServiceException  {
		return (String) getCoupons(HTML_RESULT_HANDLER, session, coordinate, inRadius, limit);
	}

	@SuppressWarnings("unchecked")
	public List<Offer> search(CoupiesSession session, Coordinate coordinate, 
			String inQuery, Integer limit) throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		Object result = search(handler, session, coordinate, inQuery, limit);
		return (List<Offer>) result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Offer> getCouponsWithLocation(CoupiesSession session, 
			Coordinate coordinate, int locationId, Integer inLimit
			) throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		Object result = getCouponsWithLocation(handler, session, coordinate, 
				locationId, inLimit);
		return (List<Offer>) result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Offer> getCustomerCoupons(CoupiesSession session, Coordinate coordinate,
			int customerId, Integer limit) throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		String url = getAPIUrl(String.format("/customers/%d/coupons", customerId), handler);
		HttpClient client = createHttpClient(session);
		addCoordinate(coordinate, client);
		addLimit(limit, client);
		Object result = consumeService(client.get(url), handler);
		return (List<Offer>) result;
	}

	@SuppressWarnings("unchecked")
	public List<Offer> getCouponsWithHighlights(CoupiesSession session, 
			Coordinate coordinate, Integer limit) throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		Object result = getCouponsWithHighlights(handler, session, coordinate, limit);
		return (List<Offer>) result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Offer> getCouponsWithHighlights(CoupiesSession session, 
			Coordinate coordinate, Integer limit, Integer radius) throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		Object result = getCouponsWithHighlights(handler, session, coordinate, limit, radius);
		return (List<Offer>) result;
	}
	
	public String getCouponsWithHighlights_html(CoupiesSession session,
			Coordinate coordinate, Integer limit) throws CoupiesServiceException {
		return (String) getCouponsWithHighlights(HTML_RESULT_HANDLER, session, coordinate, limit);
	}
	
	public String getCouponsWithHighlights_html(CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException {
		return (String) getCouponsWithHighlights(HTML_RESULT_HANDLER, session, coordinate, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Offer> getCouponFeed(CoupiesSession session, 
			Coordinate coordinate) throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		Object result = getCouponFeed(handler, session, coordinate);
		return (List<Offer>) result;
	}
	
	public List<Offer> getCouponFeed(CoupiesSession session)
			throws CoupiesServiceException {
		return getCouponFeed(session, null);
		}
		

	public String getCouponFeed_html(CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException {
		return (String) getCouponFeed(HTML_RESULT_HANDLER, session, coordinate);
	}

	public String getCoupon_html(CoupiesSession session, Coordinate coordinate,
			int id) throws CoupiesServiceException {
		return (String) getCoupon(HTML_RESULT_HANDLER, session, coordinate, id);
	}

	public String getCouponsWithCategory_html(CoupiesSession session,
			Coordinate coordinate, Integer radius, int categoryId)
			throws CoupiesServiceException {
		return (String) getCouponsWithCategory(HTML_RESULT_HANDLER, session, 
				coordinate, radius, categoryId);
	}

	public String getCouponsWithPosition_html(CoupiesSession session, Coordinate coordinate, 
			int inRadius, Integer limit) throws CoupiesServiceException {
		return (String) getCoupons(HTML_RESULT_HANDLER, 
				session, coordinate, inRadius, limit);
	}

	public String search_html(CoupiesSession session, Coordinate coordinate,
			String inQuery, Integer limit) throws CoupiesServiceException {
		return (String) search(HTML_RESULT_HANDLER, session, coordinate, 
				inQuery, limit);
	}

	public String getCouponsWithLocation_html(CoupiesSession session,
			Coordinate coordinate, int locationId, Integer limit)
			throws CoupiesServiceException {
		return (String) getCouponsWithLocation(HTML_RESULT_HANDLER, session, 
				coordinate, locationId, limit);
	}

	public String getCoupon_html(CoupiesSession session, int id)
			throws CoupiesServiceException {
		return getCoupon_html(session, null, id);
	}

	public Offer getCoupon(CoupiesSession session, int id)
			throws CoupiesServiceException {
		return getCoupon(session, id);
	}

	public String search_html(CoupiesSession session, String inQuery,
			Integer limit) throws CoupiesServiceException {
		return search_html(session, null, inQuery, limit);
	}

	public List<Offer> search(CoupiesSession session, String inQuery,
			Integer limit) throws CoupiesServiceException {
		return search(session, null, inQuery, limit);
	}

	public String getCouponsWithCategory_html(CoupiesSession session, 
			Integer radius, int categoryId) throws CoupiesServiceException {
		return getCouponsWithCategory_html(session, null, radius, categoryId);
	}

	
	public List<Offer> getCouponsWithCategory(CoupiesSession session, 
			int categoryId) throws CoupiesServiceException {
		return getCouponsWithCategory(session, null, null, categoryId);
	}

	@Deprecated
	public List<Offer> getCouponsWithCategory(CoupiesSession session, 
			Integer radius, int categoryId) throws CoupiesServiceException {
		return getCouponsWithCategory(session, null, radius, categoryId);
	}

	public String getCouponsWithHighlights_html(CoupiesSession session)
			throws CoupiesServiceException {
		return getCouponsWithHighlights_html(session, null);
	}

	public List<Offer> getCouponsWithHighlights(CoupiesSession session)
			throws CoupiesServiceException {
		return getCouponsWithHighlights(session, null, null);
	}
	
	public List<Offer> getCouponsWithHighlights(CoupiesSession session, Integer limit)
		throws CoupiesServiceException {
		return getCouponsWithHighlights(session, null, limit);
	}
	
	public List<Offer> getCouponsWithHighlights(CoupiesSession session,
			Coordinate coordinate) throws CoupiesServiceException {
		return getCouponsWithHighlights(session, coordinate, null);
	}
	
	/**
	 * @author larseimermacher
	 */
	@SuppressWarnings("unchecked")
	public List<Offer> getCouponsWithNotificationsIncludeRead(CoupiesSession session, Coordinate coordinate, int include_read)
			throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		String url = getAPIUrl("coupons/pushed", handler);
		HttpClient httpClient = createHttpClient(session);
		addCoordinate(coordinate, httpClient);
		httpClient.setParameter("include_read", include_read); //hinzugefügt um immer alle pushnachrichten angezeigt zu bekomen
		Object result = consumeService(httpClient.get(url), handler);
		return (List<Offer>) result;
	}
	
	/**
	 * @author larseimermacher
	 */
	@SuppressWarnings("unchecked")
	public List<Offer> getCouponsWithLocationOrderByCategory(CoupiesSession session, 
			Coordinate coordinate, Integer inLimit, Integer radius
			) throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		Object result = getCouponsWithLocationOrderByCategory(handler, session, coordinate, 
				inLimit, radius);
		// MainCategory 0 hinzufügen zur Anzeige von Coupons ohne Category
		for (int i = 0; i < ((List<Offer>)result).size(); i++) {
			if(((List<Offer>)result).get(i).getMainCategory()==null){
				Category leereMainCategory = new Category();
				leereMainCategory.setId(0);
				((List<Offer>)result).get(i).setMainCategory(leereMainCategory);
			}
		}
		return (List<Offer>) result;
	}
	


	@SuppressWarnings("unchecked")
	public List<Offer> getCouponsWithNotifications(CoupiesSession session, Coordinate coordinate)
			throws CoupiesServiceException {
		CouponListHandler handler = new CouponListHandler();
		String url = getAPIUrl("coupons/pushed", handler);
		HttpClient httpClient = createHttpClient(session);
		addCoordinate(coordinate, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return (List<Offer>) result;
	}

	@SuppressWarnings("unchecked")
	public List<Offer> getCashbackCoupons(CoupiesSession coupiesSession,
			Coordinate currentLocation, int limit) throws CoupiesServiceException{
		CouponListHandler handler = new CouponListHandler();
		String url = getAPIUrl("coupons/cashback", handler);
		HttpClient httpClient = createHttpClient(coupiesSession);
		addCoordinate(currentLocation, httpClient);
		addLimit(limit, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return (List<Offer>) result;
	}

	@SuppressWarnings("unchecked")
	public List<Offer> getOnlineCoupons(CoupiesSession coupiesSession,
			Coordinate currentLocation, int limit) throws CoupiesServiceException{
		CouponListHandler handler = new CouponListHandler();
		String url = getAPIUrl("coupons/online", handler);
		HttpClient httpClient = createHttpClient(coupiesSession);
		addCoordinate(currentLocation, httpClient);
		addLimit(limit, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return (List<Offer>) result;
	}
}
