package de.coupies.framework.http;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.http.StatusLine;

import de.coupies.framework.CoupiesServiceException;

/**
 * http client 
 * 
 * @author thomas.volk@denkwerk.com
 * @since 15.12.2010
 *
 */
public interface HttpClient {
	@SuppressWarnings("serial")
	public static class HttpStatusException extends CoupiesServiceException {
		public static void throwExeption(StatusLine statusLine, URI uri)
				throws HttpStatusException {
			int statusCode = statusLine.getStatusCode();
			String message = statusLine.getReasonPhrase();
			if (statusCode >= 500) {
				throw new HttpStatusException(statusCode, message);
			} 
		}

		private final int code;
		private final String description;

		public HttpStatusException(int code, String description) {
			super(String.format("%d - %s", code, description));
			this.code = code;
			this.description = description;
		}

		public int getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}
	}

	@SuppressWarnings("serial")
	public static class UnauthorizedException extends HttpStatusException {
		public static final int CODE = 401;
		
		public UnauthorizedException(String description) {
			super(CODE, description);
		}

		public static void throwExeption(StatusLine statusLine, URI uri) throws HttpStatusException {
			HttpStatusException.throwExeption(statusLine, uri);
			int statusCode = statusLine.getStatusCode();
			String message = statusLine.getReasonPhrase();
			if (statusCode >= 400) {
				throw new UnauthorizedException(message);
			} 
		}
	}

	public class Credentials {
		private final String login;
		private final String password;

		public Credentials(String login, String password) {
			this.login = login;
			this.password = password;
		}

		public String getLogin() {
			return login;
		}

		public String getPassword() {
			return password;
		}
	}

	public Integer getSocketTimeout();

	public void setSocketTimeout(Integer inSocketTimeout);

	public Integer getConnectionTimeout();

	public void setConnectionTimeout(Integer inConnectionTimeout);

	public boolean getCheckHttpStatus();

	public void setCheckHttpStatus(boolean checkHttpStatus);

	public Map<String, String> getParameters();

	public Map<String, String> getHeaders();

	public void setParameters(Map<String, String> parameters) ;
	
	public InputStream get(String url) throws HttpStatusException;

	public InputStream put(String url) throws HttpStatusException;

	public InputStream post(String url) throws HttpStatusException;
	
	public InputStream postWithProgress(String url, List<File> image, boolean withProgressBar) throws HttpStatusException, UnsupportedEncodingException;
	
	public void setParameter(String key, Object val);

	public void setCredentials(String login, String password);
	
	public void setHeader(String name, String value);
}
