package de.coupies.framework.services;

import android.content.Context;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.session.CoupiesSession;

public class PayoutServiceImpl extends AbstractCoupiesService 
		implements PayoutService {
	
	public PayoutServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}

	/**
	 * This Method is the Service to propose the User-Payout for PayPal
	 * @throws DocumentParseException 
	 */
	public void userPayout(String paypal, CoupiesSession session, String token) throws CoupiesServiceException{
		String url = getAPIUrl("user/payment", NO_RESULT_HANDLER);				// Get URL with https: scheme
		HttpClient client = createHttpClient(session);
		client.setParameter("paymenttype_id", 3);	// 3 for transaction with PayPal-Account
		client.setParameter("paypalaccount", paypal);
		client.setParameter("security_token", token);
		try{
			consumeService(client.post(url), NO_RESULT_HANDLER);
		}catch (CoupiesServiceException e) {
			if(e.getMessage().endsWith("Unexpected end of document")){
				// Bei erfolgreichem erstellen des DB eintrags (erstes mal) kommt
				// ein leeres Dokument zurück, ist also richtig!
			}else{
				e.printStackTrace();
				throw e;
			}
		}
	}

	/**
	 * This Method is the Service to propose the User-Payout for an bank transaction
	 */
	public void userPayout(String iban, String bic, String name, CoupiesSession session, String token)
			throws CoupiesServiceException {
		String url = getAPIUrl("user/payment", NO_RESULT_HANDLER);				// Get URL with https: scheme
		HttpClient client = createHttpClient(session);
		client.setParameter("paymenttype_id", 4);	// 4 for transaction with bankaccount
		client.setParameter("bankaccount_accountnumber", iban);
		client.setParameter("bankaccount_bankcode", bic);
		client.setParameter("bankaccount_name", name);
		client.setParameter("security_token", token);
		try{
			consumeService(client.post(url), NO_RESULT_HANDLER);
		}catch (CoupiesServiceException e) {
			if(e.getMessage().endsWith("Unexpected end of document")){
				// Bei erfolgreichem erstellen des DB eintrags (erstes mal) kommt
				// ein leeres Dokument zurück, ist also richtig!
			}else{
				throw e;
			}
		}
	}
	
}
