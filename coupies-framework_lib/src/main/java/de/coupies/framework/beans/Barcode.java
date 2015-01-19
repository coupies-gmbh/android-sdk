package de.coupies.framework.beans;

import java.io.Serializable;

/**
 * coupon barcode
 * 
 * @author thomas.volk@denkwerk.com
 * @since 19.08.2010
 *
 */
public class Barcode implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum Type {
		BARCODE,
        COUPIESCODE,
        UNKNOWN
	}
	public enum Scale {
		SMALL(1), MEDIUM(2), LARGE(3);
		private final int id;
		private Scale(int inId) {
			id = inId;
		}
		public int getId() {
			return id;
		}
	}
	private String couponCodeType;
	private String coupiesCode;
	private String imageUrl;

	public Type getType() {
		if(getCouponCodeType().equals("coupiescode")) {
			return Type.COUPIESCODE;
		}
		else if(getCouponCodeType().equals("barcode")) {
			return Type.BARCODE;
		}
		else if(getCouponCodeType().equals("ean13")) {
			return Type.BARCODE;
		}
		else if(getCouponCodeType().equals("ean128")) {
			return Type.BARCODE;
		}
		else {
			return Type.UNKNOWN;
		}
	}

	public String getCouponCodeType() {
		return couponCodeType;
	}
	public void setCouponCodeType(String couponCodeType) {
		this.couponCodeType = couponCodeType;
	}
	public String getCoupiesCode() {
		return coupiesCode;
	}
	public void setCoupiesCode(String coupiesCode) {
		this.coupiesCode = coupiesCode;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getScaledImageUrl(Scale inScale) {
		return String.format("%s/%s", getImageUrl(), inScale.getId());
	}
	
	public String getSmallImageUrl() {
		return getScaledImageUrl(Scale.SMALL);
	}
	
	public String getMediumImageUrl() {
		return getScaledImageUrl(Scale.MEDIUM);
	}
	
	public String getLargeImageUrl() {
		return getScaledImageUrl(Scale.LARGE);
	}
	
	public boolean hasImage() {
		return getImageUrl() != null;
	}
	
	public boolean hasCoupiesCode() {
		return getCoupiesCode() != null;
	}
	
	public String toString() {
		return String.format("Barcode[%s]", getCoupiesCode());
	}
}
