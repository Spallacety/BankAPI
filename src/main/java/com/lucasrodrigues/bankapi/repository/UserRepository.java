package com.lucasrodrigues.bankapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lucasrodrigues.bankapi.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User getByEmail(String email);
}
