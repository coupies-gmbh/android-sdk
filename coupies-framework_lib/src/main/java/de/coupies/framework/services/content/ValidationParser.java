/**
 * 
 */
package de.coupies.framework.services.content;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.http.HttpClient.UnauthorizedException;
import de.coupies.framework.utils.DOMUtils;
import de.coupies.framework.utils.StringUtils;

/**
 * @author thomas.volk@denkwerk.com
 * @since 27.08.2010
 *
 */
public class ValidationParser {
	
	public static Validation parse(Document doc) throws DocumentParseException, UnauthorizedException{
		Validation validation = new Validation();
		Element valNode = (Element) doc.getElementsByTagName("validation").item(0);
		if(valNode != null) {
			NodeList errorNodeList = valNode.getElementsByTagName("error");
			for(int i=0; i < errorNodeList.getLength(); i++) {
				Element errorNode = (Element) errorNodeList.item(i);
				validation.getErrors().add(new Validation.Error(
					errorNode.getAttribute("field"), DOMUtils.getNodeContent(errorNode)));
			}
		}
		else {
			Element errNode = (Element) doc.getElementsByTagName("error").item(0);
			if(errNode == null) {
				throw new DocumentParseException("no result");
			}
			else {
				String code = errNode.getAttribute("code");
				String msg = errNode.getAttribute("message");
				
				if(StringUtils.isNotEmpty(msg)) {
					validation.getErrors().add(new Validation.Error(code, msg));
				}
				else {
					validation.getErrors().add(new Validation.Error("", DOMUtils.getNodeContent(errNode)));
				}
				
				if(code.equals("401"))
					throw new UnauthorizedException(validation.toString());
			}
		}
		return validation;
	}
	
	public void parseAndThrow(Document doc) throws CoupiesServiceException{
		Validation validation = parse(doc);
		throw new CoupiesServiceException(validation.toString());
	}
}
