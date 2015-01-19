package de.coupies.framework.services.content.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.coupies.framework.beans.Customer;
import de.coupies.framework.beans.Location;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.utils.DOMUtils;

public class LocationHandler implements DocumentHandler {
	public Object handleDocument(Document doc) {
		return parseLocation(doc.getElementsByTagName("location").item(0));
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
			String distanceValue = itemElement.getAttribute("distance");
			if (distanceValue != null && distanceValue.length() > 0) {
				location.setDistance(Double
						.valueOf(distanceValue));
			}
			
			location.setTitle(DOMUtils.getContentOfFirstNode(locationNode, "title"));			
			location.setId(Integer.valueOf(itemElement.getAttribute("id")));
			location.setAcceptsSticker(String.valueOf(itemElement.getAttribute("accepts_sticker")));
			try{
				location.setProductAvailable(Integer.valueOf(itemElement.getAttribute("availability")));
			}catch (NumberFormatException e) {
				location.setProductAvailable(2);
			}
			// Breiten- und Längengrade in Integer umwandeln
			if (latitudeNode.hasChildNodes() == true) {
				location.setLatitude(Double.valueOf(latitudeNode
						.getFirstChild().getNodeValue()));
				location.setLongitude(Double
						.valueOf(longitudeNode.getFirstChild()
								.getNodeValue()));
			}

			// Bild auslesen
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
			location.setCouponCount(Integer.valueOf(DOMUtils.getAttribute(locationNode, "coupons")));
			location.setUrl(DOMUtils.getAttribute(locationNode, "url"));
			location.setPhoneNumber(DOMUtils.getContentOfFirstNode(locationNode, "phone"));
			location.setWebsite(DOMUtils.getContentOfFirstNode(locationNode, "website"));
		}
		return location;
	}
}
