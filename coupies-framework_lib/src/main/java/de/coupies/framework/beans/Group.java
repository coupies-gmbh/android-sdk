/**
 * 
 */
package de.coupies.framework.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thomas.volk@denkwerk.com
 * @since Sep 3, 2010
 *
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Coupon> coupons;
	private Affiliate affiliate;
	
	public Affiliate getAffiliate() {
		return affiliate;
	}
	
	public void setAffiliate(Affiliate inAffiliate) {
		affiliate = inAffiliate;
	}
	
	public String getTitle() {
		return getAffiliate().getTitle();
	}
	
	public List<Coupon> getCoupons() {
		if(coupons == null) {
			coupons = new ArrayList<Coupon>();
		}
		return coupons;
	}
	
	public void setCoupons(List<Coupon> inCoupons) {
		coupons = inCoupons;
	}
	
	public String toString() {
		return String.format("Group[%s] - <%s>", getAffiliate(), getCoupons());
	}
	
}
