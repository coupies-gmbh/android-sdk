package de.coupies.framework.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;


public class Payout implements Serializable {
	
	public static final int PAYMENT_STATUS_PENDING = 0;
	public static final int PAYMENT_STATUS_APPROVED = 1;
	public static final int PAYMENT_STATUS_VOIDED = 2;
	
	private static final long serialVersionUID = 1L;

	private Date dateCreated;
	private Date dateUpdated;
	private int paymentTypeId;
	private double amount;
	private Currency currency;
	private int paymentStatus;
	private int id;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		SimpleDateFormat formatter = new SimpleDateFormat(	"yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date dt = formatter.parse(dateCreated);
			this.dateCreated = dt;
		} catch (ParseException e) {	}
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(String dateUpdated) {
		SimpleDateFormat formatter = new SimpleDateFormat(	"yyyy-MM-dd HH:mm:ss");
		try {
			java.util.Date dt = formatter.parse(dateUpdated);
			this.dateUpdated = dt;
		} catch (ParseException e) {	}
	}

	public int getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(int paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency.getSymbol();
	}

	public void setCurrency(String currency) {
		this.currency = Currency.getInstance(currency);
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	@Override
	public String toString() {
		super.toString();
		return "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
