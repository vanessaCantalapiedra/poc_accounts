package com.myApp.poc.accounts.service.exceptions;

@SuppressWarnings("serial")
public class TransactionNotFoundException extends RuntimeException {
	static final String message = "Transaction Type not found.";

	public TransactionNotFoundException() {
		super(message);
	}
}
