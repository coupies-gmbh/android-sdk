/**
 * 
 */
package de.coupies.framework.services;

import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import de.coupies.framework.CoupiesApplicationId;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.User;
import de.coupies.framework.beans.User.Gender;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.services.content.ValidationParser;
import de.coupies.framework.services.content.handler.UserDataHandler;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.session.PartnerSession;
import de.coupies.framework.session.PartnerSessionImpl;
import de.coupies.framework.session.UserSession;
import de.coupies.framework.session.UserSessionImpl;


public class AuthentificationServiceImpl extends AbstractCoupiesService 
	implements AuthentificationService {
	
	public AuthentificationServiceImpl(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}
	
	public AuthentificationServiceImpl(HttpClientFactory httpClientFactory, Context context, String adId) {
		super(httpClientFactory, context, adId);
	}

	/**
	 * RemenberKey in Session abspeichern
	 * @param user User
	 * @return Coupies Session object
	 * @throws DocumentParseException when RemenberKey is missing
	 */
	private UserSession createSession(Locale locale, User user) throws CoupiesServiceException {
		String rememberKey = user.getRememberKey();
		if(rememberKey == null) {
			throw new DocumentParseException("no rememberkey found for user: " + user);
		}
		UserSessionImpl session = new UserSessionImpl(locale, user);
		return session;
	}

	private UserSession createSession(User user) throws CoupiesServiceException {
		return createSession(null, user);
	}

	private Object signUp(Handler handler, String email, int age,
			User.Gender gender,Locale locale,String referrer,String carrier,String password,String pushNotificationsToken)
					throws CoupiesServiceException {
		String url = getAPIUrl("user/new", handler);
		HttpClient httpClient = createHttpClient();
		httpClient.setParameter("email", email);
		httpClient.setParameter("age", age);	
		httpClient.setParameter("gender_id", gender.getId());
		if (carrier != null) {
			httpClient.setParameter("carrier", carrier);
		}
		httpClient.setParameter("password",password);
		httpClient.setParameter("culture", locale);
		httpClient.setParameter("password_conf",password);
		httpClient.setParameter("created_application_id", CoupiesApplicationId.ANDROID);
		if (referrer != null) {
			httpClient.setParameter("created_referrer", referrer);
		}	
		httpClient.setParameter("c2dm_id", pushNotificationsToken);
		Object result = consumeService(httpClient.post(url), handler);
		return result;
	}
	
	public UserSession signUp(String email, int age,
			User.Gender gender, Locale locale, String referrer, String carrier, String password,String pushNotificationsToken) throws CoupiesServiceException{
		DocumentHandler handler = new UserDataHandler();
		Object result = signUp(handler, email, 
				age, gender, locale, referrer,carrier,password,pushNotificationsToken);
		User user = (User) result;
		return createSession(locale, user);
	}
	

	private Object loginByFacebookAccessToken(Handler handler, Coordinate coordinate, int applicationId,
			String referrer, String carrier, String fb_access_token, int loginExistingUser)
			throws CoupiesServiceException {
		String url = getAPIUrl("user/loginWithFacebook", handler);
		HttpClient client = createHttpClient();
		
		client.setParameter("login_existing_user", loginExistingUser);
		client.setParameter("fb_access_token", fb_access_token);
		if (coordinate != null) {
			client.setParameter("latitude", coordinate.getLatitude());
			client.setParameter("longitude", coordinate.getLongitude());
		}
		if (referrer != null) {
			client.setParameter("created_referrer", referrer);
		}
		if (carrier != null) {
			client.setParameter("carrier", carrier);
		}
		client.setParameter("created_application_id", applicationId);
		Object result = consumeService(client.post(url), handler);
		return result;
	}
	

	private Object login(Handler handler, String email, String password)
			throws CoupiesServiceException {
		String url = getAPIUrl("user/login", handler);
		HttpClient client = createHttpClient();
		client.setParameter("email", email);
		client.setParameter("password", password);
		Object result = consumeService(client.post(url), handler);
		return result;
	}
	
	private Object loginForMDHL(Handler handler, int aboNr, Coordinate coordinate) 
			throws CoupiesServiceException{
		String url = getAPIUrl("mdhl/user/login", handler);
		HttpClient client = createHttpClient();
		if (coordinate != null) {
			client.setParameter("latitude", coordinate.getLatitude());
			client.setParameter("longitude", coordinate.getLongitude());
		}
		client.setParameter("subscription_number", aboNr);
		Object result = consumeService(client.post(url), handler);
		return result;
	}
	
	public UserSession loginForMDHL(int aboNr, Coordinate coordinate) 
			throws CoupiesServiceException{
			DocumentHandler handler = new UserDataHandler();
			Object result = loginForMDHL(handler, aboNr, coordinate);
			User user = (User) result;
			return createSession(Locale.getDefault(), user);
		}

	private Object registerNewUser(Handler handler,	Coordinate coordinate, String email, int age,
			User.Gender gender, Locale locale, int applicationId, String referrer, String carrier)
					throws CoupiesServiceException {
		//solange noch der htc bug im umlauf ist zu locale
		//String url = getAPIUrl(String.format("user/new"));
		String url = getAPIUrl("user/new", handler);
		HttpClient httpClient = createHttpClient();
		httpClient.setParameter("email", email);
		httpClient.setParameter("age", age);
		try {
			httpClient.setParameter("culture", locale.toString());
		} catch (Exception e) {
			httpClient.setParameter("culture", "de_DE");
		}		
		httpClient.setParameter("gender_id", gender.getId());
		httpClient.setParameter("created_application_id", applicationId);
		httpClient.setParameter("latitude", coordinate.getLatitude());
		httpClient.setParameter("longitude", coordinate.getLongitude());	
		if (referrer != null) {
			httpClient.setParameter("created_referrer", referrer);
		}
		if (carrier != null) {
			httpClient.setParameter("carrier", carrier);
		}
		
		Object result = consumeService(httpClient.post(url), handler);
		return result;
	}
	
	

	public UserSession registerNewUser(Coordinate coordinate, String email, int age,
			User.Gender gender, Locale locale, int applicationId) 
				throws CoupiesServiceException {
		DocumentHandler handler = new UserDataHandler();
		Object result = registerNewUser(handler, coordinate, email, 
				age, gender, locale, applicationId, null, null);
		User user = (User) result;
		return createSession(locale, user);
	}
	
	public UserSession registerNewUser(Coordinate coordinate, String email, int age,
			Gender gender, Locale locale, int applicationId, String referrer, String carrier)
					throws CoupiesServiceException {
		DocumentHandler handler = new UserDataHandler();
		Object result = registerNewUser(handler, coordinate, email, 
				age, gender, locale, applicationId, referrer, carrier);
		User user = (User) result;
		return createSession(locale, user);
	}
	
	public UserSession login(String email, String password) throws CoupiesServiceException {
		return login(email, password, Locale.getDefault());
	}


	public UserSession login(String email, String password, Locale locale) 
		throws CoupiesServiceException{
		DocumentHandler handler = new UserDataHandler();
		Object result = login(handler, email, password);
		User user = (User) result;
		return createSession(locale, user);
	}

	public UserSession loginByFacebookAccessToken(Coordinate coordinate, int applicationId, String referrer,
			 String carrier, String fb_access_token,int loginExistingUser) throws CoupiesServiceException {
		Object result = loginByFacebookAccessToken(new UserDataHandler(), coordinate, applicationId, referrer, carrier, fb_access_token,loginExistingUser);
		return createSession((User) result);
	}
	
	private Object restorePassword(Handler handler, CoupiesSession session, String email, 
			Locale locale) throws CoupiesServiceException {
		String url = getAPIUrl("user/restorePassword", handler);
		HttpClient client = createHttpClient(session);
		client.setParameter("email", email);
		client.setParameter("culture", locale.toString());
		return consumeService(client.post(url), handler);
	}
	
	public Object restorePassword(CoupiesSession session, String email, 
			Locale locale) throws CoupiesServiceException {
		Handler handler = new DocumentHandler() {
			public Object handleDocument(Document doc)
					throws DocumentParseException {
				Element rspNode = (Element) doc.getElementsByTagName("rsp").item(0);
				if (rspNode == null) {
					throw new DocumentParseException("Invalid response.");
				}
				else if (rspNode.getAttribute("stat").equals("ok")) {
					return Boolean.valueOf(true);
				}
				else {
					return Boolean.valueOf(false);
				}
			}
		};
		return restorePassword(handler, session, email, locale);
	}

	public UserSession updateUser(CoupiesSession session, User user, Locale locale)
			throws CoupiesServiceException {
		DocumentHandler handler = new UserDataHandler();
		String url = getAPIUrl("user/update", handler);
		HttpClient client = createHttpClient(session);
		client.setParameter("email", user.getEmail());
		if (locale != null) {
			try {
				user.setCulture(locale.toString());
			} catch (Exception e) {
				user.setCulture("de_DE");
			}	
		}
		client.setParameter("culture", user.getCulture());
		client.setParameter("age", user.getAge());
		client.setParameter("gender_id", user.getGenderId());
		client.setParameter("created_referrer", user.getReferrer());
		client.setParameter("carrier", user.getCarrier());
		client.setParameter("c2dm_id", user.getPushNotificationsToken());
		client.setParameter("push_intensity", user.getPushIntensity());
		client.setParameter("created_application_id", "2");
		
		user = (User) consumeService(client.post(url), handler);
		return createSession(locale, user);
	}

	/**
	 * @deprecated Use updateUser instead
	 */
	public Boolean updateUserProfile(CoupiesSession session, Locale locale, String pushNotificationsToken,
			Integer pushIntensity) throws CoupiesServiceException {
		String url = getAPIUrl("user/profile", BOOLEAN_RESULT_HANDLER);
		HttpClient client = createHttpClient(session);
		if (locale != null) {
			client.setParameter("culture", locale.toString());
		}
		client.setParameter("c2dm_id", pushNotificationsToken);
		client.setParameter("push_intensity", pushIntensity);
		return (Boolean)consumeService(client.post(url), BOOLEAN_RESULT_HANDLER);
	}
	
	public String registerNewUser_html(Coordinate coordinate, String email, int age,
			User.Gender gender, Locale locale, int applicationId, String referrer, String carrier)
					throws CoupiesServiceException {
		return (String) registerNewUser(HTML_RESULT_HANDLER, coordinate, email, 
				age, gender, locale, applicationId, referrer, carrier);
	}

	public String login_html(String email, String password)
			throws CoupiesServiceException {
		return (String) login(HTML_RESULT_HANDLER, email, password);
	}

	public String loginByFacebookId_html(Coordinate coordinate, String facebookId, String email,
			User.Gender gender, String firstname, String lastname, String username, String birthday, String locale,
			int applicationId, String referrer, String carrier, String fb_access_token, int loginExistingUser)
					throws CoupiesServiceException {
		return (String) loginByFacebookAccessToken(HTML_RESULT_HANDLER, coordinate,
				applicationId, referrer, carrier, fb_access_token,loginExistingUser);
	}
	
	public String signUp_html(String email, int age,
			User.Gender gender, Locale locale,String referrer, String carrier,String password,  
			String pushNotificationsToken) throws CoupiesServiceException{
		return (String) signUp(HTML_RESULT_HANDLER, email, age, gender,locale,referrer,carrier, password, pushNotificationsToken);
	}

	public PartnerSession createPartnerSession(String partnerToken)
			throws CoupiesServiceException {
		return new PartnerSessionImpl(partnerToken);
	}

	public PartnerSession createPartnerSession(Locale locale, String partnerToken)
			throws CoupiesServiceException {
		return new PartnerSessionImpl(locale, partnerToken);
	}
	
	public User getUserProfile(CoupiesSession coupiesSession) throws CoupiesServiceException{
		DocumentHandler handler = new UserDataHandler();
		String url = getAPIUrl("user/me", handler);
		HttpClient client = createHttpClient(coupiesSession);
		return (User)consumeService(client.get(url), handler);
	}
	
	public String getUserProfile_html(CoupiesSession coupiesSession) throws CoupiesServiceException{
		String url = getAPIUrl("user/me", HTML_RESULT_HANDLER);
		HttpClient client = createHttpClient(coupiesSession);
		return (String)consumeService(client.get(url), HTML_RESULT_HANDLER);
	}

	public Integer getUserNotificationIntensity(CoupiesSession coupiesSession) throws CoupiesServiceException{
		DocumentHandler handler = new NotificationIntensityHandler();
		String url = getAPIUrl("user/profile", handler);
		HttpClient client = createHttpClient(coupiesSession);
		return (Integer)consumeService(client.post(url), handler);
	}
	private class NotificationIntensityHandler implements DocumentHandler {
		public Object handleDocument(Document doc) throws CoupiesServiceException {
			NodeList errorNode = doc.getElementsByTagName("error");
			if(errorNode != null && errorNode.item(0)!=null){
				errorNode = errorNode.item(0).getChildNodes();
				if(errorNode != null){
					ValidationParser mValidationParser = new ValidationParser();
					mValidationParser.parseAndThrow(doc);
				}
			}
			
			Element rspNode = (Element)doc.getElementsByTagName("rsp").item(0);
			if (rspNode == null) {
				throw new DocumentParseException("Invalid response.");
			}
			Node item = doc.getElementsByTagName("push_intensity").item(0);
			if (item == null || item.getNodeType() != Node.ELEMENT_NODE) {
				throw new DocumentParseException("Invalid response.");
			}
			return Integer.valueOf(item.getFirstChild().getNodeValue());
		}
	}
}
