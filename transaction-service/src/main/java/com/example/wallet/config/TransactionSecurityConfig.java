package com.example.wallet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.wallet.CommonConstants;
import com.example.wallet.service.TransactionService;

@Configuration
public class TransactionSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	TransactionService transactionService;
	 
	@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.userDetailsService(transactionService);
	}
	
	@Override
	protected void configure(HttpSecurity http ) throws Exception {
		http.csrf().disable()
		.authorizeHttpRequests()
		.antMatchers("/transact/**").hasAuthority("user")
		.and().formLogin();
	}
}