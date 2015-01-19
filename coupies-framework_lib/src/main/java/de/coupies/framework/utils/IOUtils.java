package de.coupies.framework.utils;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

	private IOUtils() {
	}

	public static String toString(InputStream in, String encoding) throws IOException {
	    StringBuilder out = new StringBuilder();
	    byte[] b = new byte[4096];
	    for (int n; (n = in.read(b)) != -1;) {
	        out.append(new String(b, 0, n, encoding));
	    }
	    return out.toString();
	}
}
