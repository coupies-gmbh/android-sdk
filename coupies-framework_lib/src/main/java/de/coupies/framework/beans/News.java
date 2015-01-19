/**
 * 
 */
package de.coupies.framework.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * news
 * 
 * @author thomas.volk@denkwerk.com
 * @since Sep 3, 2010
 *
 */
public class News implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String message;
	private Date date;
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date inDate) {
		date = inDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String inMessage) {
		message = inMessage;
	}

	public String toString() {
		return String.format("News['%s']", getMessage());
	}
}
