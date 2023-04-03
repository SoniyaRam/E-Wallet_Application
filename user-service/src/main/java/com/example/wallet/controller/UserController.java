package com.example.wallet.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.wallet.model.User;
import com.example.wallet.request.UserCreateRequest;
import com.example.wallet.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class UserController {
	
	@Autowired
	UserService userService;
	
	@PostMapping("/user")
    public void createUser(@RequestBody UserCreateRequest userCreateRequest) throws JsonProcessingException {
        userService.create(userCreateRequest);
    }
	
	
	@GetMapping("/user")
	public User getUserDetails() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		return (User) userService.loadUserByUsername(user.getUsername());
	
	}
	
	
	@GetMapping("/admin/all/users")
	public List<User> getAllUsersDetails(){
		return userService.getAll();
	}
	
	
	@GetMapping("/admin/user/{userId}")
	public User getUserDetails(@PathVariable("userId") String userId) {
		return (User) userService.loadUserByUsername(userId);
	}
}
	
	
	