package com.aschay.TxOutboxDebzium.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class RessourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5258264129361616892L;
	private String resourceName;
	private String fieldName;
	private Object fieldValue;


	public RessourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
		super(String.format("%s Not found with %s : '%s'", resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}
}