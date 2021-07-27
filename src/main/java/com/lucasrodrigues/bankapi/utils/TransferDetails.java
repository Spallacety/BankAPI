package com.lucasrodrigues.bankapi.utils;

public class TransferDetails {
	
	private double amount;
	private String source_account_number;
	private String destination_account_number;
	private UserDetails user_transfer;
	
	public TransferDetails(double amount, String source_account_number, String destination_account_number,	UserDetails user_transfer) {
		this.amount = amount;
		this.source_account_number = source_account_number;
		this.destination_account_number = destination_account_number;
		this.user_transfer = user_transfer;
	}

	public double getAmount() {
		return amount;
	}

	public String getSource_account_number() {
		return source_account_number;
	}

	public String getDestination_account_number() {
		return destination_account_number;
	}

	public UserDetails getUser_transfer() {
		return user_transfer;
	}
	
}
