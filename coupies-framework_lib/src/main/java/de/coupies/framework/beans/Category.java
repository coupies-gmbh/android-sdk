/**
 * 
 */
package de.coupies.framework.beans;

import java.io.Serializable;

/**
 * coupon category
 * 
 * @author thomas.volk@denkwerk.com
 * @since 30.08.2010
 *
 */
public class Category implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String title;
	private String description;
	private String url;
	private Integer couponsCount;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer inId) {
		id = inId;
	}
	
	public Integer getCouponsCount() {
		return couponsCount;
	}
	public void setCouponsCount(Integer inCouponsCount) {
		couponsCount = inCouponsCount;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String inTitle) {
		title = inTitle;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String inDescription) {
		description = inDescription;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String inUrl) {
		url = inUrl;
	}
	
	public String toString() {
		return String.format("Category[%s] %s", getId(), getTitle());
	}
}
