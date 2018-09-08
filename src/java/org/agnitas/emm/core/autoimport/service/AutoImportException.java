package org.agnitas.emm.core.autoimport.service;

public class AutoImportException extends Exception {

	private String errorMessageKey;

	public AutoImportException(String errorMessageKey, Throwable cause) {
		super(cause);
		this.errorMessageKey = errorMessageKey;
	}

	public AutoImportException(String errorMessageKey) {
		super();
		this.errorMessageKey = errorMessageKey;
	}

	public String getErrorMessageKey() {
		return errorMessageKey;
	}

	public void setErrorMessageKey(String errorMessageKey) {
		this.errorMessageKey = errorMessageKey;
	}
}
