package de.coupies.framework.services;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.session.CoupiesSession;

public interface PayoutService{
	
	/**
	 * 
	 * @param paypal	The user PayPal-Email-Adress for the transaction
	 * @throws HttpStatusException
	 * @throws DocumentParseException
	 */
	public void userPayout(String paypal,CoupiesSession session, String token) throws CoupiesServiceException;
	
	/**
	 * 
	 * @param kontonummer	The users account number for the transaction
	 * @param bankleitzahl	The bank code number of the users bank
	 * @param name	The first and last name of the user
	 * @throws HttpStatusException
	 * @throws DocumentParseException
	 */
	public void userPayout(String kontonummer, String bankleitzahl, String name, CoupiesSession session, String token) throws CoupiesServiceException;
}