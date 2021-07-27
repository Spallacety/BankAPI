package com.lucasrodrigues.bankapi.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lucasrodrigues.bankapi.exception.AlreadyRegisteredEmailException;
import com.lucasrodrigues.bankapi.exception.UserNotFoundException;
import com.lucasrodrigues.bankapi.model.User;
import com.lucasrodrigues.bankapi.repository.UserRepository;
import com.lucasrodrigues.bankapi.utils.UserDetails;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@GetMapping
	public ResponseEntity<?> getAllUsers() {
		return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		verifyIfExists(id);
		return new ResponseEntity<>(userRepository.findById(id).get(), HttpStatus.OK);
	}

	@PostMapping
	@Transactional
	public ResponseEntity<?> saveUser(@Valid @RequestBody User user) {
		if (userRepository.getByEmail(user.getEmail()) != null) {
			throw new AlreadyRegisteredEmailException();
		}
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
		userRepository.save(user);
		return new ResponseEntity<>(new UserDetails(user.getEmail(), user.getName()), HttpStatus.CREATED);
	}

	public void verifyIfExists(Long id) {
		if (!userRepository.findById(id).isPresent()) {
			throw new UserNotFoundException();
		}
	}
	
}
