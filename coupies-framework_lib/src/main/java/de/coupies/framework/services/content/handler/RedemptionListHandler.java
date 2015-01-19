package de.coupies.framework.services.content.handler;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Redemption;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;

/**
 * 
 * @author larseimermacher
 *
 */
public class RedemptionListHandler implements DocumentHandler{
		
	public List<Redemption> handleDocument(Document doc) throws CoupiesServiceException{
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		List<Redemption> redemptionList = new ArrayList<Redemption>();
		
		NodeList redemptionNodes = doc.getElementsByTagName("redemption");
		if (redemptionNodes != null && redemptionNodes.getLength() > 0) {
			for (int i = 0; i < redemptionNodes.getLength(); i++) {		
				Node redemptionNode = redemptionNodes.item(i);
				if (redemptionNode.getNodeType() == Node.ELEMENT_NODE) {
					redemptionList.add(RedemptionHandler.parseRedemption(redemptionNode));
				}
			}
		}
		return redemptionList;
	}
}
