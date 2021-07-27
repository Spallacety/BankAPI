package com.lucasrodrigues.bankapi.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.lucasrodrigues.bankapi.exception.AlreadyRegisteredAccountNumberException;
import com.lucasrodrigues.bankapi.exception.AlreadyRegisteredEmailException;
import com.lucasrodrigues.bankapi.exception.UserNotFoundException;
import com.lucasrodrigues.bankapi.utils.ErrorDetails;

@ControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails();
	    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
	    	String error = fieldError.getField();
	    	boolean isEmpty = fieldError.getCode().equals("NotEmpty");
	        switch (error) {
	        case "email":
	        	if (isEmpty) {
	        		errorDetails.setError("email is mandatory");
		        	break;
	        	}
	        	errorDetails.setError("invalid email format");
	        	break;
	        case "password":
	        	if (isEmpty) {
	        		errorDetails.setError("password is mandatory");
		        	break;
	        	}
	        	errorDetails.setError("password must be at least 6 characters");
	        	break;
	        case "name":
	        	errorDetails.setError("name is mandatory");
	        	break;
	        }
	    }
	    return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e){
		return new ResponseEntity<>(new ErrorDetails("user not found"), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(AlreadyRegisteredEmailException.class)
	public ResponseEntity<?> handleAlreadyRegisteredEmailException(AlreadyRegisteredEmailException e){
		return new ResponseEntity<>(new ErrorDetails("this email is already registered"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(AlreadyRegisteredAccountNumberException.class)
	public ResponseEntity<?> handleAlreadyRegisteredAccountNumberException(AlreadyRegisteredAccountNumberException e){
		return new ResponseEntity<>(new ErrorDetails("this account number is already registered"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
