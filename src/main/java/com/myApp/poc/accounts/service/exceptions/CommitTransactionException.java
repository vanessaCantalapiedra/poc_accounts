package com.myApp.poc.accounts.service.exceptions;

@SuppressWarnings("serial")
public class CommitTransactionException extends RuntimeException {
	static final String message = "En error occurred when commiting the transaction. Please try again.";

	public CommitTransactionException(Throwable e) {
		super(message, e);
	}
}
