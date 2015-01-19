package de.coupies.framework.services.content.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Receipt;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.utils.DOMUtils;

public class ReceiptHandler implements DocumentHandler {
	ValidationParser validationParser = new ValidationParser();

	public Receipt handleDocument(Document doc) throws CoupiesServiceException {
		NodeList errorNode = doc.getElementsByTagName("error");
		if(errorNode != null && errorNode.item(0)!=null){
			errorNode = errorNode.item(0).getChildNodes();
			if(errorNode != null){
				ValidationParser mValidationParser = new ValidationParser();
				mValidationParser.parseAndThrow(doc);
			}
		}
		
		Receipt receipt = new Receipt();
		NodeList redemptionNodes = doc.getElementsByTagName("receipt");
		if (redemptionNodes == null || redemptionNodes.item(0) == null) {
			ValidationParser validationParser = new ValidationParser();
			validationParser.parseAndThrow(doc);
		}
		receipt = parseReceipt(redemptionNodes.item(0));

		return receipt;
	}

	public static Receipt parseReceipt(Node doc) throws CoupiesServiceException {

		ValidationParser validationParser = new ValidationParser();
		Receipt receipt = new Receipt();

		// Um NodeList aus einer Node zu bekommen
		Element itemElement = (Element) doc;

		if (itemElement == null) {
			validationParser.parseAndThrow((Document) itemElement);
		}

		// get the sum
		NodeList sumNodeList = itemElement.getElementsByTagName("sum");
		if (sumNodeList != null && sumNodeList.item(0) != null
				&& DOMUtils.getNodeContent(sumNodeList.item(0)) != null) {
			receipt.setSum(Double.valueOf(DOMUtils.getNodeContent(sumNodeList
					.item(0))));
		}

		// get the date
		NodeList dateNodeList = itemElement.getElementsByTagName("date");
		if (dateNodeList != null && dateNodeList.item(0) != null
				&& DOMUtils.getNodeContent(dateNodeList.item(0)) != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				java.util.Date dt = formatter.parse(String.valueOf(dateNodeList
						.item(0)));
				receipt.setDate(dt);
			} catch (ParseException e) {
			}
		}

		// get the date
		NodeList imageUrlNodeList = itemElement.getElementsByTagName("image");
		if (imageUrlNodeList != null && imageUrlNodeList.item(0) != null
				&& DOMUtils.getNodeContent(imageUrlNodeList.item(0)) != null) {
			receipt.setImageUrl(DOMUtils.getNodeContent(imageUrlNodeList
					.item(0)));
		}

		// get the id
		String receiptId = itemElement.getAttribute("id");
		if (receiptId != null) {
			receipt.setId(Integer.parseInt(receiptId));
		}
		return receipt;

	}
}
