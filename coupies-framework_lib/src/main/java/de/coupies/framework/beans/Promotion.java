package de.coupies.framework.beans;

import java.io.Serializable;

public class Promotion extends Coupon implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String action_button_text;
	
	public String getActionButtonText() {
		return action_button_text;
	}
	public void setActionButtonText(String actionButtonText) {
		this.action_button_text = actionButtonText;
	}
	
}
