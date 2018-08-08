package com.myApp.poc.accounts.service.exceptions;

@SuppressWarnings("serial")
public class AccountServiceInternalErrorException extends RuntimeException {
	static final String message = "En error occurred during the request process";

	public AccountServiceInternalErrorException() {
		super(message);
	}
}
