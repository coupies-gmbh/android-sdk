package de.coupies.framework.services.content.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;

public class BooleanResultHandler implements DocumentHandler {
	public Object handleDocument(Document doc) throws CoupiesServiceException {
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		Element rspNode = (Element) doc.getElementsByTagName("rsp").item(0);
		if (rspNode == null)
            throw new DocumentParseException("Invalid response.");
		else
            return rspNode.getAttribute("stat").equals("ok");

	}
}
