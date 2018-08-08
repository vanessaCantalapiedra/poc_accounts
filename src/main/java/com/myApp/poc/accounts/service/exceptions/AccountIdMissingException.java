package com.myApp.poc.accounts.service.exceptions;

@SuppressWarnings("serial")
public class AccountIdMissingException extends RuntimeException {
	static final String message = "Account id is missing in the request.";

	public AccountIdMissingException() {
		super(message);
	}
}
