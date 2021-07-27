package com.lucasrodrigues.bankapi.utils;

public class ErrorDetails {

	private String error;

	public ErrorDetails(String error) {
		this.error = error;
	}

	public ErrorDetails() {
	}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
