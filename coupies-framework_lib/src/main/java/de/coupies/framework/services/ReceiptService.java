package de.coupies.framework.services;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Receipt;
import de.coupies.framework.http.HttpClient.HttpStatusException;
import de.coupies.framework.session.CoupiesSession;

public interface ReceiptService {
	/**
	 * Method to upload an image file
	 * 
	 * @param session coupies session
	 * @param image file
	 * @return	Returns an Redemption-Object
	 * @throws DocumentParseException
	 * @throws HttpStatusException
	 * @throws UnsupportedEncodingException 
	 */
	
	Receipt uploadReceipt(CoupiesSession session, List<File> image ,boolean withProgressBar) throws CoupiesServiceException, UnsupportedEncodingException; 
			
	public String uploadReceipt_html(CoupiesSession session,List<File> image,boolean withProgressBar) throws CoupiesServiceException, UnsupportedEncodingException;
}
