package de.coupies.framework.beans;

import java.io.Serializable;

public class DealCoupon extends Coupon implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String fineprint;
	private String highlights;
	private Price price;
	private Price originalPrice;
	private Float discount;

	
	public String getFineprint() {
		return fineprint;
	}
	public void setFineprint(String fineprint) {
		this.fineprint = fineprint;
	}
	public String getHighlights() {
		return highlights;
	}
	public void setHighlights(String highlights) {
		this.highlights = highlights;
	}
	public Price getPrice() {
		return price;
	}
	public void setPrice(Price price) {
		this.price = price;
	}
	public Price getOriginalPrice() {
		return originalPrice;
	}
	public void setOriginalPrice(Price originalPrice) {
		this.originalPrice = originalPrice;
	}
	public Float getDiscount() {
		return discount;
	}
	public void setDiscount(Float discount) {
		this.discount = discount;
	}	
	public static class Price implements Serializable {

		private static final long serialVersionUID = 1L;
		
		public Float value;
		public Currency currency;
		
		public Price(Float value, Currency currency) {
			this.value = value;
			this.currency = currency;
		}
		public enum Currency implements Serializable {
			EURO,
			DOLLAR,
			SWISS_FRANK,
		}
	}
}
