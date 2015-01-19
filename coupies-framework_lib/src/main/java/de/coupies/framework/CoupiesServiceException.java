
package de.coupies.framework;

/**
 * general coupies service exception
 * 
 * @author thomas.volk@denkwerk.com
 * @since 27.08.2010
 *
 */
@SuppressWarnings("serial")
public class CoupiesServiceException extends Exception {

	public CoupiesServiceException(String inArg0) {
		super(inArg0);
	}

	public CoupiesServiceException(Throwable inArg0) {
		super(inArg0);
	}

}
