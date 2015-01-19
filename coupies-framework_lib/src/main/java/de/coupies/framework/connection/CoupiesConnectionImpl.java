package de.coupies.framework.connection;

/**
 * @author thomas.volk@denkwerk.com
 * @since 31.08.2010
 */
public class CoupiesConnectionImpl implements CoupiesConnection {
	private final String apiKey;
	private final String apiLevel;
	private final String host;
	private final int port;
	private final String protocol;
	private Integer socketTimeout;
	private Integer connectionTimeout;

	public CoupiesConnectionImpl(String apiKey, String protocol, String host,
			int port, String apiLevel) {
		super();
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.apiKey = apiKey;
		this.apiLevel = apiLevel;
	}

	public CoupiesConnectionImpl(String apiKey, String protocol, String host,
			String apiLevel) {
		this(apiKey, protocol, host, 443, apiLevel);
	}

	public Integer getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(Integer inSocketTimeout) {
		socketTimeout = inSocketTimeout;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer inConnectionTimeout) {
		connectionTimeout = inConnectionTimeout;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHostAndPort() {
//		if (port != 80) {
//			return String.format("%s:%d", host, port);
//		} else {
			return host;
//		}
	}

	public String getAPIBaseUrl() {
		return String.format("%s://%s", getProtocol(), getHostAndPort());
	}

	public String getApiLevel() {
		return apiLevel;
	}
}
