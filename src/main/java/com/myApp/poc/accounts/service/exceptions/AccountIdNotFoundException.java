package com.myApp.poc.accounts.service.exceptions;

@SuppressWarnings("serial")
public class AccountIdNotFoundException extends RuntimeException {
	static final String message = "Account id not found.";

	public AccountIdNotFoundException() {
		super(message);
	}
}
