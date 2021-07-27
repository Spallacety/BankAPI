package com.lucasrodrigues.bankapi.utils;

public class AccountDetails {
	
	private String number;
	private double balance;
	private UserDetails user;
	
	public AccountDetails(String number, double balance, UserDetails user) {
		super();
		this.number = number;
		this.balance = balance;
		this.user = user;
	}
	
	public String getNumber() {
		return number;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public UserDetails getUser() {
		return user;
	}
	

}
