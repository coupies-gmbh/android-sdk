package de.coupies.framework.services.content.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Coupon;
import de.coupies.framework.beans.Location;
import de.coupies.framework.beans.Promotion;
import de.coupies.framework.beans.Redemption;
import de.coupies.framework.services.LocationServiceImpl;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.ValidationParser;

/**
 * 
 * @author larseimermacher
 *
 */
public class RedemptionHandler implements DocumentHandler{
	ValidationParser validationParser = new ValidationParser();
	public Redemption handleDocument(Document doc) throws CoupiesServiceException{
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		Redemption redemption = new Redemption();
		NodeList redemptionNodes = doc.getElementsByTagName("redemption");
		if(redemptionNodes == null || redemptionNodes.item(0)==null){
			ValidationParser validationParser = new ValidationParser();
			validationParser.parseAndThrow(doc);
		}
		redemption = parseRedemption(redemptionNodes.item(0));
		
		return redemption;
	}
	public static Redemption parseRedemption(Node doc) throws CoupiesServiceException{
		ValidationParser validationParser = new ValidationParser();
		Redemption redemption = new Redemption();
		
		// Um NodeList aus einer Node zu bekommen
		Element itemElement = (Element) doc;
		
		if(itemElement==null){
			validationParser.parseAndThrow((Document) itemElement);
		}
		
		// Die Elemente auslesen
		NodeList couponNodes = itemElement.getElementsByTagName("coupon");
		NodeList promotionNodes = itemElement.getElementsByTagName("promotion");
		NodeList locationNodes = itemElement.getElementsByTagName("location");
		NodeList rejectionreasonNodes = itemElement.getElementsByTagName("rejectionreason");
		NodeList cashback_valueNodes = itemElement.getElementsByTagName("cashback_value");
		NodeList couponcodeNodes = itemElement.getElementsByTagName("couponcode");
		NodeList quantityNodes = itemElement.getElementsByTagName("quantity");
		NodeList redemptionNodes = itemElement.getElementsByTagName("receipt");

		// Prüfen ob auch alle benötigten Elemente enthalten und mit einem Wert befüllt sind
		if((couponNodes==null && promotionNodes==null) ||
			cashback_valueNodes==null||
			couponcodeNodes==null){
				validationParser.parseAndThrow((Document) itemElement);
		}

		// Auslesen der Attribute des Redemption-Elements
		String redemtionIdElem = itemElement.getAttribute("id");
		try{
			int redemtionId = Integer.parseInt(redemtionIdElem);
			redemption.setId(redemtionId);
		}catch (NumberFormatException e) {
			validationParser.parseAndThrow((Document) itemElement);
		}
		
		String time = String.valueOf(itemElement.getAttribute("time"));
				
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date dt = formatter.parse(time);
			redemption.setTime(dt);
		} catch (ParseException e) {
			validationParser.parseAndThrow((Document) itemElement);
		}
		
		try {
			Integer allocationid = Integer.parseInt(String.valueOf(itemElement
					.getAttributes().getNamedItem("allocationid")
					.getNodeValue()));
			redemption.setAllocationid(allocationid);
			
			//set the reuploadable boolean
			String reuploadableString = String.valueOf(itemElement
					.getAttributes().getNamedItem("reuploadable")
					.getNodeValue());
			if(!reuploadableString.equals("1"))
				redemption.setReuploadable(false);
			else{
				redemption.setReuploadable(true);
			}
			
			Integer userId = Integer.parseInt(String.valueOf(itemElement
					.getAttributes().getNamedItem("userid")
					.getNodeValue()));
			redemption.setUserId(userId);
		
			Integer valid = Integer.parseInt(String.valueOf(itemElement
					.getAttributes().getNamedItem("valid")
					.getNodeValue()));
			if(valid!=null)
			redemption.setValid(valid);
		} catch (Exception e) {
			// auskommentiert weil noch nicht übergeben wird
			// e.printStackTrace
		}
		
		try {
			Integer status = Integer.parseInt(String.valueOf(itemElement
					.getAttributes().getNamedItem("status")
					.getNodeValue()));
			if(status!=null)
			redemption.setStatus(status);
		} catch (Exception e) {
			validationParser.parseAndThrow((Document) itemElement);
		}
		
		// Promotion und Coupon-Objekt auslesen
		Node couponNode = couponNodes.item(0);
		Node promotionNode = promotionNodes.item(0);
		if (promotionNode == null && couponNode == null) {
			validationParser.parseAndThrow((Document) itemElement);
		}else if(couponNode != null){
			// Nutze die statische Methode des CouponListhandlers um das Coupon-Objekt zu erhalten
			try{
				Coupon coupon = (Coupon) CouponListHandler.parseOffer(couponNode);
				if(coupon!=null)
					redemption.setCoupon(coupon);
			} catch (Exception e) {
				validationParser.parseAndThrow((Document) itemElement);
			}
		}else{
			// Nutze die statische Methode des CouponListhandlers um das Promotion-Objekt zu erhalten
			try{
				Promotion promotion = (Promotion) CouponListHandler.parseOffer(promotionNode);
				if(promotion!=null)
				redemption.setPromotion(promotion);
			} catch (Exception e) {
				validationParser.parseAndThrow((Document) itemElement);
			}
		}
		
		// Cashback-Element auslesen
		Node cashbackValueNode = cashback_valueNodes.item(0);
		if (cashbackValueNode==null){
			validationParser.parseAndThrow((Document) itemElement);
		}
		// Auslesen des Wertes vom Element cashback_value
		if (cashbackValueNode != null && cashbackValueNode.hasChildNodes() == true){
			Double cashback_value;
			try{
				cashback_value = (Double.valueOf(cashbackValueNode.getFirstChild().getNodeValue()));
			}catch (Exception e) {
				cashback_value = 0.0;
				Log.w("RedemptionHandler", "Kein Wert für cashback_value --> Es wird 0.0 eingetragen!");
			}
			redemption.setCashback_vaule(cashback_value);
		}
		
		// Couponcode-Element auslesen
		Node couponcodeNode = couponcodeNodes.item(0);
		if(couponcodeNode==null){
			validationParser.parseAndThrow((Document) itemElement);
		}
		// Auslesen der Attribute und dessen Werte
		String type = String.valueOf(couponcodeNode.getAttributes().getNamedItem("type").getNodeValue());
		String text = null;
		if(type.equals("barcode")||type.equals("ean13")||type.equals("ean128")){
			if(couponcodeNode.hasChildNodes() == true) {
				text = couponcodeNode.getFirstChild().getNodeValue();
			}
		}else{
			 text = String.valueOf(couponcodeNode.getAttributes().getNamedItem("text").getNodeValue());
		}
		if(type!=null && text!= null){
			redemption.setType(type);
			redemption.setText(text);
		}
		
		// Rejectionreason-Element auslesen wenn vorhanden
		if(rejectionreasonNodes != null && rejectionreasonNodes.getLength()>0){
			Node rejectionReasonNode = rejectionreasonNodes.item(0);
			if (rejectionReasonNode==null){
				validationParser.parseAndThrow((Document) itemElement);
			}
			// Auslesen des Wertes vom Element rejectionreason
			String rejectionreason="";
			if(rejectionReasonNode !=null && rejectionReasonNode.hasChildNodes()){
				rejectionreason = rejectionReasonNode.getFirstChild().getNodeValue();
			}
			Integer rejectionReasonId = Integer.parseInt(String.valueOf(rejectionReasonNode
					.getAttributes().getNamedItem("id")
					.getNodeValue()));
			redemption.setRejectionreason(rejectionreason);
			redemption.setRejectionreason_id(rejectionReasonId);
			
			try {
				// Zusätzlichen "Comment" zu einer Ablehung auslesen
				String comment = String.valueOf(rejectionReasonNode
						.getAttributes().getNamedItem("comment")
						.getNodeValue());
				if(comment!=null && comment.length()>0)
					redemption.setComment(comment);
			}catch (NullPointerException ne) {
				// Kann vorkommen uns soll daher keinen Fehler ausgeben...
			}catch (Exception e) {
				validationParser.parseAndThrow((Document) itemElement);
			}
		}
		
		// Location-Element auslesen wenn vorhanden
		if(locationNodes != null && locationNodes.getLength()>0){
			Node locationNode = locationNodes.item(0);
			if(locationNode==null){
				validationParser.parseAndThrow((Document) itemElement);
			}
			// Auslesen der Werte vom Element location
			Location location = LocationServiceImpl.parseLocation(locationNode);
			redemption.setLocation(location);
		}
		
		if(quantityNodes !=null && quantityNodes.getLength()>0){
			Node quantityNode = quantityNodes.item(0);
			
			if(quantityNode==null){
				validationParser.parseAndThrow((Document) itemElement);
			}
			// Auslesen der Werte vom Element quantity
			Integer quantity  = (Integer.valueOf(quantityNode.getFirstChild().getNodeValue()));
			redemption.setQuantity(quantity);
		}
		
		if(redemptionNodes !=null && redemptionNodes.getLength()>0){
			Node redemptionNode = redemptionNodes.item(0);
			if(redemptionNode==null){
				validationParser.parseAndThrow((Document) itemElement);
			}
			try{
				redemption.setReceipt(ReceiptHandler.parseReceipt(((Element)doc).getElementsByTagName("receipt").item(0)));
			}catch (NullPointerException e) {
				Log.e("RedemptionHandler", "NullPointerException --> kein Bild hinterlegt");
			}catch (Exception e) {
				validationParser.parseAndThrow((Document) itemElement);
			}
		}
				
		return redemption;
	}
}
