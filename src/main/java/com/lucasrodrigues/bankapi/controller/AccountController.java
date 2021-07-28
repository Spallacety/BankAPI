package com.lucasrodrigues.bankapi.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasrodrigues.bankapi.model.Account;
import com.lucasrodrigues.bankapi.model.Balance;
import com.lucasrodrigues.bankapi.model.Transfer;
import com.lucasrodrigues.bankapi.service.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping
	@Transactional
	public ResponseEntity<?> saveAccount(@Valid @RequestBody Account account) {
		return new ResponseEntity<>(accountService.save(account), HttpStatus.CREATED);
	}
	
	@PostMapping("/transfer")
	@Transactional
	public ResponseEntity<?> makeTransfer(@Valid @RequestBody Transfer transfer) {
		return new ResponseEntity<>(accountService.transfer(transfer), HttpStatus.OK);
	}
	
	@PostMapping(value = "/balance")
	@Transactional
	public ResponseEntity<?> getBalance(@Valid @RequestBody Balance balance) {
		return new ResponseEntity<>(accountService.balance(balance), HttpStatus.OK);
	}
	
}
