package de.coupies.framework.services.async;

import android.content.Context;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.async.tasks.AsyncPayoutTask.AsyncPayoutListener;
import de.coupies.framework.services.content.DocumentParseException;

/**
 * @author larseimermacher
 * @since 13.02.2013
 * 
 */
public class AsyncPayoutService extends AbstractAsyncServices {
	
	public AsyncPayoutService(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}
	
	/**
	 * This Method is the Service to propose the User-Payout for PayPal
	 * @throws DocumentParseException 
	 */
	public void userPayout_async(AsyncPayoutListener listener, String paypal,
			String token) {
		setPaypal(paypal);
		setPaymentTypeId(3);		// 3 for transaction with PayPal-Account
		setToken(token);
		
		runAsyncPayout(listener, "user/payment");
	}

	/**
	 * This Method is the Service to propose the User-Payout for an bank transaction
	 */
	public void userPayout_async(AsyncPayoutListener listener,
			Long kontonummer, Long bankleitzahl, String name, String token) {
		setBankleitzahl(bankleitzahl);
		setKontonummer(kontonummer);
		setName(name);
		setPaymentTypeId(4);		// 4 for transaction with bankaccount
		setToken(token);
		
		runAsyncPayout(listener, "user/payment");
	}
}
