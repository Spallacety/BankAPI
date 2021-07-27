package com.lucasrodrigues.bankapi.model;

import javax.validation.constraints.NotEmpty;

public class Balance{
	
	@NotEmpty
	private String account_number;
	
	public Balance(String account_number) {
		this.account_number = account_number;

	}

	public Balance(){
	}
	
	public String getAccount_number() {
		return account_number;
	}

	
}