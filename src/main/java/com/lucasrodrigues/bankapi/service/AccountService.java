package com.lucasrodrigues.bankapi.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lucasrodrigues.bankapi.dto.AccountDTO;
import com.lucasrodrigues.bankapi.dto.BalanceDTO;
import com.lucasrodrigues.bankapi.dto.TransferDTO;
import com.lucasrodrigues.bankapi.dto.UserDTO;
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

@Service
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}

	public AccountDTO save(Account account) {
		User user = getActualUser();
		if (accountRepository.getByNumber(account.getNumber()) != null) {
			throw new AlreadyRegisteredAccountNumberException();
		}
		if (account.getBalance() < 0) {
			throw new NegativeBalanceException();
		}
		account.setUserEmail(user.getEmail());
		accountRepository.save(account);
		return new AccountDTO(account.getNumber(), account.getBalance(), new UserDTO(user.getEmail(), user.getName()));
	}
	
	public TransferDTO transfer(Transfer transfer) {
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
		return new TransferDTO(transfer.getAmount(), sourceAccount.getNumber(), destinationAccount.getNumber(), new UserDTO(user.getEmail(), user.getName()));
	}
	
	public BalanceDTO balance(Balance balance) {
		User user = getActualUser();
		if (!(accountRepository.getByNumber(balance.getAccount_number())).getUserEmail().equals(user.getEmail())) {
			throw new UserNotOwnerOfAccountException();
		}
		return new BalanceDTO(balance.getAccount_number(), (accountRepository.getByNumber(balance.getAccount_number())).getBalance());
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
