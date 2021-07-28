package com.lucasrodrigues.bankapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.lucasrodrigues.bankapi.exception.UserNotFoundException;
import com.lucasrodrigues.bankapi.model.User;
import com.lucasrodrigues.bankapi.repository.UserRepository;

@Component
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService{

	private final UserRepository userRepository;
	
	@Autowired
	public UserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getByEmail(username);
		if (user == null) {
			throw new UserNotFoundException();
		}
		List<GrantedAuthority> authorityListUser = AuthorityUtils.createAuthorityList("ROLE_USER");
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorityListUser);	
	}

}
