package com.myApp.poc.accounts.service.exceptions;

@SuppressWarnings("serial")
public class WithdrawalLimitException extends RuntimeException {
	static final String message = "The withdrawal limit has been reached";

	public WithdrawalLimitException() {
		super(message);
	}
}
