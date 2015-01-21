package de.coupies.framework;

import android.content.Context;
import de.coupies.framework.connection.CoupiesConnectionImpl;
import de.coupies.framework.http.HttpClientFactoryImpl;
import de.coupies.framework.services.ServiceFactory;
import de.coupies.framework.services.ServiceFactoryImpl;
/**
 * coupies manager
 * 
 * @author thomas.volk@denkwerk.com
 * @since 15.12.2010
 *
 */
public class CoupiesManager {
	private static final String LIVE_HOST = "www.coupies.de/api.php";
	private static final String DEVEL_HOST = "sandbox.coupies.de/api.php";
	private static final int PORT = 443;
	private static final String PROTOCOL = "https";
	
	/** this is a static class only */
	private CoupiesManager() {
	}
	/**
	 * create the coupies service factory
	 * @param apiKey
	 * @param protocol
	 * @param host
	 * @param port
	 * @return service factory
	 */
	public static ServiceFactory createServiceFactory(Context context, String apiKey, 
			String protocol, String host, int port, String apiLevel) {
		return new ServiceFactoryImpl(
				new HttpClientFactoryImpl(
				new CoupiesConnectionImpl(apiKey, protocol, host, port, apiLevel)), context);
	}
	
	/**
	 * create coupies live service factory
	 * @param coupiesApiKey coupies API Key
	 * @return service factory
	 */
	public static ServiceFactory createLiveServiceFactory(Context context, String coupiesApiKey, String apiLevel) {
		return createServiceFactory(context, coupiesApiKey, PROTOCOL, LIVE_HOST, PORT, apiLevel);
	}
	/**
	 * create test service factory
	 * @param coupiesApiKey coupies API Key
	 * @return test service factory
	 */
	public static ServiceFactory createTestServiceFactory(Context context, String coupiesApiKey, String apiLevel) {
		return createServiceFactory(context, coupiesApiKey, PROTOCOL, DEVEL_HOST, PORT, apiLevel);
	}

}
