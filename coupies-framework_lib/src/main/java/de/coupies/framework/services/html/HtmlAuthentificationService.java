package de.coupies.framework.services.html;

import java.util.Locale;

import de.coupies.framework.CoupiesServiceException;
import de.coupies.framework.beans.User;
import de.coupies.framework.beans.User.Gender;
import de.coupies.framework.session.Coordinate;

public interface HtmlAuthentificationService {
	
	String registerNewUser_html(Coordinate currentLocation, String email,
			int age, Gender gender, Locale locale,
			int applicationId, String referrer, String carrier) throws CoupiesServiceException;

	String login_html(String email, String password) throws CoupiesServiceException;

	String loginByFacebookId_html(Coordinate currentLocation, String facebookId,
			String email, User.Gender gender, String firstname, String lastname, String username, String birthday, String locale,
			int applicationId, String referrer, String carrier, String fb_access_token, int loginExistingUser) throws CoupiesServiceException;
	
	 String signUp_html(String email, int age,
				User.Gender gender, Locale locale,String referrer, String carrier,String password,  
				String pushNotificationsToken)  throws CoupiesServiceException;
}
