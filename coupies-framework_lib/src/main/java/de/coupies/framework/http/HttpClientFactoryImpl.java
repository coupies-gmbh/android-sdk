/**
 * 
 */
package de.coupies.framework.http;

import de.coupies.framework.connection.CoupiesConnection;


/**
 * http client factory
 * 
 * @author thomas.volk@denkwerk.com
 * @since Sep 7, 2010
 *
 */
public class HttpClientFactoryImpl implements HttpClientFactory {
	private final CoupiesConnection connection;

	public HttpClientFactoryImpl(CoupiesConnection inConnection) {
		super();
		connection = inConnection;
	}
	
	public CoupiesConnection getConnection() {
		return connection;
	}
		
	public HttpClient createHttpClient() {
		HttpClient httpClient = new HttpClientImpl();
		prepareHttpClient(httpClient);
		return httpClient;
	}

	/**
	 * typo error please use prepareHttpClient(HttpClient httpClient)
	 * instead
	 * 
	 * @param httpClient
	 */
	@Deprecated
	protected void prepareHttpCleint(HttpClient httpCleint) {
		prepareHttpClient(httpCleint);
	}
	
	protected void prepareHttpClient(HttpClient httpClient) {
		httpClient.setSocketTimeout(getConnection().getSocketTimeout());
		httpClient.setConnectionTimeout(getConnection().getConnectionTimeout());
		String apiKey = getConnection().getApiKey();
		if(apiKey != null) {
			httpClient.setParameter("key", apiKey);
		}
		String apiLevel = getConnection().getApiLevel();
		if(apiLevel!=null){
			httpClient.setParameter("api_level", apiLevel);
		}
	}
}
