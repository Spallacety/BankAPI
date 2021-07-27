package com.lucasrodrigues.bankapi.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class Transfer{
	
	@NotEmpty
	private String source_account_number;
	
	@NotEmpty
	private String destination_account_number;
	
	@Positive
	@NotNull
	private double amount;
	
	public Transfer(String source_account_number, String destination_account_number, double amount) {
		this.source_account_number = source_account_number;
		this.destination_account_number = destination_account_number;
		this.amount = amount;
	}

	public Transfer(){
	}
	
	public String getSource_account_number() {
		return source_account_number;
	}

	public String getDestination_account_number() {
		return destination_account_number;
	}

	public double getAmount() {
		return amount;
	}
	
}