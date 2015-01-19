package de.coupies.framework.services.content;

import java.util.ArrayList;
import java.util.List;

/**
 * coupies response validation
 * 
 * @author thomas.volk@denkwerk.com
 * @since 19.08.2010
 *
 */
public class Validation {
	public static class Error {
		private final String field;
		private final String message;
		public Error(String field, String message) {
			this.field = field;
			this.message = message;
		}
		public String getField() {
			return field;
		}
		public String getMessage() {
			return message;
		}		
	}
	private List<Error> errors;
	
	public List<Error> getErrors() {
		if(errors == null) {
			errors = new ArrayList<Error>();
		}
		return errors;
	}
	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Error error: getErrors()) {
			builder.append(error.getMessage()).append("\n");
		}
		return builder.toString().trim();
	}
	
	public boolean isEmpty() {
		return getErrors().isEmpty();
	}
}
