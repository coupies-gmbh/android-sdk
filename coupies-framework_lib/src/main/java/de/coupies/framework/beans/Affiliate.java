/**
 * 
 */
package de.coupies.framework.beans;

import java.io.Serializable;

/**
 *  affiliate
 * 
 * @author thomas.volk@denkwerk.com
 * @since Sep 3, 2010
 *
 */

public class Affiliate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String iconUrl;
	private String title;
	private String description;
	private String website;
	
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

	public String getWebsite() {
		return website;
	}
	public void setWebsite(String inWebsite) {
		website = inWebsite;
	}

	public String toString() {
		return String.format("Affiliate[%s] %s", getId(), getTitle());
	}
}
