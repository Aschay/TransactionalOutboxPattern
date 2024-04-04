package com.aschay.TxOutboxDebzium.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RessourceNotFoundException.class)
	public ResponseEntity<CustomErrorResponse> customHandleNotFound(Exception ex, WebRequest request) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setTimestamp(LocalDateTime.now());
		errors.setMessage(ex.getMessage());
		errors.setStatus(HttpStatus.NOT_FOUND.value());
		errors.setDetails(ex.getCause().getLocalizedMessage());
		return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<CustomErrorResponse> customBadRequest(Exception ex, WebRequest request) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setTimestamp(LocalDateTime.now());
		errors.setMessage(ex.getMessage());
		errors.setStatus(HttpStatus.BAD_REQUEST.value());
		errors.setDetails(ex.getCause().getLocalizedMessage());
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setTimestamp(LocalDateTime.now());
		errors.setMessage(ex.getMessage());
		errors.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
		StringBuilder sb = new StringBuilder();
		sb.append(ex.getMethod());
		sb.append("Method is not supported for this request. Supported methods are ");
		ex.getSupportedHttpMethods().forEach(t -> sb.append(t + " "));
		errors.setDetails(sb.toString());
		return new ResponseEntity<>(errors, status);

	}

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		CustomErrorResponse errors = new CustomErrorResponse();
		errors.setTimestamp(LocalDateTime.now());
		errors.setStatus(status.value());
		errors.setMessage("validation failed");
		errors.setDetails(ex.getMessage());
		return new ResponseEntity<>(errors, status);
	}

	public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		CustomErrorResponse errors = new CustomErrorResponse();
		String detail = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
		errors.setTimestamp(LocalDateTime.now());
		errors.setMessage(detail);
		errors.setStatus(HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(errors, status);
	}
	
	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(
	  ConstraintViolationException ex, WebRequest request) {
	    List<String> errors = new ArrayList<String>();
	    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
	        errors.add(violation.getRootBeanClass().getName() + " " + 
	          violation.getPropertyPath() + ": " + violation.getMessage());
	    }
	    CustomErrorResponse error = new CustomErrorResponse();
	    error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMessage(ex.getLocalizedMessage());
		StringBuffer sb = new StringBuffer();
		for(String s : errors) {
			sb.append(" ");
			sb.append(s);
		}
		error.setDetails(sb.toString());
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}
	
	

}
