package de.coupies.framework.services.async;

import java.util.Locale;

import android.content.Context;
import de.coupies.framework.beans.User;
import de.coupies.framework.beans.User.Gender;
import de.coupies.framework.http.HttpClientFactory;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask.AsyncBooleanLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask.AsyncCoupiesSessionLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask.AsyncIntegerLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncAuthenticationTask.AsyncUserLoadingListener;
import de.coupies.framework.services.async.tasks.AsyncHtmlLoadingTask.AsyncHtmlLoadingListener;


/**
 * @author lars.eimermacher@coupies.de
 *
 */
public class AsyncAuthentificationService extends AbstractAsyncServices {

	public AsyncAuthentificationService(HttpClientFactory httpClientFactory, Context context) {
		super(httpClientFactory, context);
	}

	public void registerNewUser_async(AsyncCoupiesSessionLoadingListener listener, String email, int age,
			Gender gender, Locale locale, String referrer, String carrier) {
		setEmail(email);
		setAge(age);
		setGender(gender);
		setLocale(locale);
		setReferrer(referrer);
		setCarrier(carrier);
		
		runAsyncAuthentication(listener, "user/new");
	}
	
	public void registerNewUser_async_html(AsyncHtmlLoadingListener listener, String email, int age,
			Gender gender, Locale locale, String referrer, String carrier) {
		setEmail(email);
		setAge(age);
		setGender(gender);
		setLocale(locale);
		setReferrer(referrer);
		setCarrier(carrier);
		
		runAsyncLoadingHtml(listener, "user/new");
	}

	public void login_async(AsyncCoupiesSessionLoadingListener listener, String email, String password) {
		setEmail(email);
		setPassword(password);
		
		runAsyncAuthentication(listener, "user/login");
	}
	
	public void login_async_html(AsyncHtmlLoadingListener listener, String email, String password) {
		setEmail(email);
		setPassword(password);
		
		runAsyncLoadingHtml(listener, "user/login");
	}

	public void loginByFacebookAccessToken_async(AsyncCoupiesSessionLoadingListener listener, String fb_access_token,
			int loginExistingUser) {
		setFb_access_token(fb_access_token);
		setLoginExistingUser(loginExistingUser);
		
		runAsyncAuthentication(listener, "user/loginWithFacebook");
		
	}

	public void restorePassword_async(AsyncBooleanLoadingListener listener, String email) {
		setEmail(email);
		
		runAsyncAuthentication(listener, "user/restorePassword");
	}

	public void updateUser_async(AsyncCoupiesSessionLoadingListener listener, User user) {
		setAge(user.getAge());
		setCarrier(user.getCarrier());
		setCulture(user.getCulture());
		setEmail(user.getEmail());
		setGender(user.getGender());
		setReferrer(user.getReferrer());
		setLocale(user.getLocale());
		setPush_intensity(user.getPushIntensity());
		setPushNotificationsToken(user.getPushNotificationsToken());
		
		runAsyncAuthentication(listener, "user/update");
	}

	public void loginForMDHL_async(AsyncCoupiesSessionLoadingListener listener, int aboNr) {
		setAboNr(aboNr);
		
		runAsyncAuthentication(listener, "mdhl/user/login");
	}

	public void getUserProfile_async(AsyncUserLoadingListener listener) {
		runAsyncAuthentication(listener, "user/me");
	}

	public void getUserProfile_async_html(AsyncHtmlLoadingListener listener) {
		runAsyncLoadingHtml(listener, "user/me");
	}

	public void getUserNotificationIntensity_async(AsyncIntegerLoadingListener listener) {
		runAsyncAuthentication(listener, "user/profile");
	}
}
