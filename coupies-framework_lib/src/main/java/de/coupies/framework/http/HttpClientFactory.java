package de.coupies.framework.http;

import de.coupies.framework.connection.CoupiesConnection;

/**
 * http client factory
 * 
 * @author thomas.volk@denkwerk.com
 * @since Sep 7, 2010
 *
 */
public interface HttpClientFactory {
	
	public HttpClient createHttpClient();
	
	public CoupiesConnection getConnection();
}
