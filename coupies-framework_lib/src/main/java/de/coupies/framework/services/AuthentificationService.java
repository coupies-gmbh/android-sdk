package de.coupies.framework.services;

import java.util.Locale;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.User;
import de.coupies.framework.beans.User.Gender;
import de.coupies.framework.http.HttpClient.HttpStatusException;
import de.coupies.framework.services.content.DocumentParseException;
import de.coupies.framework.services.html.HtmlAuthentificationService;
import de.coupies.framework.session.Coordinate;
import de.coupies.framework.session.CoupiesSession;
import de.coupies.framework.session.PartnerSession;
import de.coupies.framework.session.UserSession;

public interface AuthentificationService extends HtmlAuthentificationService {
	@SuppressWarnings("serial")
	public class AuthentificationException extends CoupiesServiceException {
		public AuthentificationException(String detailMessage) {
			super(detailMessage);
		}
		
	}

	/**
	 * register new user
	 * 
	 * @param coordinate current position
	 * @param email the users email
	 * @param age the users age
	 * @param gender the users gender
	 * @param locale culture eg. de_DE
	 * @param applicationId coupies applicationID @see de.coupies.framework.CoupiesApplicationId	
	 * @return coupies session 
	 * @throws CoupiesServiceException
	 */
	 UserSession registerNewUser(Coordinate coordinate, String email, int age,
			User.Gender gender, Locale locale,
			int applicationId) throws CoupiesServiceException;
	 
	 /**
		 * register new user
		 * 
		 * @param coordinate current position
		 * @param email the users email
		 * @param age the users age
		 * @param gender the users gender
		 * @param password of the user
		 * @param locale culture eg. de_DE
		 * @param referrer (optional) A referrer of the advertising media that triggered the download of the application. Can be passed by Google Play.
		 * @param carrier (optional) The home carrier of the user as string. This is composed as [name]_[CountryCode]_[NetworkCode], e.g. “Vodafone.de_262_02”.
		 * @return user session 
		 * @throws CoupiesServiceException
		 */
	 UserSession signUp(String email, int age,
				User.Gender gender, Locale locale,String referrer, String carrier,String password,  
				String pushNotificationsToken)  throws CoupiesServiceException;

	 /**
		 * register new user
		 * 
		 * @param (optional) coordinate current position
		 * @param email the users email
		 * @param age the users age
		 * @param gender the users gender
		 * @param locale (optional) culture eg. de_DE
		 * @param applicationId coupies applicationID @see de.coupies.framework.CoupiesApplicationId
		 * @param referrer (optional) A referrer of the advertising media that triggered the download of the application. Can be passed by Google Play.
		 * @param carrier (optional) The home carrier of the user as string. This is composed as [name]_[CountryCode]_[NetworkCode], e.g. “Vodafone.de_262_02”.
		 * @return coupies session 
		 * @throws CoupiesServiceException
		 */
	 UserSession registerNewUser(Coordinate currentLocation, String email,
				int age, Gender gender, Locale locale,
				int applicationId, String referrer, String carrier) throws CoupiesServiceException;
	 
	/**
	 * User Login
	 * 
	 * @param email Email
	 * @param password Passwort
	 * @return coupies session 
	 * @throws AuthentificationService.AuthentificationException the user is not registered or wrong password 
	 * @throws CoupiesServiceException general error 
	 */
	 UserSession login(String email, String password) throws CoupiesServiceException;

	/**
	 * User via Facebook anmelden
	 * 
	 * @param (optional) coordinate current position
	 * @param facebookId facebookId
	 * @param email email 
	 * @param gender gender
	 * @param locale from facebook json request
	 * @param applicationId coupies applicationID @see de.coupies.framework.CoupiesApplicationId
	 * @param referrer (optional) A referrer of the advertising media that triggered the download of the application. Can be passed by Google Play.
	 * @param carrier (optional) The home carrier of the user as string. This is composed as [name]_[CountryCode]_[NetworkCode], e.g. “Vodafone.de_262_02”.
	 * @param loginExistingUser, defines whether the it is login or signup (default = 1)
	 * @return Coupies Session Objekt mit den Daten den Benutzers
	 * @throws CoupiesServiceException
	 */
	 UserSession loginByFacebookAccessToken(Coordinate currentLocation, int applicationId, String referrer,
			 String carrier, String fb_access_token,int loginExistingUser) throws CoupiesServiceException;

	/**
	 * restore password
	 * 
	 * @param session coupies session
	 * @param email email 
	 * @param locale locale
	 * @return true if the email exists and password was restored
	 * @throws CoupiesServiceException
	 */
	 Object restorePassword(CoupiesSession session, String email,
			Locale locale) throws CoupiesServiceException;

	
	 /**
	  * 
	  * @param session
	  * @param user
	  * @param locale locale
	  * @throws CoupiesServiceException
	  */
	 UserSession updateUser(CoupiesSession session, User user, Locale locale) throws CoupiesServiceException;

	 /**
	  * 
	  * @param session
	  * @param locale
	  * @param pushNotificationsToken
	  * @param pushIntensity
	 * @return 
	  * @throws CoupiesServiceException
	  */				
	 Boolean updateUserProfile(CoupiesSession session, Locale locale, String pushNotificationsToken,
				Integer pushIntensity) throws CoupiesServiceException;
	 
	 /**
	  * 
	  * @param partnerToken
	  * @return
	  * @throws CoupiesServiceException
	  */
	 PartnerSession createPartnerSession(String partnerToken) throws CoupiesServiceException;

	 UserSession login(String email, String password, Locale locale) throws CoupiesServiceException;
	 
	 UserSession loginForMDHL(int aboNr, Coordinate coordinate) throws CoupiesServiceException;
	 
	 /**
	  * Method to get the User-Profile to show it to the User
	  * 
	  * @author larseimermacher
	  * @param coupiesSession
	  * @return Returns a User
	  * @throws DocumentParseException
	  * @throws HttpStatusException
	  */
	 User getUserProfile(CoupiesSession coupiesSession) throws CoupiesServiceException;
	 
	 String getUserProfile_html(CoupiesSession coupiesSession) throws CoupiesServiceException;

	 Integer getUserNotificationIntensity(CoupiesSession coupiesSession)  throws CoupiesServiceException;
}