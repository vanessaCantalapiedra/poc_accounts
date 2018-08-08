package com.myApp.poc.accounts.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(AccountServiceInternalErrorException.class)
	protected ResponseEntity<Object> handleAccountServiceInternalErrorException(AccountServiceInternalErrorException ex,
			WebRequest request) {
		return buildResponseEntity(new ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR, ex));
	}

	@ExceptionHandler(AccountIdNotFoundException.class)
	protected ResponseEntity<Object> handleAccountIdNotFoundException(AccountIdNotFoundException ex,
			WebRequest request) {
		return buildResponseEntity(new ErrorInfo(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(TransactionNotFoundException.class)
	protected ResponseEntity<Object> handleTransactionNotFoundException(TransactionNotFoundException ex,
			WebRequest request) {
		return buildResponseEntity(new ErrorInfo(HttpStatus.NOT_FOUND, ex));
	}

	@ExceptionHandler(CommitTransactionException.class)
	protected ResponseEntity<Object> handledCommitTransactionException(CommitTransactionException ex,
			WebRequest request) {
		return buildResponseEntity(new ErrorInfo(HttpStatus.CONFLICT, ex.getMessage(), ex.getCause()));
	}

	@ExceptionHandler(AccountIdMissingException.class)
	protected ResponseEntity<Object> handleAccountIdMissingException(AccountIdMissingException ex, WebRequest request) {
		return buildResponseEntity(new ErrorInfo(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(AccountIdsMatchException.class)
	protected ResponseEntity<Object> handleAccountIdsMatchException(AccountIdsMatchException ex, WebRequest request) {
		return buildResponseEntity(new ErrorInfo(HttpStatus.BAD_REQUEST, ex));
	}

	@ExceptionHandler(TransactionNotAllowedException.class)
	protected ResponseEntity<Object> handleTransactionNotAllowedException(TransactionNotAllowedException ex,
			WebRequest request) {
		return buildResponseEntity(new ErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, ex));
	}

	@ExceptionHandler(WithdrawalLimitException.class)
	protected ResponseEntity<Object> handleWithdrawalLimitException(WithdrawalLimitException ex, WebRequest request) {
		return buildResponseEntity(new ErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, ex));
	}

	private ResponseEntity<Object> buildResponseEntity(ErrorInfo errorInfo) {
		return new ResponseEntity<>(errorInfo, errorInfo.getStatus());
	}
}
