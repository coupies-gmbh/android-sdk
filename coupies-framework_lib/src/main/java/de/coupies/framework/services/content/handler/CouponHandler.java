package de.coupies.framework.services.content.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;

public class CouponHandler implements DocumentHandler{
	ValidationParser mValidationParser;
	
	public Object handleDocument(Document doc)
			throws CoupiesServiceException {
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				mValidationParser = new ValidationParser();
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
				mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		return CouponListHandler.parseOffer(couponNode);
	}
}