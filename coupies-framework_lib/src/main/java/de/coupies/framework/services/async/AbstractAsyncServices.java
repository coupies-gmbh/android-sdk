package de.coupies.framework.services.async;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import de.coupies.coupies_framework_lib.R;
import de.coupies.framework.CoupiesApplicationId;
import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.Offer;
import de.coupies.framework.beans.User;
import de.coupies.framework.beans.User.Gender;
import de.coupies.framework.beans.Location;
import de.coupies.framework.http.HttpClient;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.AbstractCoupiesService;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask.AsyncBooleanLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask.AsyncCoupiesSessionLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask.AsyncIntegerLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask.AsyncUserLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncBookmarkTask;
import de.coupies.framework.services.async.tasks.AsyncBookmarkTask.AsyncBookmarkLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncHtmlLoadingTask;
import de.coupies.framework.services.async.tasks.AsyncLikeTask;
import de.coupies.framework.services.async.tasks.AsyncLikeTask.AsyncLikeListener;
import de.coupies.framework.services.async.tasks.AsyncLocationLoadingTask;
import de.coupies.framework.services.async.tasks.AsyncLocationLoadingTask.AsyncLocationListLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncLocationLoadingTask.AsyncLocationLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncOfferLoadingTask;
import de.coupies.framework.services.async.tasks.AsyncHtmlLoadingTask.AsyncHtmlLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncOfferLoadingTask.AsyncOfferListLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncOfferLoadingTask.AsyncOfferLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncPayoutTask;
import de.coupies.framework.services.async.tasks.AsyncPayoutTask.AsyncPayoutListener;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.services.content.DocumentProcessor.DocumentHandler;
import de.coupies.framework.services.content.DocumentProcessor.Handler;
import de.coupies.framework.services.content.handler.CouponHandler;
import de.coupies.framework.services.content.handler.CouponListHandler;
import de.coupies.framework.services.content.handler.LocationHandler;
import de.coupies.framework.services.content.handler.LocationListHandler;
import de.coupies.framework.services.content.handler.NotificationIntensityHandler;
import de.coupies.framework.services.content.handler.UserDataHandler;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.session.UserSession;
import de.coupies.framework.session.UserSessionImpl;
import de.coupies.framework.utils.CustomDialog;

public class AbstractAsyncServices extends AbstractCoupiesService{
	protected String 	email,
						referrer,
						carrier,
						password,
						pushNotificationsToken,
						fb_access_token,
						culture,
						paypal,
						token,
						name;
	
	protected Gender gender;
	protected Locale locale;
	protected CoupiesSession session;
	protected Context context;
	protected int 	radius,
					limit,
					includeRead,
					age,
					loginExistingUser,
					push_intensity,
					aboNr,
					couponId,
					paymentTypeId;
	
	protected Long 	kontonummer,
					bankleitzahl;
	
	protected boolean 	bookmark = false,
						like = false;
	
	private Coordinate coordinate;
	protected Dialog dialog;
	
	public void setRadius(int radius){
		this.radius = radius;
	}
	
	public int getRadius(){
		return radius;
	}
	
	public void setLimit(int limit){
		this.limit = limit;
	}
	
	public int getLimit(){
		return limit;
	}
	
	public void setCoordinate(Coordinate coordinate){
		this.coordinate = coordinate;
	}
	
	public Coordinate getCoordinate(){
		return coordinate;
	}
	
	public void setDialog(Dialog dialog){
		this.dialog = dialog;
	}
	
	public Dialog getDialog(){
		return dialog;
	}
	
	public void setIncludeRead(int includeRead){
		this.includeRead = includeRead;
	}
	
	public int getIncludeRead(){
		return includeRead;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPushNotificationsToken() {
		return pushNotificationsToken;
	}

	public void setPushNotificationsToken(String pushNotificationsToken) {
		this.pushNotificationsToken = pushNotificationsToken;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public CoupiesSession getSession() {
		return session;
	}

	public void setSession(CoupiesSession session) {
		this.session = session;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public String getFb_access_token() {
		return fb_access_token;
	}

	public void setFb_access_token(String fb_access_token) {
		this.fb_access_token = fb_access_token;
	}

	public int getLoginExistingUser() {
		return loginExistingUser;
	}

	public void setLoginExistingUser(int loginExistingUser) {
		this.loginExistingUser = loginExistingUser;
	}

	public String getCulture() {
		return culture;
	}

	public void setCulture(String culture) {
		this.culture = culture;
	}

	public int getPush_intensity() {
		return push_intensity;
	}

	public void setPush_intensity(int push_intensity) {
		this.push_intensity = push_intensity;
	}

	public int getAboNr() {
		return aboNr;
	}

	public void setAboNr(int aboNr) {
		this.aboNr = aboNr;
	}

	public int getCouponId() {
		return couponId;
	}

	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}

	public boolean isBookmark() {
		return bookmark;
	}

	public void setBookmark(boolean bookmark) {
		this.bookmark = bookmark;
	}

	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}

	public String getPaypal() {
		return paypal;
	}

	public void setPaypal(String paypal) {
		this.paypal = paypal;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(int paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public Long getKontonummer() {
		return kontonummer;
	}

	public void setKontonummer(Long kontonummer) {
		this.kontonummer = kontonummer;
	}

	public Long getBankleitzahl() {
		return bankleitzahl;
	}

	public void setBankleitzahl(Long bankleitzahl) {
		this.bankleitzahl = bankleitzahl;
	}

	public AbstractAsyncServices(HttpClientFactory httpClientFactory,
			Context context) {
		super(httpClientFactory, context);
		
	}
	
	@Override
	protected Object consumeService(InputStream inStream, Handler handler) throws CoupiesServiceException {
		return handler;
	}
	
	public void runAsyncLoadingOffer(AsyncOfferLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncOfferLoadingTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					CouponHandler handler = new CouponHandler();
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					addCoordinate(coordinate, httpClient);
					httpClient = includeHttpParameter(httpClient);
					offerResult = (Offer) consumeService(httpClient.get(url), handler);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncLoadingOfferList(AsyncOfferListLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncOfferLoadingTask(dialog, true, listener){
			@SuppressWarnings("unchecked")
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					CouponListHandler handler = new CouponListHandler();
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					addCoordinate(coordinate, httpClient);
					httpClient = includeHttpParameter(httpClient);
					listResult = (List<Offer>) consumeService(httpClient.get(url), handler);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncLoadingLocation(AsyncLocationLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncLocationLoadingTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					LocationHandler handler = new LocationHandler();
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					addCoordinate(coordinate, httpClient);
					httpClient = includeHttpParameter(httpClient);
					locationResult = (Location) consumeService(httpClient.get(url), handler);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncLoadingLocationList(AsyncLocationListLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncLocationLoadingTask(dialog, true, listener){
			@SuppressWarnings("unchecked")
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					LocationListHandler handler = new LocationListHandler();
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					addCoordinate(coordinate, httpClient);
					httpClient = includeHttpParameter(httpClient);
					listResult = (List<Location>) consumeService(httpClient.get(url), handler);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncAuthentication(AsyncCoupiesSessionLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncAuthenticationTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					DocumentHandler handler = new UserDataHandler();
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					httpClient = includeHttpParameter(httpClient);
					sessionResult = createSession((User) consumeService(httpClient.post(url), handler));
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncAuthentication(AsyncUserLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncAuthenticationTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					DocumentHandler handler = new UserDataHandler();
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					httpClient = includeHttpParameter(httpClient);
					userResult = (User)consumeService(httpClient.get(url), handler);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncAuthentication(AsyncIntegerLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncAuthenticationTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					DocumentHandler handler = new NotificationIntensityHandler();
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					httpClient = includeHttpParameter(httpClient);
					integerResult = (Integer)consumeService(httpClient.get(url), handler);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncAuthentication(AsyncBooleanLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncAuthenticationTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
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
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					httpClient = includeHttpParameter(httpClient);
					booleanResult = (Boolean)consumeService(httpClient.post(url), handler);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	protected void runAsyncLoadingHtml(AsyncHtmlLoadingListener listener,final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncHtmlLoadingTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					String url = getAPIUrl(apiPath, HTML_RESULT_HANDLER);
					HttpClient httpClient = createHttpClient(session);
					httpClient = includeHttpParameter(httpClient);
					htmlResult = (String) consumeService(httpClient.get(url), HTML_RESULT_HANDLER);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncLoadingBookmarkOfferList(AsyncBookmarkLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncBookmarkTask(dialog, true, listener){
			@SuppressWarnings("unchecked")
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					CouponListHandler handler = new CouponListHandler();
					String url = getAPIUrl(apiPath, handler);
					HttpClient httpClient = createHttpClient(session);
					addCoordinate(coordinate, httpClient);
					httpClient = includeHttpParameter(httpClient);
					bookmarkResult = (List<Offer>) consumeService(httpClient.get(url), handler);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncBookmark(AsyncBookmarkLoadingListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncBookmarkTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					String url = getAPIUrl(apiPath, NO_RESULT_HANDLER);
					HttpClient httpClient = createHttpClient(session);
					addCoordinate(coordinate, httpClient);
					httpClient = includeHttpParameter(httpClient);
					consumeService(httpClient.get(url), NO_RESULT_HANDLER);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncLike(AsyncLikeListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncLikeTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					String url = getAPIUrl(apiPath, NO_RESULT_HANDLER);
					HttpClient httpClient = createHttpClient(session);
					addCoordinate(coordinate, httpClient);
					httpClient = includeHttpParameter(httpClient);
					consumeService(httpClient.post(url), NO_RESULT_HANDLER);
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	public void runAsyncPayout(AsyncPayoutListener listener, final String apiPath){
		if(dialog == null)
			createProgressDialog();
		new AsyncPayoutTask(dialog, true, listener){
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					String url = getAPIUrl(apiPath, NO_RESULT_HANDLER);
					HttpClient httpClient = createHttpClient(session);
					addCoordinate(coordinate, httpClient);
					httpClient = includeHttpParameter(httpClient);
					consumeService(httpClient.post(url), NO_RESULT_HANDLER);
				}catch (CoupiesServiceException e) {
					if(e.getMessage().endsWith("Unexpected end of document")){
						// Bei erfolgreichem erstellen des DB eintrags (erstes mal) kommt
						// ein leeres Dokument zurück, ist also richtig!
					}else{
						e.printStackTrace();
						Log.e(getClass().toString(), e.getLocalizedMessage());
						error = e;
						return false;
					}
				}catch(Exception e){
					e.printStackTrace();
					Log.e(getClass().toString(), e.getLocalizedMessage());
					error = e;
					return false;
				}
				
				return true;
			}
		}.execute();
	}
	
	private HttpClient includeHttpParameter(HttpClient client){
		addCoordinate(coordinate, client);
		if(limit > 0)
			addLimit(limit, client);
		if(includeRead > 0)
			client.setParameter("include_read", radius);
		if(radius > 0)
			client.setParameter("radius", radius);
		if(email != null)
			client.setParameter("email", email);
		if(age > 0)
			client.setParameter("age", age);
		if(gender != null && gender.getId()>0)
			client.setParameter("gender_id", gender.getId());
		if (carrier != null)
			client.setParameter("carrier", carrier);
		if(password != null){
			client.setParameter("password",password);
			client.setParameter("password_conf",password);
		}
		if(locale != null)
			client.setParameter("culture", locale);
		if (referrer != null) 
			client.setParameter("created_referrer", referrer);
		if(pushNotificationsToken != null)
			client.setParameter("c2dm_id", pushNotificationsToken);
		if(loginExistingUser > 0)
			client.setParameter("login_existing_user", loginExistingUser);
		if(fb_access_token != null)
			client.setParameter("fb_access_token", fb_access_token);
		if(aboNr>0)
			client.setParameter("subscription_number", aboNr);
		if(couponId>0)
			client.setParameter("couponid", couponId);
		if(paymentTypeId>0)
			client.setParameter("paymenttype_id", paymentTypeId);
		if(paypal != null)
			client.setParameter("paypalaccount", paypal);
		if(token != null)
			client.setParameter("security_token", token);
		if(kontonummer != null && kontonummer > 0)
			client.setParameter("bankaccount_accountnumber", String.valueOf(kontonummer));
		if(bankleitzahl != null && bankleitzahl > 0)
			client.setParameter("bankaccount_bankcode", bankleitzahl.longValue());
		if(name != null)
			client.setParameter("bankaccount_name", name);
		
		client.setParameter("like", like);
		client.setParameter("bookmark", bookmark);
		client.setParameter("created_application_id", CoupiesApplicationId.ANDROID);
		
		return client;
	}
	
	protected void createProgressDialog(){
		dialog = new CustomDialog(context, R.style.CustomDialog);
		dialog.setContentView(R.layout.coupies_custom_progress_dialog);
		((CustomDialog)dialog).setNewActivity(context);
	}
	
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
}
