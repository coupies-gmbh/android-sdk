package de.coupies.framework.connection;

/**
 * connection data to the coupies server 
 * 
 * @author thomas.volk@denkwerk.com
 * @since 31.08.2010
 *
 */
public interface CoupiesConnection {
	/**
	 * @return coupies API key
	 */
	public String getApiKey();	
	
	/**
	 * @return coupies API Level
	 */
	public String getApiLevel();
	
	/**
	 * @return base URL
	 */
	public String getAPIBaseUrl();
	
	/**
	 * @return connection timeout
	 */
	public Integer getConnectionTimeout();
	
	/**
	 * @return socket timeout
	 */
	public Integer getSocketTimeout();

	/**
	 * 
	 * @return connection host
	 */
	public String getHost();
}
