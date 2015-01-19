package de.coupies.framework.session;

import java.util.Locale;


/**
 * 
 * @author thomas.volk@denkwerk.com
 * @since 19.08.2010
 *
 */
public interface CoupiesSession {
	public static class Identification {
		private final String parameterName;
		private final String value;
		
		public Identification(String parameterName, String value) {
			this.parameterName = parameterName;
			this.value = value;
		}

		public String getParameterName() {
			return parameterName;
		}
		
		public String getValue() {
			return value;
		}
	}	
	Identification getIdentification();
	
	Locale getLocale();
}
