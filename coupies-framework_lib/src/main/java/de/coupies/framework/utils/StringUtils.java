/**
 * 
 */
package de.coupies.framework.utils;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * @author thomas.volk@denkwerk.com
 * @since Sep 9, 2010
 *
 */
public class StringUtils {

	private StringUtils() {
	}

	public static boolean isNotEmpty(String text) {
		return text != null && text.trim().length() > 0;
	}
	
	public static String join(Object[] list, String sep) {
		boolean first = true;
		StringBuilder builder = new StringBuilder();
		for(Object o: list) {
			if(first) {
				first = false;
			}
			else {
				builder.append(sep);
			}
			builder.append(o);
		}
		return builder.toString();
	}
	
	public static Locale stringToLocale(String cultureString) {
		Locale locale = null;
		if(cultureString != null) {
			StringTokenizer tokenizer = new StringTokenizer(cultureString, "_");
			if(tokenizer.countTokens() > 1) {
				String language = tokenizer.nextToken();
				String country = tokenizer.nextToken();
				locale = new Locale(language, country);
			}
			else {
				locale = new Locale(cultureString);
			}
		}
		return locale;
	}
}
