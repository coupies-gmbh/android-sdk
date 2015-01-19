package de.coupies.framework.services;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.content.DocumentProcessor.InputStreamHandler;

public interface ResourceService {
	
	public Object loadResource(String url, InputStreamHandler handler) throws CoupiesServiceException;
}
