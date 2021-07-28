package com.lucasrodrigues.bankapi.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasrodrigues.bankapi.dto.UserDTO;
import com.lucasrodrigues.bankapi.exception.AlreadyRegisteredEmailException;
import com.lucasrodrigues.bankapi.model.User;
import com.lucasrodrigues.bankapi.repository.UserRepository;

@RestController
@RequestMapping("/users")
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
