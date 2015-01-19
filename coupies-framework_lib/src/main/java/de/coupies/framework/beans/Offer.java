package de.coupies.framework.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.util.Log;

/**
 * Offer
 * 
 * @author Lars Eimermacher
 * @since 20.12.2012
 *
 */
public class Offer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int NO_ACTION=0;
	public static final int ACTION_NFC_QR=1;
	public static final int ACTION_ONLINE=2;
	public static final int ACTION_CASHBACK=3;
	
	private String 	title,
					details,
					imageUrl,
					featuredImageUrl,
					frontendUrl,
					url,
					targetUrl,
					videoUrl,
					swTicketId,
					locationButtonText,
					validityButtonText;
	
	private List<String> infoImageUrlList;
	
	private Affiliate affiliate;
	
	private Customer customer;
	
	private Date expireDate;
	private Date startDate;
	private Date creationDate;
	private Date updateDate;
	
	private boolean bookmarked;
	private boolean unread;
	private boolean liked;
	@SuppressWarnings("unused")
	private boolean hasInfoImages;
	private boolean isPlaceholder = false,
					isPlaceholderForEmptyNotifications = false,
					showCustomerCoupons =true;

	private Integer likes;
	private Integer passId;
	private Integer action;
	private Integer id;
	private Integer priority;
	
	private Double distance;
	private Double faceValue;

	//currently only one interest is being retrieved
	//so we call it main category, review this later
	private Category mainCategory;
	
	
	public boolean isPlaceholderForEmptyNotifications() {
		return isPlaceholderForEmptyNotifications;
	}

	public void setPlaceholderForEmptyNotifications(
			boolean isPlaceholderForEmptyNotifications) {
		this.isPlaceholderForEmptyNotifications = isPlaceholderForEmptyNotifications;
	}
	
	public boolean isPlaceholder() {
		return isPlaceholder;
	}

	public void setPlaceholder(boolean isPlaceholder) {
		this.isPlaceholder = isPlaceholder;
	}

	public Category getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(Category mainCategory) {
		this.mainCategory = mainCategory;
	}
	
	public String getFrontendUrl() {
		return frontendUrl;
	}

	public void setFrontendUrl(String frontendUrl) {
		this.frontendUrl = frontendUrl;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date inUpdateDate) {
		updateDate = inUpdateDate;
	}

	public String getUrl() {
		return url;
	}
	public boolean hasInfoImages(){
		if(this.getInfoImageUrl()!=null || this.getInfoImageUrl().length()!=0){
			return true;
		}
		return false;
	}
	public void setUrl(String inUrl) {
		url = inUrl;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date inCreationDate) {
		creationDate = inCreationDate;
	}

	public boolean getBookmarked() {
		return bookmarked;
	}

	public void setBookmarked(boolean inBookmarked) {
		bookmarked = inBookmarked;
	}
	
	public boolean getLiked() {
		return liked;
	}

	public void setLiked(boolean inLiked) {
		liked = inLiked;
	}
	
	public int getLikes() {
		return likes;
	}

	public void setLikes(int inLikes) {
		likes = inLikes;
	}
	
	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date inExpireDate) {
		expireDate = inExpireDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date inExpireDate) {
		startDate = inExpireDate;
	}

	public Affiliate getAffiliate() {
		return affiliate;
	}

	public void setAffiliate(Affiliate inAffiliate) {
		affiliate = inAffiliate;
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
	
	public String getDistanceRepresentation() {
		if(getDistance() == null) {
			return "";
		}
		else {
			if(getDistance()<1){
				return String.format("%1$dm", (int)(getDistance()*1000));
			}else{
				return String.format("%.1fkm", getDistance());
			}
		}
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
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String image) {
		this.imageUrl = image;
	}
	public void setSwTicketId(String swTicketId){
		this.swTicketId=swTicketId;
	}
	public String getSwTicketId(){
		return this.swTicketId;
	}
	public boolean isSamsungWallet(){
		return this.getSwTicketId() != null && this.swTicketId.length() > 0;
	}
	public void setVideo(String videoUrl){
		this.videoUrl=videoUrl;
	}
	public String getVideo(){
		return this.videoUrl;
	}
	public boolean hasVideo(){
		return this.getVideo() != null && this.getVideo().length() > 0;
	}
	public String getFeaturedImageUrl() {
		return featuredImageUrl;
	}
	public void setFeaturedImageUrl(String image) {
		this.featuredImageUrl = image;
	}
	public String getInfoImageUrl() {
		if (infoImageUrlList != null && !infoImageUrlList.isEmpty()) {
			return infoImageUrlList.get(0);
		}
		else {
			return null;
		}
	}
	public List<String> getInfoImageUrlList() {
		return this.infoImageUrlList;
	}
	public void setInfoImageUrlList(List<String> list) {
		this.infoImageUrlList = list;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer id) {
		this.priority = id;
	}

	public String toString() {
		return String.format("Coupon[%s] %s", getId(), getTitle());
	}

	public boolean isFeatured() {
		if (getPriority() == 0) {
			return true;
		}
		return false;
	}
	
	public void setPassId(int passId){
		this.passId=passId;
	}
	
	public Integer getPassId(){
		return passId;
	}
	
	public void setUnread(boolean unread){
		this.unread=unread;
	}
	
	public boolean isUnread(){
		return unread;
	}
	
	public boolean isPassbook(){
		try {
			if(passId != null && passId >0)
				return true;
		} catch (Exception e) {
			Log.w("Coupon", e);
			return false;
		}
		return false;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public Integer getAction() {
		return action;
	}

	public void setAction(Integer action) {
		this.action = action;
	}

	public Double getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(Double faceValue) {
		this.faceValue = faceValue;
	}

	public String getLocationButtonText() {
		return locationButtonText;
	}

	public void setLocationButtonText(String locationButtonText) {
		this.locationButtonText = locationButtonText;
	}

	public String getValidityButtonText() {
		return validityButtonText;
	}

	public void setValidityButtonText(String validityButtonText) {
		this.validityButtonText = validityButtonText;
	}
	
	public boolean showCustomerCoupons(){
		return showCustomerCoupons;
	}
	
	public void setShowCustomerCoupons(boolean showCustomerCoupons){
		this.showCustomerCoupons = showCustomerCoupons;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime
				* result
				+ ((infoImageUrlList == null) ? 0 : infoImageUrlList.hashCode());
		result = prime * result
				+ ((updateDate == null) ? 0 : updateDate.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((distance == null) ? 0 : distance.hashCode());
		
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
		Offer other = (Offer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (infoImageUrlList == null) {
			if (other.infoImageUrlList != null)
				return false;
		} else if (!infoImageUrlList.equals(other.infoImageUrlList))
			return false;
		if (updateDate == null) {
			if (other.updateDate != null)
				return false;
		} else if (!updateDate.equals(other.updateDate))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url)){
				return false;
			}
		if(passId!=null)
			if(swTicketId!=null && other.swTicketId!=null)
				if(!swTicketId.equals(other.swTicketId))
					return false;
		if (unread != other.unread)
				return false;
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (distance != other.distance)
				return false;

		return true;
	}
}

