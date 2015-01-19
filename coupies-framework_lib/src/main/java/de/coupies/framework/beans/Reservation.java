/**
 * The Class for Reservation-Objects from COUPIES-API
 * @author larseimermacher
 * @since 26.02.2013
 */
package de.coupies.framework.beans;

import java.util.Date;

public class Reservation {
	
	private Date time;
	private Integer allocationId;
	private Integer couponId;
	private String couponWebLink;
	private String couponType;
	private String tokenParameter;
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Integer getAllocationId() {
		return allocationId;
	}
	public void setAllocationId(Integer allocationId) {
		this.allocationId = allocationId;
	}
	public Integer getCouponId() {
		return couponId;
	}
	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}
	public String getCouponWebLink() {
		return couponWebLink;
	}
	public void setCouponWebLink(String couponWebLink) {
		this.couponWebLink = couponWebLink;
	}
	public String getCouponType() {
		return couponType;
	}
	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}
	public String getTokenParameter() {
		return tokenParameter;
	}
	public void setTokenParameter(String tokenParameter) {
		this.tokenParameter = tokenParameter;
	}
}
