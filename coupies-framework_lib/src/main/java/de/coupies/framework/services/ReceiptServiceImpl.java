package de.coupies.framework.services;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.content.Context;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Receipt;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.handler.ReceiptHandler;
import de.coupies.framework.session.CoupiesSession;

public class ReceiptServiceImpl extends AbstractCoupiesService implements ReceiptService{
	
	
	public ReceiptServiceImpl(HttpClientFactory httpClientFactory,
			Context context) {
		super(httpClientFactory, context);
	}

	/**
	 * Upload an image file with this method. If no progressBar needed you can fill this parameter with null
	 */
	public Receipt uploadReceipt(CoupiesSession session, List<File> image, boolean withProgressBar) throws CoupiesServiceException, UnsupportedEncodingException {
		DocumentHandler handler = new ReceiptHandler();
		
		String url = getAPIUrl("receipts/new", handler);
		HttpClient client = createHttpClient(session);
		Object result = consumeService(client.postWithProgress(url, image, withProgressBar), handler);
		return (Receipt)result;
	}
	
	/**
	 * Upload an image file with this method. If no progressBar needed you can fill this parameter with null
	 */
	public String uploadReceipt_html(CoupiesSession session,List<File> image, boolean withProgressBar) throws CoupiesServiceException, UnsupportedEncodingException {
		
		String url = getAPIUrl("receipts/new", HTML_RESULT_HANDLER);
		HttpClient client = createHttpClient(session);
		Object result = consumeService(client.postWithProgress(url, image, withProgressBar), HTML_RESULT_HANDLER);
		return (String)result;
	}

}
