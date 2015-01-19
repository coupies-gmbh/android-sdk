package de.coupies.framework.services;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.w3c.dom.Node;

import android.content.Context;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentProcessor;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.handler.BooleanResultHandler;
import de.coupies.framework.services.content.handler.HtmlResultHandler;
import de.coupies.framework.services.content.handler.NoResultHandler;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.session.CoupiesSession.Identification;
import de.coupies.framework.utils.DOMUtils;
import de.coupies.framework.utils.DeviceUtils;
import de.coupies.framework.utils.URLUtils;

/**
 * 
 * @author thomas.volk@denkwerk.com
 * @since 15.12.2010
 *
 */
public abstract class AbstractCoupiesService extends AbstractRestfulService {
	
	protected final static NoResultHandler NO_RESULT_HANDLER = new NoResultHandler();
	protected final static BooleanResultHandler BOOLEAN_RESULT_HANDLER = new BooleanResultHandler();
	protected final static HtmlResultHandler HTML_RESULT_HANDLER = new HtmlResultHandler();
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private final ValidationParser validationParser = new ValidationParser();
	private static String deviceId = null;
	private static String advertiserId = null;

	public AbstractCoupiesService(HttpClientFactory httpClientFactory,Context context) {
		super(httpClientFactory);
		if (deviceId == null) {
			deviceId = DeviceUtils.getDeviceId(context);
		}
	}
	
	public AbstractCoupiesService(final HttpClientFactory httpClientFactory,final Context context, String adId) {
		super(httpClientFactory);
		if (deviceId == null) {
			deviceId = DeviceUtils.getDeviceId(context);
		}
		if(advertiserId == null)
			advertiserId = adId;
	}

	protected ValidationParser getValidationParser() {
		return validationParser;
	}

	protected SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	/**
	 * Use this method for the login/registration calls, when we still don't have
	 * a CoupiesSession. After, use createHttpClient(CoupiesSession session).
	 * @return
	 */
	protected HttpClient createHttpClient() {
		HttpClient httpClient = getHttpClientFactory().createHttpClient();
		addDeviceId(httpClient);
		addAdvertiserId(httpClient);
		return httpClient;
	}
	
	protected HttpClient createHttpClient(CoupiesSession session) {
		HttpClient httpClient = createHttpClient();
		Identification ident = session.getIdentification();
		
		httpClient.setParameter(ident.getParameterName(), ident.getValue());
		httpClient.setParameter("culture", session.getLocale().toString());

		return httpClient;
	}

	protected String prepareUrl(CoupiesSession session, String url) throws CoupiesServiceException {		

		String apiKey = getHttpClientFactory().getConnection().getApiKey();
		String apiLevel = getHttpClientFactory().getConnection().getApiLevel();
		Identification ident = session.getIdentification();
		
		url = URLUtils.addParameter(url, "api_level", apiLevel);
		url = URLUtils.addParameter(url, "key", apiKey);
		url = URLUtils.addParameter(url, ident.getParameterName(), 
				ident.getValue());
		url = URLUtils.addParameter(url, "culture", session.getLocale().toString());	
		return url;
	}


	protected String getAPIUrl(String path, Handler handler) {
		String suffix = "";
		if(handler instanceof DocumentHandler) {
			suffix = ".xml";
		}
		else if(handler instanceof HtmlResultHandler) {
			suffix = ".html";
		}
		String url = String.format("%s/%s%s", 
				getHttpClientFactory().getConnection().getAPIBaseUrl(), path, suffix);
		return url;
	}

	protected Object consumeService(InputStream inStream, 
			Handler handler) throws CoupiesServiceException {
		return new DocumentProcessor().execute(inStream, handler);
	}
		
	protected String getNodeValue(Node subNode) {
		return DOMUtils.getNodeContent(subNode);
	}
	
	protected void addLimit(Integer limit, HttpClient client) {
		if(limit != null && limit > 0) {
			client.setParameter("limit", limit);
		}
	}
	
	protected void addCoordinate(Coordinate coordinate, HttpClient httpClient) {
		if(coordinate != null) {
			httpClient.setParameter("latitude", coordinate.getLatitude());
			httpClient.setParameter("longitude", coordinate.getLongitude());
		}
	}
	
	protected void addDeviceId(HttpClient httpClient) {
		if(deviceId != null) {
			httpClient.setParameter("phone_token", deviceId);
		}
	}
	
	protected void addAdvertiserId(HttpClient httpClient){
		if(advertiserId != null) {
			httpClient.setParameter("android_aid", advertiserId);
		}
	}
}
