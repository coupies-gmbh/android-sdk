package de.coupies.framework.services;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Customer;
import de.coupies.framework.beans.Location;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.utils.DOMUtils;
import de.coupies.framework.utils.StringUtils;
import de.coupies.framework.utils.URLUtils;

public class LocationServiceImpl extends AbstractCoupiesService 
		implements LocationService {
	private class LocationHandler implements DocumentHandler {
		public Object handleDocument(Document doc) {
			return parseLocation(doc.getElementsByTagName("location").item(0));
		}
	}

	private class LocationListHandler implements DocumentHandler {
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
			
			List<Location> locations = new ArrayList<Location>();
			NodeList locationNodes = doc.getElementsByTagName("location");
			for (int i = 0; i < locationNodes.getLength(); i++) {
				Location location = parseLocation(locationNodes.item(i));
				locations.add(location);
			}
			return locations;
			
		}
	}
	
	public LocationServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}
	public static Location parseLocation(Node locationNode) {
		Location location = new Location();
		if (locationNode.getNodeType() == Node.ELEMENT_NODE) {
			// Knoten in ein Element umwandeln
			Element itemElement = (Element) locationNode;

			// Die Knoten aulesen die weiterhin ben�tigt
			// werden
			Node customerNode = (itemElement)
					.getElementsByTagName("customer").item(0);
			Node addressNode = (itemElement)
					.getElementsByTagName("address").item(0);
			Node latitudeNode = (itemElement)
					.getElementsByTagName("latitude").item(0);
			Node longitudeNode = (itemElement)
					.getElementsByTagName("longitude").item(0);
			String distanceValue = itemElement
					.getAttribute("distance");
			if (distanceValue != null && distanceValue.length() > 0) {
				location.setDistance(Double
						.valueOf(distanceValue));
			} 
			
			try{
				location.setProductAvailable(Integer.valueOf(itemElement.getAttribute("availability")));
			}catch (NumberFormatException e) {
				location.setProductAvailable(2);
			}
			
			location.setTitle(DOMUtils.getContentOfFirstNode(locationNode, "title"));			
			location.setId(Integer.valueOf(itemElement.getAttribute("id")));
			location.setAcceptsSticker(String.valueOf(itemElement.getAttribute("accepts_sticker")));

			// Breiten- und Längengrade in Integer umwandeln
			if (latitudeNode.hasChildNodes() == true) {
				location.setLatitude(Double.valueOf(latitudeNode
						.getFirstChild().getNodeValue()));
				location.setLongitude(Double
						.valueOf(longitudeNode.getFirstChild()
								.getNodeValue()));
			}

			// Bild aulesen
			if (customerNode.hasAttributes() == true) {
				Customer customer = new Customer();
				customer.setId(Integer.valueOf(DOMUtils.getAttribute(customerNode, "id")));
				customer.setIconUrl(DOMUtils.getAttribute(customerNode, "icon"));
				location.setCustomer(customer);
			}

			// Adresse auslesen
			if (addressNode.hasChildNodes() == true) {
				location.setAddress(addressNode.getFirstChild()
						.getNodeValue());
			}
			try{
				location.setCouponCount(Integer.valueOf(DOMUtils.getAttribute(locationNode, "coupons")));
			}catch (Exception e) {
				// TODO: other reaction 
//				e.printStackTrace();
			}
			location.setUrl(DOMUtils.getAttribute(locationNode, "url"));
			location.setPhoneNumber(DOMUtils.getContentOfFirstNode(locationNode, "phone"));
			location.setWebsite(DOMUtils.getContentOfFirstNode(locationNode, "website"));
		}
		return location;
	}
	private Object getLocation(Handler handler, CoupiesSession session,
			int id) throws CoupiesServiceException {
		String url = getAPIUrl(String.format("locations/%s", id), handler);
		Object result = consumeService(createHttpClient(session).get(url),
				handler);
		return result;
	}
	private Object getLocationsWithCoupon(Handler handler,
			CoupiesSession session, Coordinate coordinate, 
			int couponId) throws CoupiesServiceException {
		String url = getAPIUrl(String.format("coupons/%s/locations", couponId), handler);
		HttpClient httpClient = createHttpClient(session);
		addCoordinate(coordinate, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return result;
	}

	private Object getLocationWithCategories(Handler handler,
			CoupiesSession session, Coordinate coordinate, 
			Integer[] inCategoryIds, Integer limit, int inRadius)
			throws CoupiesServiceException {
		String url=null;
		if(coordinate!=null){
			url = getAPIUrl(String.format("locations/%s/%s/%d",
				URLUtils.convertFromFloat(coordinate.getLatitude()),
				URLUtils.convertFromFloat(coordinate.getLongitude()),
				inRadius				
				), handler);
		}else{
			url = getAPIUrl(String.format("locations/%s/%s/%d",
					""+50,
					""+6,
					inRadius				
					), handler);
		}
		HttpClient client = createHttpClient(session);
		if(inCategoryIds != null) {
			client.setParameter("interests", StringUtils.join(inCategoryIds, ","));
		}
		addLimit(limit, client);
		Object result = consumeService(client.get(url), handler);
		return result;
	}
	
	private Object getLocations(Handler handler,
			CoupiesSession session, Coordinate coordinate, 
			Integer limit, int inRadius) throws CoupiesServiceException {
		return getLocationWithCategories(handler, session, coordinate,
				null, limit, inRadius);
	}



	public Location getLocation(CoupiesSession session, int id) throws CoupiesServiceException {
		LocationHandler handler = new LocationHandler();
		Object result = getLocation(handler, session, id);
		return (Location) result;
	}

	@SuppressWarnings("unchecked")
	public List<Location> getLocationsWithCoupon(CoupiesSession session, 
			Coordinate coordinate, int couponId) throws CoupiesServiceException {
		LocationListHandler handler = new LocationListHandler();
		Object result = getLocationsWithCoupon(handler, session,
				coordinate, couponId);
		return (List<Location>) result;
	}

	
	@SuppressWarnings("unchecked")
	public List<Location> getLocationsWithCategories(CoupiesSession session, 
			Coordinate coordinate, int inRadius, Integer[] inCategoryIds, Integer limit) 
			throws CoupiesServiceException {
		LocationListHandler handler = new LocationListHandler();
		Object result = getLocationWithCategories(handler, session, coordinate,
				inCategoryIds, limit, inRadius);
		return (List<Location>) result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Location> getLocationWithPosition(CoupiesSession session, 
			Coordinate coordinate, int inRadius, Integer limit) 
			throws CoupiesServiceException {
		LocationListHandler handler = new LocationListHandler();
		Object result = getLocations(handler, session, coordinate, limit, inRadius);
		return (List<Location>) result;
	}

	public String getLocation_html(CoupiesSession session, int id)
			throws CoupiesServiceException {
		return (String) getLocation(HTML_RESULT_HANDLER, session, id);
	}
	public String getLocationsWithCoupon_html(CoupiesSession session,
			Coordinate coordinate, int couponId) throws CoupiesServiceException {
		return (String) getLocationsWithCoupon(HTML_RESULT_HANDLER, 
				session, coordinate, couponId);
	}
	public String getLocationsWithCategories_html(CoupiesSession session,
			Coordinate coordinate, int inRadius, Integer[] inCategoryIds, 
			Integer limit) throws CoupiesServiceException {
		return (String) getLocationWithCategories(HTML_RESULT_HANDLER, 
				session, coordinate, inCategoryIds, limit, inRadius);
	}
	public String getLocationsWithPosition_html(CoupiesSession session,
			Coordinate coordinate, 
			int inRadius, Integer limit) throws CoupiesServiceException {
		return (String) getLocations(HTML_RESULT_HANDLER, 
				session, coordinate, limit, inRadius);
	}
	public String getLocationsWithCoupon_html(CoupiesSession session,
			int couponId) throws CoupiesServiceException {
		return getLocationsWithCoupon_html(session, null, couponId);
	}
	public List<Location> getLocationsWithCoupon(CoupiesSession session,
			int couponId) throws CoupiesServiceException {
		return getLocationsWithCoupon(session, null, couponId);
	}
	
	public void reportLocation(CoupiesSession session, int couponId, int locationId) throws CoupiesServiceException{
		String url = getAPIUrl(String.format("coupons/%s/locations/%s/unavailable", couponId, locationId), NO_RESULT_HANDLER);
		HttpClient httpClient = createHttpClient(session);
		consumeService(httpClient.post(url), NO_RESULT_HANDLER);
	}
	
	@SuppressWarnings("unchecked")
	public List<Location> getLocationSearch(CoupiesSession session, Coordinate coordinate, int couponId, String searchString) throws CoupiesServiceException {
		LocationListHandler handler = new LocationListHandler();
		String url = getAPIUrl(String.format("coupons/%s/locations", couponId), handler);
		HttpClient httpClient = createHttpClient(session);
		httpClient.setParameter("searchstring", searchString);
		addCoordinate(coordinate, httpClient);
		Object result = consumeService(httpClient.get(url), handler);
		return (List<Location>)result;
	}
		
}
