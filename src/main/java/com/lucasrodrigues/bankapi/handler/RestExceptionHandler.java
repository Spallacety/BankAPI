package com.lucasrodrigues.bankapi.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.lucasrodrigues.bankapi.exception.AccountNotFoundException;
import com.lucasrodrigues.bankapi.exception.AlreadyRegisteredAccountNumberException;
import com.lucasrodrigues.bankapi.exception.AlreadyRegisteredEmailException;
import com.lucasrodrigues.bankapi.exception.DestinationAccountNotFoundException;
import com.lucasrodrigues.bankapi.exception.InsufficientBalanceException;
import com.lucasrodrigues.bankapi.exception.NegativeBalanceException;
import com.lucasrodrigues.bankapi.exception.NullUserException;
import com.lucasrodrigues.bankapi.exception.SameAccountException;
import com.lucasrodrigues.bankapi.exception.SourceAccountNotFoundException;
import com.lucasrodrigues.bankapi.exception.UserNotFoundException;
import com.lucasrodrigues.bankapi.exception.UserNotOwnerOfAccountException;
import com.lucasrodrigues.bankapi.utils.ErrorDetails;

@ControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails();
	    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
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
	        case "source_account_number":
	        	errorDetails.setError("source account number is mandatory");
	        	break;
	        case "destination_account_number":
	        	errorDetails.setError("destination account number is mandatory");
	        	break;
	        case "amount":
	        	errorDetails.setError("amount must be greater than zero");
	        	break;
	        }
	    }
	    return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e){
		return new ResponseEntity<>(new ErrorDetails("user not found"), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(NullUserException.class)
	public ResponseEntity<?> handleNullUserException(NullUserException e){
		return new ResponseEntity<>(new ErrorDetails("user cannot be null"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(UserNotOwnerOfAccountException.class)
	public ResponseEntity<?> handleUserNotOwnerOfAccountException(UserNotOwnerOfAccountException e){
		return new ResponseEntity<>(new ErrorDetails("you're not the owner of this account"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(AlreadyRegisteredEmailException.class)
	public ResponseEntity<?> handleAlreadyRegisteredEmailException(AlreadyRegisteredEmailException e){
		return new ResponseEntity<>(new ErrorDetails("this email is already registered"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<?> handleAccountNotFoundException(AccountNotFoundException e){
		return new ResponseEntity<>(new ErrorDetails("account not found"), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(SourceAccountNotFoundException.class)
	public ResponseEntity<?> handleSourceAccountNotFoundException(SourceAccountNotFoundException e){
		return new ResponseEntity<>(new ErrorDetails("source account not found"), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(DestinationAccountNotFoundException.class)
	public ResponseEntity<?> handleDestinationAccountNotFoundException(DestinationAccountNotFoundException e){
		return new ResponseEntity<>(new ErrorDetails("destination account not found"), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(AlreadyRegisteredAccountNumberException.class)
	public ResponseEntity<?> handleAlreadyRegisteredAccountNumberException(AlreadyRegisteredAccountNumberException e){
		return new ResponseEntity<>(new ErrorDetails("this account number is already registered"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<?> handleInsufficientBalanceException(InsufficientBalanceException e){
		return new ResponseEntity<>(new ErrorDetails("insufficient balance on source account"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(NegativeBalanceException.class)
	public ResponseEntity<?> handleNegativeBalanceException(NegativeBalanceException e){
		return new ResponseEntity<>(new ErrorDetails("balance can't be negative"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(SameAccountException.class)
	public ResponseEntity<?> handleSameAccountException(SameAccountException e){
		return new ResponseEntity<>(new ErrorDetails("source and destination accounts are the same"), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
}
