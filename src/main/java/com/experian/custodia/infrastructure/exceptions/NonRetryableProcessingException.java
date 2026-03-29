package com.experian.custodia.infrastructure.exceptions;

public class NonRetryableProcessingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NonRetryableProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
