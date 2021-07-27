package com.lucasrodrigues.bankapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucasrodrigues.bankapi.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Account getByNumber(String number);
}
