package de.coupies.framework.services;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Category;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;

/**
 * @author thomas.volk@denkwerk.com
 * @since 30.08.2010
 *
 */
public class CategoryServiceImpl extends AbstractCoupiesService 
		implements CategoryService {
	private class CategoryHandler implements DocumentHandler {
		public Object handleDocument(Document inDoc) throws CoupiesServiceException {
			NodeList errorNode = inDoc.getElementsByTagName("error");
			if(errorNode != null && errorNode.item(0)!=null){
				errorNode = errorNode.item(0).getChildNodes();
				if(errorNode != null){
					ValidationParser mValidationParser = new ValidationParser();
					mValidationParser.parseAndThrow(inDoc);
				}
			}
			
			Node categoryNode = inDoc.getElementsByTagName("interest").item(0);
			return parseCategory(categoryNode);
		}
	}
	private class CategoryListHandler implements DocumentHandler {
		public Object handleDocument(Document inDoc) throws CoupiesServiceException {
			NodeList errorNode = inDoc.getElementsByTagName("error");
			if(errorNode != null && errorNode.item(0)!=null){
				errorNode = errorNode.item(0).getChildNodes();
				if(errorNode != null){
					ValidationParser mValidationParser = new ValidationParser();
					mValidationParser.parseAndThrow(inDoc);
				}
			}
			
			NodeList couponNodes = inDoc.getElementsByTagName("interest");
			List<Category> categories = new ArrayList<Category>();
			for (int i = 0; i < couponNodes.getLength(); i++) {
				Node categoryNode = couponNodes.item(i);
				if(categoryNode.getNodeType() == Node.ELEMENT_NODE) {
					categories.add(parseCategory(categoryNode));
				}	
			}
			return categories;
		}
	}


	public CategoryServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}

	private Category parseCategory(Node categoryNode) {
		Element categoryElement = (Element) categoryNode;
		Category category = new Category();
		category.setId(Integer.valueOf(categoryElement.getAttribute("id")));
		category.setCouponsCount(Integer.valueOf(categoryElement.getAttribute("coupons")));
		category.setUrl(categoryElement.getAttribute("url"));
		
		Node subNode = categoryElement.getElementsByTagName("title").item(0);
		category.setTitle(getNodeValue(subNode));
		subNode = categoryElement.getElementsByTagName("description").item(0);
		category.setDescription(getNodeValue(subNode));
		return category;
	}
	
	private Object getCategory(Handler interestHandler,
			CoupiesSession session, int id) throws CoupiesServiceException {
		String url = getAPIUrl(String.format("interests/%d", id), interestHandler);
		HttpClient client = createHttpClient(session);
		Log.d("COUPIES samsung wallet", "API request "+url+" sent to the server to get the list");
		Object result = consumeService(client.get(url),interestHandler);
		return result;
	}

	private Object getAllCategoriesForRadius(Handler handler,
			CoupiesSession session, Coordinate coordinate, 
			double radius) throws CoupiesServiceException {
		String url = getAPIUrl("interests", handler);
		HttpClient client = createHttpClient(session);
		client.setParameter("latitude", coordinate.getLatitude());
		client.setParameter("longitude", coordinate.getLongitude());
		client.setParameter("radius", radius);
		Object result = consumeService(client.get(url),handler);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Category> getCategoriesWithPosition(CoupiesSession session, 
			Coordinate coordinate, double radius) throws CoupiesServiceException {
		DocumentHandler handler = new CategoryListHandler();
		Object result = getAllCategoriesForRadius(handler, session, 
				coordinate, radius);
		return (List<Category>) result;
	}

	public Category getCategory(CoupiesSession session, int id) throws CoupiesServiceException {
		DocumentHandler interestHandler = new CategoryHandler();
		Object result = getCategory(interestHandler, session, id);
		Log.d("COUPIES samsung wallet", "The server returned back the list with the comming result "+result.toString());
		return (Category) result;
	}

	public String getCategoriesWithPosition_html(CoupiesSession session,
			Coordinate coordinate, double radius) throws CoupiesServiceException {
		return (String) getAllCategoriesForRadius(HTML_RESULT_HANDLER, session, 
				coordinate, radius);
	}

	public String getCategory_html(CoupiesSession session, int id)
			throws CoupiesServiceException {
		Log.d("COUPIES samsung wallet", "getting coupons list for server started");
		return (String) getCategory(HTML_RESULT_HANDLER, session, id);
	}

	

}
