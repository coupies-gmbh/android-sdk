/**
 * 
 */
package de.coupies.framework.services;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentProcessor;
import de.coupies.framework.services.content.DocumentProcessor.InputStreamHandler;


/**
 * @author thomas.volk@denkwerk.com
 * @since Sep 7, 2010
 *
 */
public class ResourceServiceImpl extends AbstractRestfulService implements ResourceService{

	
	public ResourceServiceImpl(HttpClientFactory httpClientFactory) {
		super(httpClientFactory);
	}

	public Object loadResource(String url, InputStreamHandler handler) throws CoupiesServiceException {
		HttpClient client = getHttpClientFactory().createHttpClient();
		if(url != null && url.length()>0)
			return new DocumentProcessor().execute(client.get(url), handler);
		else 
			return null;
	}
}
