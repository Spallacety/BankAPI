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
import com.lucasrodrigues.bankapi.exception.DestinationAccountNotFoundException;
import com.lucasrodrigues.bankapi.exception.InsufficientBalanceException;
import com.lucasrodrigues.bankapi.exception.NegativeBalanceException;
import com.lucasrodrigues.bankapi.exception.NullUserException;
import com.lucasrodrigues.bankapi.exception.SameAccountException;
import com.lucasrodrigues.bankapi.exception.SourceAccountNotFoundException;
import com.lucasrodrigues.bankapi.exception.UserNotOwnerOfAccountException;
import com.lucasrodrigues.bankapi.model.Account;
import com.lucasrodrigues.bankapi.model.Balance;
import com.lucasrodrigues.bankapi.model.Transfer;
import com.lucasrodrigues.bankapi.model.User;
import com.lucasrodrigues.bankapi.repository.AccountRepository;
import com.lucasrodrigues.bankapi.repository.UserRepository;
import com.lucasrodrigues.bankapi.utils.AccountDetails;
import com.lucasrodrigues.bankapi.utils.BalanceDetails;
import com.lucasrodrigues.bankapi.utils.TransferDetails;
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
		User user = getActualUser();
		if (accountRepository.getByNumber(account.getNumber()) != null) {
			throw new AlreadyRegisteredAccountNumberException();
		}
		if (account.getBalance() < 0) {
			throw new NegativeBalanceException();
		}
		account.setUserEmail(user.getEmail());
		accountRepository.save(account);
		return new ResponseEntity<>(new AccountDetails(account.getNumber(), account.getBalance(), new UserDetails(user.getEmail(), user.getName())), HttpStatus.CREATED);
	}
	
	@PostMapping("/transfer")
	@Transactional
	public ResponseEntity<?> transfer(@Valid @RequestBody Transfer transfer) {
		User user = getActualUser();
		if (accountRepository.getByNumber(transfer.getSource_account_number()) == null) {
			throw new SourceAccountNotFoundException();
		}
		if (accountRepository.getByNumber(transfer.getDestination_account_number()) == null) {
			throw new DestinationAccountNotFoundException();
		}
		if (!(accountRepository.getByNumber(transfer.getSource_account_number())).getUserEmail().equals(user.getEmail())) {
			throw new UserNotOwnerOfAccountException();
		}
		Account sourceAccount = accountRepository.getByNumber(transfer.getSource_account_number());
		Account destinationAccount = accountRepository.getByNumber(transfer.getDestination_account_number());
		if (sourceAccount.getBalance() < transfer.getAmount()) {
			throw new InsufficientBalanceException();
		}
		if (sourceAccount == destinationAccount) {
			throw new SameAccountException();
		}
		sourceAccount.removeBalance(transfer.getAmount());
		destinationAccount.addBalance(transfer.getAmount());
		accountRepository.save(sourceAccount);
		accountRepository.save(destinationAccount);
		return new ResponseEntity<>(new TransferDetails(transfer.getAmount(), sourceAccount.getNumber(), destinationAccount.getNumber(), new UserDetails(user.getEmail(), user.getName())), HttpStatus.OK);
	}
	
	@PostMapping(value = "/balance")
	@Transactional
	public ResponseEntity<?> getBalance(@Valid @RequestBody Balance balance) {
		User user = getActualUser();
		if (!(accountRepository.getByNumber(balance.getAccount_number())).getUserEmail().equals(user.getEmail())) {
			throw new UserNotOwnerOfAccountException();
		}
		return new ResponseEntity<>(new BalanceDetails(balance.getAccount_number(), (accountRepository.getByNumber(balance.getAccount_number())).getBalance()), HttpStatus.OK);
	}
	
	public void verifyIfExists(Long id) {
		if (!accountRepository.findById(id).isPresent()) {
			throw new AccountNotFoundException();
		}
	}
	
	public User getActualUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.getByEmail(email);
		if (user == null) {
			throw new NullUserException();
		}
		return user;
	}
	
}
