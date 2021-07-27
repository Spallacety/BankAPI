package com.lucasrodrigues.bankapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="accounts")
public class Account{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    private String userEmail;
	
	@NotEmpty
	@Column(unique = true)
    private String number;
	
	@NotNull
    private double balance;

	public Account(Long id, String userEmail, String number, double balance) {
		super();
		this.id = id;
		this.userEmail = userEmail;
		this.number = number;
		this.balance = balance;
	}

	public Account(String userEmail, String number, double balance) {
		super();
		this.userEmail = userEmail;
		this.number = number;
		this.balance = balance;
	}

	public Account() {
	}
	
	public void addBalance(double amount) {
		this.balance += amount;
	}
	
	public void removeBalance(double amount) {
		this.balance -= amount;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

}