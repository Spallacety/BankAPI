package com.lucasrodrigues.bankapi.dto;

public class BalanceDTO {

	private String account_number;
	private double balance;

	public BalanceDTO(String account_number, double balance) {
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
