package com.lucasrodrigues.bankapi.dto;

public class MessageDTO {

	private String message;

	public MessageDTO(String message) {
		super();
		this.message = message;
	}

	public MessageDTO() {
		super();
	}

	public String getMessage() {
		return message;
	}

}
