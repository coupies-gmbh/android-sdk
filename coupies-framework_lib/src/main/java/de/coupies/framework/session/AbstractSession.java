package de.coupies.framework.session;

import java.util.Locale;

public abstract class AbstractSession {
	private Locale locale;	
	
	protected AbstractSession(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		if(locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}
}
