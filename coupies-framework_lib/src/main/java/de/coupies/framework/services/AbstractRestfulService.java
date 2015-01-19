/**
 * 
 */
package de.coupies.framework.services;

import de.coupies.framework.http.HttpClientFactory;

/**
 * @author thomas.volk@denkwerk.com
 * @since Sep 7, 2010
 *
 */
public class AbstractRestfulService {
	private final HttpClientFactory httpClientFactory;
	
	public AbstractRestfulService(HttpClientFactory httpClientFactory) {
		this.httpClientFactory = httpClientFactory;
	}

	protected HttpClientFactory getHttpClientFactory() {
		return httpClientFactory;
	}
}
