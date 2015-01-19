package de.coupies.framework.session;

import de.coupies.framework.beans.User;


public interface UserSession extends CoupiesSession {
	String getRememberKey();
	User getUser();
}
