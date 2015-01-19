package de.coupies.framework.beans;

import java.io.Serializable;

/**
 * coupon
 * 
 * @author thomas.volk@denkwerk.com
 * @since 19.08.2010
 *
 */
public class Coupon extends Offer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer remainingTotal;
	private Integer remaining;
	private boolean closestLocationAcceptsSticker;
	private Integer remainingInterval;
	
	public Integer getRemainingInterval() {
		return remainingInterval;
	}

	public void setRemainingInterval(Integer inRemainingInterval) {
		remainingInterval = inRemainingInterval;
	}

	/**
	 * löst Coupon ein
	 * @return true wenn der Coupon einglöst werden konnte
	 */
	public boolean redeem() {
		if(isRedeemable()) {
			if(remaining != null) {
				remaining--;
			}
			return true;
		}
		return false;
	}

	/**
	 * @return true wenn der Coupon einglöst werden kann
	 */
	public boolean isRedeemable() {
		return remaining == null || remaining > 0;
	}
	
	public Integer getRemainingTotal() {
		return remainingTotal;
	}
	public void setRemainingTotal(Integer remainingTotal) {
		this.remainingTotal = remainingTotal;
	}	
	
	
	public Boolean getClosestLocationAcceptsSticker() {
		return closestLocationAcceptsSticker;
	}
	public void setClosestLocationAcceptsSticker(Boolean acceptsSticker) {
		this.closestLocationAcceptsSticker = acceptsSticker;
	}		
	public void setRemaining(Integer remaining) {
		this.remaining = remaining;
	}
	public Integer getRemaining() {
		return remaining;
	}
	
}

