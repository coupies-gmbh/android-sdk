package de.coupies.framework.beans;

import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;

import de.coupies.framework.utils.StringUtils;

/**
 * coupies user
 * 
 * @author thomas.volk@denkwerk.com
 * @since 15.12.2010
 *
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum Gender {
		MALE(1), FEMALE(2);
		private final int id;

		private Gender(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
		
		public static Gender findGender(Integer id) {
			if(id != null) {
				for(Gender gender: Gender.values()) {
					if(gender.getId() == id) {
						return gender;
					}
				}
			}
			return null;
		}
	}
	private Integer id;
	private String email;
	private String facebookId;
	private String rememberKey;
	private String culture;
	private Integer age;
	private Integer genderId;
	private Integer pushIntensity;
	private String pushNotificationsToken;
	private String carrier;
	private String referrer;
	private Double balance;
	private Double saved_total;
	private Payout payout;
	private Currency currency;
	private String lastName;
	private String firstName;
	
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Integer getGenderId() {
		return genderId;
	}
	
	public void setGenderId(Integer genderId) {
		this.genderId = genderId;
	}
	
	public Gender getGender() {
		return Gender.findGender(getGenderId());
	}
	
	public void setGender(Gender gender) {
		if(gender != null) {
			this.genderId = gender.getId();
		}
		else {
			this.genderId = null;
		}
	}
	
	public String getRememberKey() {
		return rememberKey;
	}
	public void setRememberKey(String rememberKey) {
		this.rememberKey = rememberKey;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setCulture(String culture) {
		this.culture = culture;
	}
	public String getCulture() {
		return culture;
	}
	
	public Locale getLocale() {
		return StringUtils.stringToLocale(getCulture());
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String toString() {
		return String.format("User[%s] %s", getId(), getEmail());
	}
	
	public void setReferrer(String installReferrer) {
		this.referrer = installReferrer;
	}
	public String getReferrer() {
		return referrer;
	}
	
	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}
	public String getCarrier() {
		return carrier;
	}
	
	public void setPushNotificationToken(String pushNotificationsToken) {
		this.pushNotificationsToken = pushNotificationsToken;
	}
	public String getPushNotificationsToken() {
		return pushNotificationsToken;
	}
	
	public void setPushIntensity(Integer pushIntensity) {
		this.pushIntensity = pushIntensity;
	}
	public Integer getPushIntensity() {
		return pushIntensity;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Double getSaved_total() {
		return saved_total;
	}
	public void setSaved_total(Double saved_total) {
		this.saved_total = saved_total;
	}
	public Payout getLatestPayout() {
		return payout;
	}
	public void setLatestPayout(Payout payout) {
		this.payout = payout;
	}
	public String getCurrency() {
		return currency.getSymbol();
	}

	public void setCurrency(String currency) {
		this.currency = Currency.getInstance(currency);
	}
	public String getFacebookId() {
		return facebookId;
	}
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
}
