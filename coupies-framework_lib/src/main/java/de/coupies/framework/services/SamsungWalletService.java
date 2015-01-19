package de.coupies.framework.services;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.session.CoupiesSession;

public interface SamsungWalletService {

	/**
	 * This function registers a new Samsung Wallet Ticket and returns
	 * the TicketId
	 * 
	 * @author Karim Wahishi
	 * 
	 * @param session
	 * @param passId
	 * @return String ticketId the Samsung Wallet Ticket id
	 * @throws CoupiesServiceException
	 */
	public String issueTicket(CoupiesSession session, int passId) throws CoupiesServiceException;
	
	public String viewTicket(CoupiesSession session, int passId) throws CoupiesServiceException;
	
}
