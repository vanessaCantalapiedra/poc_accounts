package com.myApp.poc.accounts.service.exceptions;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
public class ErrorInfo {

	private HttpStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;
	private String message;
	@JsonInclude(Include.NON_NULL)
	private String debugMessage;
	private static final String DEFAULT_MESSAGE = "Unknown error";
	// private List<ApiSubError> subErrors;

	private ErrorInfo() {
		timestamp = LocalDateTime.now();
	}

	ErrorInfo(HttpStatus status, Throwable ex) {
		this();
		this.status = status;
		this.message = Optional.ofNullable(ex.getMessage()).orElse(DEFAULT_MESSAGE);
	}

	ErrorInfo(HttpStatus status, String userMessage, Throwable ex) {
		this();
		this.status = status;
		this.message = userMessage;
		this.debugMessage = ex.getCause().getMessage();
	}

}
