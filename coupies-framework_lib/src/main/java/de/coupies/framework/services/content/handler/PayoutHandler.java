package de.coupies.framework.services.content.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Payout;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.utils.DOMUtils;

public class PayoutHandler {
	public Payout handleDocument(Document doc) throws CoupiesServiceException {
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		Payout payout = new Payout();
		NodeList payoutNodes = doc.getElementsByTagName("latest_payout");
		if (payoutNodes == null || payoutNodes.item(0) == null) {
			return null;
		}
		payout = parsePayout(payoutNodes.item(0));
		return payout;
	}

	public static Payout parsePayout(Node doc) throws CoupiesServiceException {

		ValidationParser validationParser = new ValidationParser();
		Payout payout = new Payout();

		// Um NodeList aus einer Node zu bekommen
		Element itemElement = (Element) doc;

		if (itemElement == null) {
			validationParser.parseAndThrow((Document) itemElement);
		}

		// get the date created
		NodeList dateCreatedNodeList = itemElement.getElementsByTagName("created_at");
		if (dateCreatedNodeList != null && dateCreatedNodeList.item(0) != null
				&& DOMUtils.getNodeContent(dateCreatedNodeList.item(0)) != null) {
				payout.setDateCreated(DOMUtils.getNodeContent(dateCreatedNodeList.item(0)));
		}
	
		// get the date updated
		NodeList dateUpdatedNodeList = itemElement.getElementsByTagName("updated_at");
		if (dateUpdatedNodeList != null && dateUpdatedNodeList.item(0) != null
				&& DOMUtils.getNodeContent(dateUpdatedNodeList.item(0)) != null) {
				payout.setDateUpdated(DOMUtils.getNodeContent(dateUpdatedNodeList.item(0)));
		}

		// get payment id
		NodeList paymentIdNodeList = itemElement.getElementsByTagName("paymenttype_id");
		if (paymentIdNodeList != null && paymentIdNodeList.item(0) != null
				&& DOMUtils.getNodeContent(paymentIdNodeList.item(0)) != null) {
			try {
			payout.setPaymentTypeId(Integer.parseInt(DOMUtils.getNodeContent(paymentIdNodeList
					.item(0))));
			}catch(NumberFormatException e) {}
		}
		
		// get amount
		NodeList amountNodeList = itemElement.getElementsByTagName("amount");
		if (amountNodeList != null && amountNodeList.item(0) != null
				&& DOMUtils.getNodeContent(amountNodeList.item(0)) != null) {
			try {
				payout.setAmount(Double.parseDouble(DOMUtils.getNodeContent(amountNodeList
						.item(0))));
			}catch(NumberFormatException e) {}
		}
		
		// get the currency
		NodeList currencyNodeList = itemElement.getElementsByTagName("currency");
		if (currencyNodeList != null && currencyNodeList.item(0) != null
				&& DOMUtils.getNodeContent(currencyNodeList.item(0)) != null) {
			payout.setCurrency(DOMUtils.getNodeContent(currencyNodeList.item(0)));
		}
		
		// get the paymentStatus
		NodeList paymentStatusNodeList = itemElement.getElementsByTagName("paymentstatus_id");
		if (paymentStatusNodeList != null && paymentStatusNodeList.item(0) != null
				&& DOMUtils.getNodeContent(paymentStatusNodeList.item(0)) != null) {
				payout.setPaymentStatus(Integer.parseInt(DOMUtils.getNodeContent(paymentStatusNodeList.item(0))));
		}

		// get the id
		String payoutId = itemElement.getAttribute("id");
        if (payoutId != null) {
        	try {
               payout.setId(Integer.parseInt(payoutId));
        	}catch(NumberFormatException e) {}
        }
		
		return payout;

	}
	
}
