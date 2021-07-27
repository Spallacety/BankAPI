package com.lucasrodrigues.bankapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.lucasrodrigues.bankapi.handler.ExceptionHandlerFilter;
import com.lucasrodrigues.bankapi.repository.UserRepository;
import com.lucasrodrigues.bankapi.service.CustomUserDetailService;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private CustomUserDetailService customUserDetailService;
	@Autowired
	private UserRepository userRepository;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager(), userRepository);
		jwtAuthenticationFilter.setFilterProcessesUrl("/auth");
		
		http.cors().and().csrf().disable().authorizeRequests().antMatchers("/users").permitAll().and().authorizeRequests().antMatchers("/*").permitAll().anyRequest().authenticated().and().addFilterBefore(new ExceptionHandlerFilter(), BasicAuthenticationFilter.class).addFilter(jwtAuthenticationFilter).addFilter(new JWTAuthorizationFilter(authenticationManager(), customUserDetailService));
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
}
