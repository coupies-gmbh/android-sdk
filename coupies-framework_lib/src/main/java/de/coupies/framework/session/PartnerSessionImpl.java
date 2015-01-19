package de.coupies.framework.session;

import java.util.Locale;

public class PartnerSessionImpl extends AbstractSession implements PartnerSession {
	private String partnerToken;

	public PartnerSessionImpl() {
		this(null, null);
	}
	
	public PartnerSessionImpl(String partnerToken) {
		this(null, partnerToken);
	}
	
	public PartnerSessionImpl(Locale locale, String partnerToken) {
		super(locale);
		this.partnerToken = partnerToken;
	}

	public String getPartnerToken() {
		return partnerToken;
	}

	public void setPartnerToken(String partnerId) {
		this.partnerToken = partnerId;
	}

	public Identification getIdentification() {
		return new Identification("partner_token", getPartnerToken());
	}
}

