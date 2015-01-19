package de.coupies.framework.services.content.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Reservation;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.ValidationParser;

/**
 * This Document-Handler is for Reservetions. Used for "reservations/new.xml"
 * The handleDocument-Method return an Reservation-Object 
 * 
 * @author larseimermacher
 * @since 26.02.2013
 */
public class ReservationHandler implements DocumentHandler{
	ValidationParser validationParser = new ValidationParser();
	public Reservation handleDocument(Document doc) throws CoupiesServiceException{
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		Reservation reservation = new Reservation();
		NodeList reservationNodes = doc.getElementsByTagName("reservation");
		if(reservationNodes == null){
			ValidationParser validationParser = new ValidationParser();
			validationParser.parseAndThrow(doc);
		}
		reservation = parseReservation(reservationNodes.item(0));
		
		return reservation;
	}
	public static Reservation parseReservation(Node doc) throws CoupiesServiceException{
		ValidationParser validationParser = new ValidationParser();
		Reservation reservation = new Reservation();
		
		// Um NodeList aus einer Node zu bekommen
		Element itemElement = (Element) doc;
		
		// Die Elemente auslesen
		NodeList couponNodes = itemElement.getElementsByTagName("coupon");
		NodeList couponcodeNodes = itemElement.getElementsByTagName("couponcode");

	// Prüfen ob auch alle benötigten Elemente enthalten und mit einem Wert befüllt sind
		if(	couponNodes==null||	couponcodeNodes==null){
			validationParser.parseAndThrow((Document) itemElement);
		}

		// Auslesen der Attribute des Reservation-Elements
		String time = String.valueOf(itemElement.getAttribute("time"));
				
		// Auslesen und speichern des "time"-Attribute
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date dt = formatter.parse(time);
			reservation.setTime(dt);
		} catch (ParseException e) {
			validationParser.parseAndThrow((Document) itemElement);
		}
		// Auslesen und speichern des "allocationid"-Attribut
		Integer allocationid = Integer.parseInt(String.valueOf(itemElement
				.getAttributes().getNamedItem("allocationid")
				.getNodeValue()));
		reservation.setAllocationId(allocationid);
			
		
	// Coupon-Element auslesen
		Node couponNode = couponNodes.item(0);
		if (couponNode == null) {
			validationParser.parseAndThrow((Document) itemElement);
		}
		// Auslesen und speichern des "id"-Attribute
		Integer couponId = Integer.parseInt(String.valueOf(couponNode.getAttributes().getNamedItem("id").getNodeValue()));
		reservation.setCouponId(couponId);
		// Auslesen und speichern des "href"-Attribute
		String couponWebLink = String.valueOf(couponNode.getAttributes().getNamedItem("href").getNodeValue());
		reservation.setCouponWebLink(couponWebLink);
		
		
	// Couponcode-Element auslesen
		Node couponcodeNode = couponcodeNodes.item(0);
		if(couponcodeNode==null){
			validationParser.parseAndThrow((Document) itemElement);
		}
		// Auslesen und speichern des "type" und "text"-Attribute
		String couponType = String.valueOf(couponcodeNode.getAttributes().getNamedItem("type").getNodeValue());
		String text = null;
		if(couponType.equals("barcode")||couponType.equals("ean13")||couponType.equals("ean128")){
			if(couponcodeNode.hasChildNodes() == true) {
				text = couponcodeNode.getFirstChild().getNodeValue();
			}
		}else {
			text = String.valueOf(couponcodeNode.getAttributes().getNamedItem("text").getNodeValue());
		}
		if(couponType!=null && text!= null){
			reservation.setCouponType(couponType);
			reservation.setTokenParameter(text);
		}
				
		return reservation;
	}
}
