package de.coupies.framework.beans;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * location for coupon redemption
 * 
 * @author thomas.volk@denkwerk.com
 * @since 19.08.2010
 *
 */
public class Location implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Customer customer;
	
	private Double 	latitude,
					longitude,
					distance;
	
	private String 	title,
					address,
					acceptsSticker,
					website,
					url,
					phoneNumber;
	
	private Integer id,
					couponCount,
					productAvailable;
	
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String inWebsite) {
		website = inWebsite;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getAcceptsSticker() {
		return acceptsSticker;
	}
	public void setAcceptsSticker(String acceptsSticker) {
		this.acceptsSticker = acceptsSticker;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String inPhoneNumber) {
		phoneNumber = inPhoneNumber;
	}
	public Integer getCouponCount() {
		return couponCount;
	}
	public void setCouponCount(Integer inCouponCount) {
		couponCount = inCouponCount;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer inCustomer) {
		customer = inCustomer;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String inUrl) {
		url = inUrl;
	}
	
	public String getDistanceRepresentation() {
		 String strDistance;
		 NumberFormat format = NumberFormat.getInstance();	                 
	     if (distance != null && distance > 999) {	        	    	
	    	 format.setMaximumFractionDigits(1);
	    	 strDistance = String.valueOf(format.format(distance/1000)) + " km";
	     } else if(distance != null && distance > 0) {
	    	 format.setMaximumFractionDigits(0);
	    	 strDistance = String.valueOf(format.format(distance)) + " m";
	     }else {
	    	 strDistance = "";
	     }
	     return strDistance;
	}
	
	public Integer getProductAvailable() {
		return productAvailable;
	}
	public void setProductAvailable(Integer productAvailable) {
		this.productAvailable = productAvailable;
	}
	public String toString() {
		return String.format("Location[%s] lat=%s, long=%s - (%s)", getId(), getLatitude(), getLongitude(), getCustomer());
	}
}
