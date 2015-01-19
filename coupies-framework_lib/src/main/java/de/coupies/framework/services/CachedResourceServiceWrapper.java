/**
 * 
 */
package de.coupies.framework.services;

import java.util.HashMap;
import java.util.Map;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.content.DocumentProcessor.InputStreamHandler;

/**
 * siple chache wrapped arround the ResourceClient
 * 
 * @author thomas.volk@denkwerk.com
 * @since Sep 7, 2010
 *
 */
public class CachedResourceServiceWrapper implements ResourceService {
	private final ResourceService resourceClient;
	private Map<String, Object> cache = new HashMap<String, Object>();
	
	public CachedResourceServiceWrapper(ResourceService inResourceClient) {
		super();
		resourceClient = inResourceClient;
	}

	public ResourceService getResourceClient() {
		return resourceClient;
	}

	public synchronized Object loadResource(String inUrl, InputStreamHandler inHandler)
			throws CoupiesServiceException {
		Object payload = cache.get(inUrl);
		if(payload == null) {
			payload = getResourceClient().loadResource(inUrl, inHandler);
			cache.put(inUrl, payload);
		}
		return payload;
	}

}
