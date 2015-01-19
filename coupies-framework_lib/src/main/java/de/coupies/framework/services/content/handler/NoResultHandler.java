package de.coupies.framework.services.content.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.ValidationParser;

public class NoResultHandler implements DocumentHandler {
	public Object handleDocument(Document doc) throws CoupiesServiceException {
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		if(doc!=null && (doc.hasAttributes() || doc.hasChildNodes())){
			Element valNode = (Element) doc.getElementsByTagName("validation").item(0);
				if(valNode!=null){
					ValidationParser validationParser = new ValidationParser();
					validationParser.parseAndThrow(doc);
				}
		}
		return null;
	}
}