package com.lucasrodrigues.bankapi.utils;

public class UserDetails {
	
	private String email;
	private String name;
	
	public UserDetails(String email, String name) {
		this.email = email;
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getName() {
		return name;
	}
	
}
