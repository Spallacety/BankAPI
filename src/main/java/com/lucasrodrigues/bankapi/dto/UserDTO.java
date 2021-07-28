package com.lucasrodrigues.bankapi.dto;

public class UserDTO {
	
	private String email;
	private String name;
	
	public UserDTO(String email, String name) {
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
