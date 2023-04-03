package com.example.wallet.service;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.wallet.CommonConstants;
import com.example.wallet.constants.UserConstants;
import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.request.UserCreateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	UserRepository 	userRepository ;
	
	@Autowired
	PasswordEncoder passwordEncoder ;
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate ;
	
	@Autowired
	ObjectMapper objectMapper;

	@Override
	public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
		return 	userRepository.findByPhoneNumber(phoneNumber);
	}
	
	public void create(UserCreateRequest userCreateRequest ) throws JsonProcessingException {
		User user = userCreateRequest.toUser();
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setAuthorities(UserConstants.SERVICE_AUTHORITY);
		user = userRepository.save(user);
	
	//publish the event post user creation which will be listen by consumer
		
	JSONObject jsonObject = new JSONObject();
	jsonObject.put(CommonConstants.USER_CREATION_TOPIC_USER_ID, user.getId());
	jsonObject.put(CommonConstants.USER_CREATION_TOPIC_PHONE_NUMBER, user.getPhoneNumber());
	jsonObject.put(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_VALUE, user.getIdentifierValue());
	jsonObject.put(CommonConstants.USER_CREATION_IDENTIFIER_KEY, user.getUserIdentifier());
	
	kafkaTemplate.send(CommonConstants.USER_CREATION_TOPIC,
			objectMapper.writeValueAsString(jsonObject));
}
	public List<User> getAll(){
		return userRepository.findAll();
	}
	private String encryptpassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}
	}