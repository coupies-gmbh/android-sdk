/**
 * 
 */
package de.coupies.framework.services.content.handler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Coupon;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;

/**
 * @author thomas.volk@denkwerk.com
 * @since 26.08.2010
 *
 */
public class FeedListHandler extends CouponListHandler implements DocumentHandler {
	public Object handleDocument(Document doc) throws CoupiesServiceException {
		List<Coupon> coupons = new ArrayList<Coupon>();
		
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		NodeList featuredNodes = doc.getElementsByTagName("featured_coupons");
		for (int i = 0; i < featuredNodes.getLength(); i++) {		
			Node itemNode = featuredNodes.item(i);
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
				coupons.add((Coupon)parseOffer(itemNode));
			}
		}

		NodeList couponNodes = doc.getElementsByTagName("coupons");
		if (couponNodes != null && couponNodes.getLength() > 0) {
			couponNodes = couponNodes.item(0).getChildNodes();
			for (int i = 0; i < couponNodes.getLength(); i++) {		
				Node itemNode = couponNodes.item(i);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					coupons.add((Coupon)parseOffer(itemNode));
				}
			}
		}
		
		return coupons;
	}
}
