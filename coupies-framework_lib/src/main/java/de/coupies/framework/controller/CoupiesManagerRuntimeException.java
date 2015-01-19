
package de.coupies.framework.controller;

/**
 * general coupies service exception
 * 
 * @author pedro.ribeiro@coupies.de
 * @since 18.01.2012
 * 
 */
@SuppressWarnings("serial")
public class CoupiesManagerRuntimeException extends RuntimeException {

	public CoupiesManagerRuntimeException(String inArg0) {
		super(inArg0);
	}

	public CoupiesManagerRuntimeException(Throwable inArg0) {
		super(inArg0);
	}
}
