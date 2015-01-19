package de.coupies.framework.http;

import de.coupies.framework.connection.CoupiesConnection;

public class DebugHttpClientFactoryImpl extends HttpClientFactoryImpl {

	public DebugHttpClientFactoryImpl(CoupiesConnection inConnection) {
		super(inConnection);
	}

	@Override
	protected void prepareHttpClient(HttpClient httpClient) {
		super.prepareHttpClient(httpClient);
		httpClient.setHeader("Host", "sandbox.coupies.de");
	}

}