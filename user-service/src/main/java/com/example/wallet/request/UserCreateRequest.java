package com.example.wallet.request;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.example.wallet.UserIdentifier;
import com.example.wallet.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
	
	@NotBlank
	private String name;
	
	private String dob;
	
	@NotBlank
	private String phoneNumber;
	
	private String country;
	
	private String password;
	
	private String authorities;
	
	@NotBlank
	private String email;
	
	@NotBlank
	private UserIdentifier userIdentifier;
	
	@NotBlank
	private String identifierValue;

	public User toUser() {
		return User.builder()
				.authorities(authorities).country(country)
				.dob(dob).email(email).identifierValue(identifierValue)
				.name(name).password(password)
				.phoneNumber(phoneNumber).userIdentifier(userIdentifier)
				.build(); } 
}
