package com.lucasrodrigues.bankapi.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.lucasrodrigues.bankapi.exception.UserNotFoundException;
import com.lucasrodrigues.bankapi.utils.ErrorDetails;

@ControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException userNotFoundException){
		return new ResponseEntity<>(new ErrorDetails("User not found!"), HttpStatus.NOT_FOUND);
	}

}
