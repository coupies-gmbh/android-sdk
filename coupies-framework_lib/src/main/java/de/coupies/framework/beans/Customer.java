/**
 * 
 */
package de.coupies.framework.beans;

import java.io.Serializable;

/**
 * 
 * coupies business partner
 * 
 * @author thomas.volk@denkwerk.com
 * @since Sep 3, 2010
 *
 */
public class Customer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String iconUrl;
	private String title;
	private String description;

	public String getTitle() {
		return title;
	}
	public void setTitle(String inTitle) {
		title = inTitle;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer inId) {
		id = inId;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String inIconUrl) {
		iconUrl = inIconUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String inDescription) {
		description = inDescription;
	}

	public String toString() {
		return String.format("Customer[%s] %s", getId(), getTitle());
	}
}
