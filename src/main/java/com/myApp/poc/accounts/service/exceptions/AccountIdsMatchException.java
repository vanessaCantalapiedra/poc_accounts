package com.myApp.poc.accounts.service.exceptions;

@SuppressWarnings("serial")
public class AccountIdsMatchException extends RuntimeException {
	static final String message = "Accounts destiny and source have the same id.";

	public AccountIdsMatchException() {
		super(message);
	}
}
