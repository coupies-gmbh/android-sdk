package de.coupies.framework.services.content.handler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Location;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;

public class LocationListHandler implements DocumentHandler {
	public Object handleDocument(Document doc)
			throws CoupiesServiceException {
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		List<Location> locations = new ArrayList<Location>();
		NodeList locationNodes = doc.getElementsByTagName("location");
		for (int i = 0; i < locationNodes.getLength(); i++) {
			Location location = LocationHandler.parseLocation(locationNodes.item(i));
			locations.add(location);
		}
		return locations;
		
	}
}
