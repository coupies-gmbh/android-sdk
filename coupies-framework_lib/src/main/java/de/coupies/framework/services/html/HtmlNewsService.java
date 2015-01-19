package de.coupies.framework.services.html;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.session.CoupiesSession;

public interface HtmlNewsService {

	String getNews_html(CoupiesSession session) throws CoupiesServiceException;

}