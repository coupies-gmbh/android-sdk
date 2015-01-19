package de.coupies.framework.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class URLUtils {
	public static String addParameter(String url, String name, String value) {
		int qpos = url.indexOf('?');
		int hpos = url.indexOf('#');
		char sep = qpos == -1 ? '?' : '&';
		String seg = sep + encodeUrl(name) + '=' + encodeUrl(value);
		return hpos == -1 ? url + seg : url.substring(0, hpos) + seg
				+ url.substring(hpos);
	}

	public static String encodeUrl(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			throw new IllegalArgumentException(uee);
		}
	}
	
	/** erzeugt (php-) url vertraegliches Zahlenformat */
	public static String convertFromDouble(Double number) {
		// String test = String.format(Locale.GERMANY, "%f", number); BUG in Android 2.2
		//Die String.format gibt bei Locale.GERMANY nicht immer "," zurück, daher hier manuell per String.replace
		return replaceDotWithComma(number.toString());				
	}
	
	/** erzeugt (php-) url vertraegliches Zahlenformat */
	public static String convertFromFloat(Float number) {
		return replaceDotWithComma(number.toString());
	}

	private static String replaceDotWithComma(String number_string) {
		number_string = number_string.replace(".", ",");
		return number_string;
	}

	/**
	 * konvertiert (php-) url vertraegliches Zahlenformat zurueck
	 * 
	 * @throws ParseException
	 */
	public static Double convertToDouble(String number) throws ParseException {
		NumberFormat format = DecimalFormat.getInstance(Locale.GERMANY);
		return (Double) format.parse(number);
	}
}
