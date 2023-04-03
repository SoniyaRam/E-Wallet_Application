package com.example.wallet.model;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.wallet.constants.UserConstants;
import com.example.wallet.UserIdentifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User  implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	private String email ;
	
	private String dob;
	
	@Column(unique = true)
	private String phoneNumber;
	
	private String country;
	
	private String password;
	
	private String authorities;
	
	@Enumerated(value = EnumType.STRING)
	private UserIdentifier userIdentifier;
	
	@Column(unique = true)
	private String identifierValue;
	
	@CreationTimestamp
	private Date createOn;
	
	@UpdateTimestamp
	private Date updateOn;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String[] allAuthorities = this.authorities.split(UserConstants.AUTHORITY_DELIMITER);
		return Arrays.stream(allAuthorities)
				.map(x-> new SimpleGrantedAuthority(x))
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.phoneNumber;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}