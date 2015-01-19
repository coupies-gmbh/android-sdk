package de.coupies.framework.services.content.handler;

import java.io.IOException;
import java.io.InputStream;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.services.content.DocumentProcessor.InputStreamHandler;
import de.coupies.framework.utils.IOUtils;

public class HtmlResultHandler implements InputStreamHandler {

	public Object read(InputStream inStream) throws CoupiesServiceException {
		try {
			return IOUtils.toString(inStream, "UTF-8");
		} catch (IOException e) {
			throw new DocumentParseException(e);
		}
	}
	
}