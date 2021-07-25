package com.lucasrodrigues.bankapi.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.lucasrodrigues.bankapi.exception.UserNotFoundException;
import com.lucasrodrigues.bankapi.utils.ErrorDetail;


@ControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException userNotFoundException){
		return new ResponseEntity<>(new ErrorDetail("User not found!"), HttpStatus.NOT_FOUND);
	}
}
