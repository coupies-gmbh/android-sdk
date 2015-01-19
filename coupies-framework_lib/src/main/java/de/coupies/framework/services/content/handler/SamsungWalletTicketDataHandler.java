package de.coupies.framework.services.content.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.AuthentificationService;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.Validation;
import de.coupies.framework.services.content.ValidationParser;

public  class SamsungWalletTicketDataHandler implements DocumentHandler {
	private final ValidationParser validationParser = new ValidationParser();
	
	protected ValidationParser getValidationParser() {
		return validationParser;
	}
	
	public Object handleDocument(Document doc) throws CoupiesServiceException {
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		String ticketId = null;
		
		Element ticketNode = (Element) doc.getElementsByTagName("ticket_id").item(0);
		if(ticketNode == null) {
			Validation validation = ValidationParser.parse(doc);
			throw new AuthentificationService.AuthentificationException(validation.toString());
		}

		try {
			ticketId = ticketNode.getFirstChild().getNodeValue();
		} catch (Exception e) {
			Log.w("Contenthandler", "Kein Wert für ticket_id eingetragen");
		}
				
		return ticketId;
	}
}