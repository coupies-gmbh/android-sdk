package de.coupies.framework.services.content.handler;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.User;
import de.coupies.framework.services.AuthentificationService;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.Validation;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.utils.DOMUtils;

public  class UserDataHandler implements DocumentHandler {
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
		
		Element userNode = (Element) doc.getElementsByTagName("user").item(0);
		if(userNode == null) {
			Validation validation = ValidationParser.parse(doc);
			throw new AuthentificationService.AuthentificationException(validation.toString());
		}
		User user = new User();
		user.setId(Integer.valueOf(userNode.getAttribute("id")));
		user.setLatestPayout(new PayoutHandler().handleDocument(doc));
		Node subNode = userNode.getElementsByTagName("rememberkey").item(0);
		user.setRememberKey(DOMUtils.getNodeContent(subNode));
		subNode = userNode.getElementsByTagName("facebook_id").item(0);
		user.setFacebookId(DOMUtils.getNodeContent(subNode));
		subNode = userNode.getElementsByTagName("email").item(0);
		user.setEmail(DOMUtils.getNodeContent(subNode));
		subNode = userNode.getElementsByTagName("push_intensity").item(0);
		user.setPushIntensity(Integer.parseInt(DOMUtils.getNodeContent(subNode)));
		subNode = userNode.getElementsByTagName("balance").item(0);
		user.setBalance(Double.parseDouble(DOMUtils.getNodeContent(subNode)));
		subNode = userNode.getElementsByTagName("saved_total").item(0);
		user.setSaved_total(Double.parseDouble(DOMUtils.getNodeContent(subNode)));
		subNode = userNode.getElementsByTagName("c2dm_id").item(0);
		user.setPushNotificationToken(DOMUtils.getNodeContent(subNode));
		subNode = userNode.getElementsByTagName("currency").item(0);
		user.setCurrency(DOMUtils.getNodeContent(subNode));
		subNode = userNode.getElementsByTagName("firstname").item(0);
		user.setFirstName(DOMUtils.getNodeContent(subNode));
		subNode = userNode.getElementsByTagName("lastname").item(0);
		user.setLastName(DOMUtils.getNodeContent(subNode));
		return user;
	}
}