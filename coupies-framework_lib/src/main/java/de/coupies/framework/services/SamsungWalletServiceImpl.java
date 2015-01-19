package de.coupies.framework.services;

import android.content.Context;
import android.sax.Element;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.services.content.handler.SamsungWalletTicketDataHandler;
import de.coupies.framework.session.CoupiesSession;

public class SamsungWalletServiceImpl extends AbstractCoupiesService implements SamsungWalletService {

	public SamsungWalletServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}
	
	private Object issueTicket(Handler handler, CoupiesSession session, int passId) throws CoupiesServiceException {
		String url = getAPIUrl("swtickets/"+passId, handler);
		HttpClient httpClient = createHttpClient(session);
		Object result = consumeService(httpClient.post(url), handler);
		return result;
	}
	
	private Object viewTicket(Handler handler, CoupiesSession session, int passId) throws CoupiesServiceException {
		String url = getAPIUrl("swtickets/"+passId, handler);
		HttpClient httpClient = createHttpClient(session);
		Object result = consumeService(httpClient.get(url), handler);
		return result;
	}
	
	public String issueTicket(CoupiesSession session, int passId) throws CoupiesServiceException {
		SamsungWalletTicketDataHandler handler = new SamsungWalletTicketDataHandler();
		Object result = issueTicket(handler, session, passId);
		return result.toString();
	}
	
	public String viewTicket(CoupiesSession session, int passId) throws CoupiesServiceException {
		SamsungWalletTicketDataHandler handler = new SamsungWalletTicketDataHandler();
		Element result = (Element) viewTicket(handler, session, passId);
		return result.toString();
	}
	
}
