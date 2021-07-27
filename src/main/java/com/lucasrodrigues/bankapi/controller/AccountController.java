package com.lucasrodrigues.bankapi.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasrodrigues.bankapi.exception.AccountNotFoundException;
import com.lucasrodrigues.bankapi.exception.AlreadyRegisteredAccountNumberException;
import com.lucasrodrigues.bankapi.model.Account;
import com.lucasrodrigues.bankapi.model.User;
import com.lucasrodrigues.bankapi.repository.AccountRepository;
import com.lucasrodrigues.bankapi.repository.UserRepository;
import com.lucasrodrigues.bankapi.utils.AccountDetails;
import com.lucasrodrigues.bankapi.utils.UserDetails;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	public AccountController(AccountRepository accountRepository, UserRepository userRepository) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}
	
	@GetMapping
	public ResponseEntity<?> getAllAccounts() {
		return new ResponseEntity<>(accountRepository.findAll(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getAccountById(@PathVariable Long id) {
		verifyIfExists(id);
		return new ResponseEntity<>(accountRepository.findById(id).get(), HttpStatus.OK);
	}

	@PostMapping
	@Transactional
	public ResponseEntity<?> saveAccount(@Valid @RequestBody Account account) {
		if (accountRepository.getByNumber(account.getNumber()) != null) {
			throw new AlreadyRegisteredAccountNumberException();
		}
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.getByEmail(email);
		account.setUserEmail(email);
		accountRepository.save(account);
		return new ResponseEntity<>(new AccountDetails(account.getNumber(), account.getBalance(), new UserDetails(user.getEmail(), user.getName())), HttpStatus.OK);
	}
	
	public void verifyIfExists(Long id) {
		if (!accountRepository.findById(id).isPresent()) {
			throw new AccountNotFoundException();
		}
	}
	
}
