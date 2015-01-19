/**
 * 
 */
package de.coupies.framework.services.content;

import de.coupies.framework.CoupiesServiceException;

@SuppressWarnings("serial")
public class DocumentParseException extends CoupiesServiceException {
	public DocumentParseException(Throwable throwable) {
		super(throwable);
	}

	public DocumentParseException(String detailMessage) {
		super(detailMessage);
	}
	
}