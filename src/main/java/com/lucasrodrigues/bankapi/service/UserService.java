package com.lucasrodrigues.bankapi.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lucasrodrigues.bankapi.dto.UserDTO;
import com.lucasrodrigues.bankapi.exception.AlreadyRegisteredEmailException;
import com.lucasrodrigues.bankapi.model.User;
import com.lucasrodrigues.bankapi.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserDTO save(User user) {
		if (userRepository.getByEmail(user.getEmail()) != null) {
			throw new AlreadyRegisteredEmailException();
		}
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		userRepository.save(user);
		return new UserDTO(user.getEmail(), user.getName());
	}
	
}
