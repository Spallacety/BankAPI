package com.lucasrodrigues.bankapi.dto;

public class AccountDTO {
	
	private String number;
	private double balance;
	private UserDTO user;
	
	public AccountDTO(String number, double balance, UserDTO user) {
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
	
	public UserDTO getUser() {
		return user;
	}
	

}
