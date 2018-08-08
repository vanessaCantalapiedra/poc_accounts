package com.myApp.poc.accounts.service.exceptions;

@SuppressWarnings("serial")
public class TransactionNotAllowedException extends RuntimeException {
	static final String message = "Selected transaction not allowed for the account Type.";

	public TransactionNotAllowedException() {
		super(message);
	}
}
