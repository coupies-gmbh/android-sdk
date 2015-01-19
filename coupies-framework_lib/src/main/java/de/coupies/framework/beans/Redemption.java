package de.coupies.framework.beans;

import java.io.Serializable;
import java.util.Date;

public class Redemption implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public enum Type{
		barcode,
		coupiescode,
		actioncode,
		none,
		unknown
    }
	
	public static final int STATUS_INVALID = 0;
	public static final int STATUS_VALID = 1;
	public static final int STATUS_PENDING = 2;
	
	private Date time;
	
	private Integer id;
	private Integer allocationid;
	private Integer valid;
	private Integer status;
	private Integer userId;
	private Integer rejectionreason_id;
	private Integer quantity;
	private Boolean reuploadable;
	
	private Double cashback_value;
	
	private Coupon coupon;
	private Promotion promotion;
	private Location location;
	
	private String type;
	private String text;
	private String rejectionreason;
	private Receipt receipt;
	private String comment;
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Receipt getReceipt() {
		return receipt;
	}
	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getRejectionreason() {
		return rejectionreason;
	}
	public void setRejectionreason(String rejectionreason) {
		this.rejectionreason = rejectionreason;
	}
	public Double getCashback_vaule() {
		return cashback_value;
	}
	public void setCashback_vaule(Double cashback_vaule) {
		this.cashback_value = cashback_vaule;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Integer getAllocationid() {
		return allocationid;
	}
	public void setAllocationid(Integer allocationid) {
		this.allocationid = allocationid;
	}
	public Integer getValid() {
		return valid;
	}
	public void setValid(Integer valid) {
		this.valid = valid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Coupon getCoupon() {
		return coupon;
	}
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
	public Promotion getPromotion() {
		return promotion;
	}
	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Type getType() {
		if(getStringType().equals("coupiescode")) {
			return Type.coupiescode;
		}
		else if(getStringType().equals("barcode")||
				getStringType().equals("ean13")||
				getStringType().equals("ean128")) {
			return Type.barcode;
		}
		else if(getStringType().equals("actioncode")) {
			return Type.actioncode;
		}
		else if(getStringType().equals("none")) {
			return Type.none;
		}
		else {
			return Type.unknown;
		}
	}
	private String getStringType(){
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Integer getRejectionreason_id() {
		return rejectionreason_id;
	}
	public void setRejectionreason_id(Integer rejectionreason_id) {
		this.rejectionreason_id = rejectionreason_id;
	}
	public Boolean getReuploadable() {
		return reuploadable;
	}
	public void setReuploadable(boolean reuploadable) {
		this.reuploadable = reuploadable;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((allocationid == null) ? 0 : allocationid.hashCode());
		result = prime * result
				+ ((rejectionreason == null) ? 0 : rejectionreason.hashCode());
		result = prime
				* result
				+ ((rejectionreason_id == null) ? 0 : rejectionreason_id
						.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Redemption other = (Redemption) obj;
		if (allocationid == null) {
			if (other.allocationid != null)
				return false;
		} else if (!allocationid.equals(other.allocationid))
			return false;
		if (rejectionreason == null) {
			if (other.rejectionreason != null)
				return false;
		} else if (!rejectionreason.equals(other.rejectionreason))
			return false;
		if (rejectionreason_id == null) {
			if (other.rejectionreason_id != null)
				return false;
		} else if (!rejectionreason_id.equals(other.rejectionreason_id))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;

		return reuploadable==other.reuploadable;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}	
}
