package de.coupies.framework.services;

import java.util.List;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.News;
import de.coupies.framework.services.html.HtmlNewsService;
import de.coupies.framework.session.CoupiesSession;

public interface NewsService extends HtmlNewsService {

	/**
	 * @param session coupies session
	 * @return news list
	 * @throws CoupiesServiceException 
	 */
	List<News> getNews(CoupiesSession session) throws CoupiesServiceException;

}