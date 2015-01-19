package de.coupies.framework.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.controller.redemption.RedemptionActivity;
import de.coupies.framework.http.ProgressMultipartEntity.ProgressListener;
import de.coupies.framework.utils.URLUtils;

/**
 * http client 
 * 
 * @author thomas.volk@denkwerk.com
 * @since 15.12.2010
 *
 */
public class HttpClientImpl implements HttpClient {
	private Map<String, String> parameters;
	private Credentials credentials;
	private boolean checkHttpStatus = true;
	private Map<String, String> headers;
	private Integer socketTimeout;
	private Integer connectionTimeout;

	public HttpClientImpl() {
	}

	private List<NameValuePair> prepareParameters() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (Entry<String, String> entry : getParameters().entrySet()) {
			String value = entry.getValue();
			if (value != null) {
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), value));
			}
		}
		
		return nameValuePairs;
	}
	
	/**
	 * get HTTPS-Client with Trust-All SSL
	 * @return Default HttpClient with AllowAll SSLSocketFactory
	 * @throws CoupiesServiceException 
	 */
	private DefaultHttpClient createHttpsClient() {
		HttpParams params = new BasicHttpParams();

	    //Set main protocol parameters
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);

	    // Turn off stale checking.  Our connections break all the time anyway, and it's not worth it to pay the penalty of checking every time.
	    HttpConnectionParams.setStaleCheckingEnabled(params, false);
	    // FIX v2.2.1+ - Set timeout to 30 seconds, seems like 5 seconds was not enough for good communication
	    HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
	    HttpConnectionParams.setSoTimeout(params, 30 * 1000);
	    HttpConnectionParams.setSocketBufferSize(params, 8192);

	    // Don't handle redirects -- return them to the caller.  Our code often wants to re-POST after a redirect, which we must do ourselves.
	    HttpClientParams.setRedirecting(params, true);

	    // Register our own "trust-all" SSL scheme
	    SchemeRegistry schReg = new SchemeRegistry();
	    try
	    {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);
	        
	        SSLSocketFactory sslSocketFactory = new CoupiesSSLSocketFactory(trustStore);
	        sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        
	        schReg.register(new Scheme("http", sslSocketFactory, 80));
	        schReg.register(new Scheme("https", sslSocketFactory, 443));
	    }
	    catch (Exception ex)
	    {
	        ex.printStackTrace();
	        throw new RuntimeException(new CoupiesServiceException("Can not create http-client"));
	    }

	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params,schReg);
	    
	    return new DefaultHttpClient(conMgr, params);
	}
	
	protected InputStream execute(DefaultHttpClient client, 
			HttpUriRequest request, URI uri) throws HttpStatusException {
		for (Map.Entry<String, String> entry : getHeaders().entrySet()) {
			request.setHeader(entry.getKey(), entry.getValue());
		}
		HttpResponse res;
		try {
			res = client.execute(request);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(new CoupiesServiceException("No connection."));	
		}
		if (getCheckHttpStatus()) {
			checkHttpStatus(res.getStatusLine(), uri);
		}
		try {
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(res.getEntity());
			return bufHttpEntity.getContent();
		} catch (IllegalStateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void checkHttpStatus(StatusLine statusLine, URI uri)
			throws HttpStatusException {
//		UnauthorizedException.throwExeption(statusLine, uri);
		HttpStatusException.throwExeption(statusLine, uri);
	}

	private void addCredentials(String url, DefaultHttpClient client) {
		// Authentifizierung
		if (credentials != null) {
			URL urlObj;
			try {
				urlObj = new URL(url);
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
			client.getCredentialsProvider().setCredentials(
					new AuthScope(urlObj.getHost(), urlObj.getPort()),
					new UsernamePasswordCredentials(credentials.getLogin(),
							credentials.getPassword()));
		}
	}
	
	private UrlEncodedFormEntity craeteParameterEntity(
			List<NameValuePair> nameValuePairs) {
		try {
			return new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private URI createURI(String tmpUrl) {
		try {
			return new URI(tmpUrl);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
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

	public boolean getCheckHttpStatus() {
		return checkHttpStatus;
	}

	public void setCheckHttpStatus(boolean checkHttpStatus) {
		this.checkHttpStatus = checkHttpStatus;
	}

	public Map<String, String> getParameters() {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		return parameters;
	}

	public Map<String, String> getHeaders() {
		if (headers == null) {
			headers = new HashMap<String, String>();
		}
		return headers;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public InputStream get(String url) throws HttpStatusException {
		DefaultHttpClient client = createHttpsClient();
		String tmpUrl = url;
		for (Entry<String, String> entry : getParameters().entrySet()) {
			String value = entry.getValue();
			if (value != null) {
				tmpUrl = URLUtils.addParameter(tmpUrl, entry.getKey(), value);
			}
		}
		
		URI uri = createURI(tmpUrl);
		HttpGet get = new HttpGet(uri);
		addCredentials(url, client);
		return execute(client, get, uri);
	}

	public InputStream put(String url) throws HttpStatusException {
		DefaultHttpClient client = createHttpsClient();
		URI uri = createURI(url);

		List<NameValuePair> nameValuePairs = prepareParameters();
		addCredentials(url, client);
		HttpPut put = new HttpPut(uri);
		put.setEntity(craeteParameterEntity(nameValuePairs));
		return execute(client, put, uri);
	}

	public InputStream post(String url) throws HttpStatusException {
		DefaultHttpClient client = createHttpsClient();
		URI uri = createURI(url);

		List<NameValuePair> nameValuePairs = prepareParameters();
		addCredentials(url, client);
		HttpPost post = new HttpPost(uri);
		post.setEntity(craeteParameterEntity(nameValuePairs));						
		return execute(client, post, uri);
	}

	public InputStream postWithProgress(String url, final List<File> image, final boolean withProgress) throws UnsupportedEncodingException, HttpStatusException {
		DefaultHttpClient client = createHttpsClient();
		URI uri = createURI(url);
		addCredentials(url, client);
		HttpPost post = new HttpPost(uri);
		
		int listSize = 0;
		for(File tempFile : image){
			listSize += (int)tempFile.length();
		}
		
		final int imageListSize = listSize; 
		
		// Create Multipart Entity for the POST
		ProgressMultipartEntity mpEntity = new ProgressMultipartEntity(new ProgressListener() {
			
			public void transferred(long num) {
				if(withProgress) {
					RedemptionActivity.notifyProgressBar((int)num, imageListSize);
				}
			}
		});
		
		for(int i=0; i<image.size(); i++){
			// Create the FileBodyPart for the image 	
			ContentBody fileBody = new FileBody(image.get(i), "image/jpg");
			if(i==0){
		        //Add the data to the multipart entity
		        mpEntity.addPart("receipt", fileBody);
			}else{
				//Add the data to the multipart entity
				int tempReceiptNumber = i+1;
		        mpEntity.addPart("receipt_"+tempReceiptNumber, fileBody);
			}
		}
        
        // Add all StringBodyParts like normal
        for (Entry<String, String> entry : getParameters().entrySet()) {
			String value = entry.getValue();
			if (value != null) {
				mpEntity.addPart(entry.getKey(), new StringBody(value, Charset.forName("UTF-8")));
			}
		}
		
		post.setEntity(mpEntity);
		return execute(client, post, uri);
	}

	public void setParameter(String key, Object val) {
		String value;
		if(val == null) {
			getParameters().remove(key);
		}
		else {
			if (val instanceof Double) {
				value = URLUtils.convertFromDouble((Double) val);
			} else if (val instanceof Float) {
				value = URLUtils.convertFromFloat((Float) val);
			} else {
				value = String.valueOf(val);
			}
			getParameters().put(key, value);
		}
	}

	public void setCredentials(String login, String password) {
		credentials = new Credentials(login, password);
	}

	public void setHeader(String name, String value) {
		getHeaders().put(name, value);
	}
}
