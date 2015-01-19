/**
 * 
 */
package de.coupies.framework.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.News;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.utils.DOMUtils;

/**
 * @author thomas.volk@denkwerk.com
 * @since Sep 3, 2010
 *
 */
public class NewsServiceImpl extends AbstractCoupiesService 
		implements NewsService {

	private final static class NewsListHandler implements DocumentHandler {
		public Object handleDocument(Document inDoc)
				throws CoupiesServiceException {
			NodeList errorNode = inDoc.getElementsByTagName("error");
			if(errorNode != null && errorNode.item(0)!=null){
				errorNode = errorNode.item(0).getChildNodes();
				if(errorNode != null){
					ValidationParser mValidationParser = new ValidationParser();
					mValidationParser.parseAndThrow(inDoc);
				}
			}
			
			List<News> newsList = new ArrayList<News>();
			Node newsNode = inDoc.getElementsByTagName("news").item(0);
			NodeList messageNodes = ((Element) newsNode).getElementsByTagName("message");
			for(int i = 0; i < messageNodes.getLength(); i++) {
				News news = new News();
				Node messageNode = messageNodes.item(i);
				news.setMessage(DOMUtils.getNodeContent(messageNode));
				SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd");
				String creationDate = DOMUtils.getAttribute(messageNode, "creationTime");
				if(creationDate == null) {
					news.setDate(new Date());
				}
				else {								
					try {
						news.setDate(format.parse(creationDate));
					} catch (ParseException e) {
						news.setDate(new Date());
					}
				}
				newsList.add(news);
			}
			return newsList;
		}
	}
	
	public NewsServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}

	private Object getNews(Handler handler, CoupiesSession session)
			throws CoupiesServiceException {
		String url = getAPIUrl("user/news", handler);
		Object result = consumeService(createHttpClient(session).get(url),
				handler);
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<News> getNews(CoupiesSession session) throws CoupiesServiceException {
		DocumentHandler handler = new NewsListHandler();
		Object result = getNews(handler, session);
		return (List<News>) result;
	}

	public String getNews_html(CoupiesSession session) throws CoupiesServiceException {
		return (String) getNews(HTML_RESULT_HANDLER, session);
	}

}
