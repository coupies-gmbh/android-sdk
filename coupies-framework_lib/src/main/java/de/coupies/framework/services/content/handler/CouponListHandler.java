/**
 * 
 */
package de.coupies.framework.services.content.handler;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Affiliate;
import de.coupies.framework.beans.Category;
import de.coupies.framework.beans.Coupon;
import de.coupies.framework.beans.Customer;
import de.coupies.framework.beans.DealCoupon;
import de.coupies.framework.beans.DealCoupon.Price.Currency;
import de.coupies.framework.beans.DealCoupon.Price;
import de.coupies.framework.beans.Offer;
import de.coupies.framework.beans.Promotion;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.utils.DOMUtils;

/**
 * @author thomas.volk@denkwerk.com
 * @since 26.08.2010
 *
 */
public class CouponListHandler implements DocumentHandler {
	
	public Object handleDocument(Document doc) throws CoupiesServiceException {
		
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		List<Offer> offer = new ArrayList<Offer>();
		NodeList couponNodes = doc.getElementsByTagName("coupons");
		if (couponNodes != null && couponNodes.getLength() > 0) {
			couponNodes = couponNodes.item(0).getChildNodes();
			for (int i = 0; i < couponNodes.getLength(); i++) {		
				Node itemNode = couponNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					offer.add(parseOffer(itemNode));
				}
			}
		}
		return offer;
	}
	
	public static Offer parseOffer(Node itemNode) {
		Offer offer = null;
		if (itemNode.getNodeName().equals("coupon")) {
			offer = parseCoupon(itemNode, new Coupon());
		}else if (itemNode.getNodeName().equals("deal")) {
			offer = parseDeal(itemNode, new DealCoupon());
		}else if (itemNode.getNodeName().equals("offer")){
			offer = parseOffer(itemNode, new Offer());
		}else if (itemNode.getNodeName().equals("promotion")){
			offer = parsePromotion(itemNode, new Promotion());
		}
		return offer;
	}
	
	public static Offer parseOffer(Node itemNode, Offer offer) {
		
		Element itemElement = (Element) itemNode;
		offer.setDetails(DOMUtils.getContentOfFirstNode(itemElement, "description"));
		
		// Get Nodes that are used
		NodeList titleNodes = (itemElement).getElementsByTagName("title");
		NodeList imageNodes = (itemElement)	.getElementsByTagName("image");
		NodeList featuredImageNodes = (itemElement).getElementsByTagName("featured_image");
		NodeList infoImageNodes = (itemElement).getElementsByTagName("images_info");
		NodeList actionNode = (itemElement).getElementsByTagName("action");
		NodeList frontendUrlNode = (itemElement).getElementsByTagName("frontend_url");
		NodeList targetUrlNode = (itemElement).getElementsByTagName("target_url");
		NodeList faceValueNode = (itemElement).getElementsByTagName("face_value");
		NodeList youtubeVideoIdNode = (itemElement).getElementsByTagName("youtube_video_id");
		NodeList samsungWalletNode = (itemElement).getElementsByTagName("samsungwallet_ticketid");
		NodeList locationButtonTextNode = (itemElement).getElementsByTagName("location_button_text");
		NodeList validityButtonTextNode = (itemElement).getElementsByTagName("onlineshop_button_text");
		
		//Get Distance to next location
		NumberFormat n = NumberFormat.getInstance();
		n.setMaximumFractionDigits(1);
		if (!String.valueOf((itemElement).getAttribute("distance")).equals("")) {
			offer.setDistance(Double.valueOf((itemElement).getAttribute("distance")) / 1000);
		}

		if ((itemElement).hasAttribute("expires")) {
			String expires = String.valueOf((itemElement)	.getAttribute("expires"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				java.util.Date dt = formatter.parse(expires);
				offer.setExpireDate(dt);
			} catch (ParseException e) {	}
		}
		
		if ((itemElement).hasAttribute("starts")) {
			String expires = String.valueOf((itemElement).getAttribute("starts"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				java.util.Date dt = formatter.parse(expires);
				offer.setStartDate(dt);
			} catch (ParseException e) {	}
		}
		
		if ((itemElement).hasAttribute("created_at")) {
			String createdAt = String.valueOf((itemElement).getAttribute("created_at"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				java.util.Date dt = formatter.parse(createdAt);
				offer.setCreationDate(dt);
			} catch (ParseException e) {	}
		}
		
		if ((itemElement).hasAttribute("updated_at")) {
			String updatedAt = String.valueOf((itemElement).getAttribute("updated_at"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				java.util.Date dt = formatter.parse(updatedAt);
				offer.setUpdateDate(dt);
			} catch (ParseException e) {	}
		}
				
		
		//Lars prüfen ob eine Pass_id hinterlget ist (nur wenn auch als passbookartikel im Backend)
		if ((itemElement).hasAttribute("pass_id")) {
			String passId = String.valueOf((itemElement).getAttribute("pass_id"));
			if(passId != null && passId.length() > 0) offer.setPassId(Integer.valueOf(passId));
		}

				
		if ((itemElement).hasAttribute("unread")) {
			String unread = String.valueOf((itemElement).getAttribute("unread"));
			if(unread.length() > 0 && !unread.equals("0")) {
				offer.setUnread(true);
			} else {
				offer.setUnread(false);
			}
		} else {
			offer.setUnread(false);
		}
		
		if ((itemElement).hasAttribute("url")) {
			offer.setUrl(itemElement.getAttribute("url"));
		}

		/**
		 * @author larseimermacher
		 * Prüfen welche Aktion zum Einlösen für dieses Offer hinterlegt ist 
		 */
		if (actionNode != null && actionNode.item(0).hasChildNodes() == true){
			try{
				offer.setAction(Integer.valueOf(actionNode.item(0).getFirstChild().getNodeValue()));
			}catch (Exception e) {
				offer.setAction(0);
				Log.w("Contenthandler", "Kein Wert für action --> Es wird 0 eingetragen!");
			}
			
		}
		
		/**
		 * @author larseimermacher
		 * Die frontendUrl besorgen 
		 */
		if (frontendUrlNode != null && frontendUrlNode.item(0).hasChildNodes() == true){
			try{
				offer.setFrontendUrl(frontendUrlNode.item(0).getFirstChild().getNodeValue());
			}catch (Exception e) {
				Log.w("Contenthandler", "Kein Wert für targetUrl");
			}
		}
		
		/**
		 * @author larseimermacher
		 * Die targetUrl besorgen 
		 */
		if (targetUrlNode != null && targetUrlNode.item(0).hasChildNodes() == true){
			try{
				offer.setTargetUrl(targetUrlNode.item(0).getFirstChild().getNodeValue());
			}catch (Exception e) {
				Log.w("Contenthandler", "Kein Wert für targetUrl");
			}
		}
		
		/**
		 * @author larseimermacher
		 * Die locationButtonTextNode besorgen 
		 */
		if (locationButtonTextNode != null && locationButtonTextNode.item(0) != null && locationButtonTextNode.item(0).hasChildNodes() == true){
			try{
				offer.setLocationButtonText(locationButtonTextNode.item(0).getFirstChild().getNodeValue());
			}catch (Exception e) {
				Log.w("Contenthandler", "Kein Wert für locationButtonTextNode");
			}
		}
		
		/**
		 * @author larseimermacher
		 * Die validityButtonTextNode besorgen 
		 */
		if (validityButtonTextNode != null && validityButtonTextNode.item(0) != null && validityButtonTextNode.item(0).hasChildNodes() == true){
			try{
				offer.setValidityButtonText(validityButtonTextNode.item(0).getFirstChild().getNodeValue());
			}catch (Exception e) {
				Log.w("Contenthandler", "Kein Wert für validityButtonTextNode");
			}
		}
		
		/**
		 * @author larseimermacher
		 * Das faceValue aus der XML auslesen
		 */
		if(faceValueNode != null && faceValueNode.item(0) != null &&faceValueNode.item(0).hasChildNodes() == true){
			try{
				offer.setFaceValue(Double.valueOf(faceValueNode.item(0).getFirstChild().getNodeValue()));
			}catch (Exception e) {
				Log.w("Contenthandler", "Kein Wert für faceValue");
			}
		}
		
		/**
		 * @author karimwahishi
		 * Read Youtube Video ID from XML
		 */
		if(youtubeVideoIdNode != null && youtubeVideoIdNode.item(0) != null && youtubeVideoIdNode.item(0).hasChildNodes() == true){
			try{
				offer.setVideo(youtubeVideoIdNode.item(0).getFirstChild().getNodeValue());
			}catch (Exception e) {
				Log.w("Contenthandler", "Kein Wert für YoutubeVideoId");
			}
		}
		
		/**
		 * @author karimwahishi
		 * Read samsungWallet ID from XML
		 */
		if(samsungWalletNode != null && samsungWalletNode.item(0) != null && samsungWalletNode.item(0).hasChildNodes() == true){
			try{
				offer.setSwTicketId(samsungWalletNode.item(0).getFirstChild().getNodeValue());
			}catch (Exception e) {
				Log.w("Contenthandler", "Kein Wert für SamsungWalletId");
			}
		}
		
		// Titel des Coupons auslesen
		if (titleNodes.item(0).hasChildNodes() == true) offer.setTitle(titleNodes.item(0).getFirstChild().getNodeValue());
		// Bilder aulesen
		if (imageNodes.item(0).hasChildNodes() == true) offer.setImageUrl(imageNodes.item(0).getFirstChild().getNodeValue());		
		
		if (featuredImageNodes != null && featuredImageNodes.item(0) != null && featuredImageNodes.item(0).hasChildNodes() == true) {
			offer.setFeaturedImageUrl(featuredImageNodes.item(0).getFirstChild().getNodeValue());
		}
		
		if (infoImageNodes != null && infoImageNodes.item(0) != null) {
			NodeList infoImages = infoImageNodes.item(0).getChildNodes();
			if (infoImages != null) {
				List<String> list = new ArrayList<String>(5);
				for (int i = 0; i < infoImages.getLength(); i++) {		
					Node item = infoImages.item(i);
					if (item.getNodeType() == Node.ELEMENT_NODE) {
						list.add(item.getFirstChild().getNodeValue());
					}
				}
				offer.setInfoImageUrlList(list);
			}
		}
	
		offer.setId(Integer.valueOf((itemElement).getAttribute("id")));		
		offer.setPriority(Integer.valueOf((itemElement).getAttribute("priority")));
		
		//Get Customer
		Element customerNode = DOMUtils.getSubElement(itemElement, "customer", 0);
		if(customerNode != null) {
			Customer customer = new Customer();
			customer.setId(Integer.valueOf(customerNode.getAttribute("id")));
			customer.setIconUrl(customerNode.getAttribute("icon"));
			customer.setDescription(customerNode.getAttribute("description"));
			customer.setTitle(DOMUtils.getNodeContent(customerNode));
			offer.setCustomer(customer);					
		}
		
		//Get Affiliate
		Element affiliateNode = DOMUtils.getSubElement(itemElement, "affiliate", 0);
		if(affiliateNode != null) {
			Affiliate affiliate = new Affiliate();
			affiliate.setId(Integer.valueOf(affiliateNode.getAttribute("id")));
			affiliate.setIconUrl(affiliateNode.getAttribute("icon"));
			affiliate.setDescription(affiliateNode.getAttribute("description"));
			affiliate.setWebsite(affiliateNode.getAttribute("website"));
			affiliate.setTitle(DOMUtils.getNodeContent(affiliateNode));
			offer.setAffiliate(affiliate);					
		}
		
		//Get Main Interest
		Element interestNode = DOMUtils.getSubElement(itemElement, "interest", 0);
		if(interestNode != null && !String.valueOf(interestNode.getAttribute("id")).equals("")) {
			Category category = new Category();
			category.setId(Integer.valueOf(interestNode.getAttribute("id")));
			category.setTitle(DOMUtils.getNodeContent(interestNode));
			offer.setMainCategory(category);					
		}
		
		//get bookmark status
		String bookmarked = DOMUtils.getAttribute(itemNode, "isbookmarked");
		if("0".equals(bookmarked)) {
			offer.setBookmarked(false);
		} else {
			offer.setBookmarked(true);
		}
		
		//get like status
		String liked = DOMUtils.getAttribute(itemNode, "isliked");
		if (liked != null && liked.length() > 0) {
			if("0".equals(liked)) {
				offer.setLiked(false);
			} else {
				offer.setLiked(true);
			}
		}
		
		//get like count
		String likes = DOMUtils.getAttribute(itemNode, "likes");
		if (likes != null && likes.length() > 0) {
			offer.setLikes(Integer.valueOf(likes));
		}		
//		Log.d("COUPIES smamsung wallet", "This offer is ready and parsed "+offer.toString()+" with ticket ID "+offer.getSwTicketId());
		return offer;
	}
	
	public static DealCoupon parseDeal(Node itemNode, DealCoupon deal) {
		deal = (DealCoupon)parseCoupon(itemNode, deal);
		
		deal.setFineprint(DOMUtils.getContentOfFirstNode(itemNode, "fineprint"));
		deal.setHighlights(DOMUtils.getContentOfFirstNode(itemNode, "highlights"));
		if(DOMUtils.getFloat(DOMUtils.getContentOfFirstNode(itemNode, "discount"))!=null){
			deal.setDiscount(DOMUtils.getFloat(DOMUtils.getContentOfFirstNode(itemNode, "discount")));
		}else{
			deal.setDiscount(DOMUtils.getFloat("0"));
		}
		deal.setPrice(parsePrice(DOMUtils.getSubNode(itemNode, "price", 0)));
		deal.setOriginalPrice(parsePrice(DOMUtils.getSubNode(itemNode, "original_price", 0)));
		
		return deal;
	}
	
	public static Promotion parsePromotion(Node itemNode, Promotion promotion) {
		promotion = (Promotion) parseCoupon(itemNode, promotion);
		
		if(DOMUtils.getContentOfFirstNode(itemNode, "action_button_text")!=null){
			promotion.setActionButtonText(DOMUtils.getContentOfFirstNode(itemNode, "action_button_text"));
		}else{
			promotion.setActionButtonText(null);
		}
		
		return promotion;
	}
	
	public static Coupon parseCoupon(Node itemNode, Coupon coupon){
		coupon = (Coupon)parseOffer(itemNode, coupon);

		Element itemElement = (Element)itemNode;
		
		if((itemElement).hasAttribute("remaining")){
			String remaining = String.valueOf((itemElement).getAttribute("remaining"));
			if(remaining !=null && remaining.length()>0) coupon.setRemaining(Integer.valueOf(remaining));
		}
		
		if ((itemElement).hasAttribute("remaining_total")) {
			String remaining_total = String.valueOf((itemElement).getAttribute("remaining_total"));
			if(remaining_total != null && remaining_total.length() > 0) coupon.setRemainingTotal(Integer.valueOf(remaining_total));
		}
		
		if ((itemElement).hasAttribute("remaining_interval")) {
			String remaining_interval = String.valueOf((itemElement).getAttribute("remaining_interval"));
			if(remaining_interval != null && remaining_interval.length() > 0) coupon.setRemainingInterval(Integer.valueOf(remaining_interval));
		}
		
		if ((itemElement).hasAttribute("closest_location_accepts_sticker")) {
			String acceptsSticker = String.valueOf((itemElement).getAttribute("closest_location_accepts_sticker"));
			if(acceptsSticker.length() > 0) {
				coupon.setClosestLocationAcceptsSticker(true);
			} else {
				coupon.setClosestLocationAcceptsSticker(false);
			}
		} else {
			coupon.setClosestLocationAcceptsSticker(false);
		}
		
		return coupon;
	}
	
	public static Price parsePrice(Node itemNode) {
		Currency currency = parseCurrency(DOMUtils.getAttribute(itemNode, "currency"));
		Float value = DOMUtils.getFloat(DOMUtils.getNodeContent(itemNode));
		if (currency != null && value != null) {
			return new Price(value, currency);
		}
		else {
			return null;
		}
	}
	
	public static Currency parseCurrency(String currency) {
		if (currency.equalsIgnoreCase("euro") || currency.equalsIgnoreCase("eur")) {
			return Currency.EURO;
		}
		else if (currency.equalsIgnoreCase("dolar")  || currency.equalsIgnoreCase("usd")) {
			return Currency.DOLLAR;
		}
		else if (currency.equalsIgnoreCase("chf") ) {
			return Currency.SWISS_FRANK;
		}
		else {
			return null;
		}
	}
}