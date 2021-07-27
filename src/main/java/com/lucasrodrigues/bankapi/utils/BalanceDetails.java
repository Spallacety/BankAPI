package com.lucasrodrigues.bankapi.utils;

public class BalanceDetails {
	
	private String account_number;
	private double balance;
	
	public BalanceDetails(String account_number, double balance) {
		super();
		this.account_number = account_number;
		this.balance = balance;
	}
	
	public String getAccount_number() {
		return account_number;
	}
	
	public double getBalance() {
		return balance;
	}

	

}
