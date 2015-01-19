package de.coupies.framework.session;

import java.util.Locale;

import de.coupies.framework.beans.User;


public class UserSessionImpl extends AbstractSession implements UserSession {
	private User user;

	public UserSessionImpl() {
		this(null, null);
	}
	
	public UserSessionImpl(User user) {
		this(user.getLocale(), user);
	}
	
	public UserSessionImpl(Locale locale, User user) {
		super(locale);
		this.user = user;	
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Identification getIdentification() {
		return new Identification("remember_key", getRememberKey());
	}
	public String getRememberKey() {
		return getUser().getRememberKey();
	}
}
