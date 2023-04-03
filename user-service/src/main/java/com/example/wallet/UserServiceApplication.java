package com.example.wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.wallet.model.User;
import com.example.wallet.repository.UserRepository;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {
	
	@Autowired
	UserRepository userRepository ;
	
	@Autowired
	PasswordEncoder passwordEncoder ;

		public static void main(String[] args) {
			SpringApplication.run(UserServiceApplication.class, args);
		}

		@Override
		public void run(String... args) throws Exception {
//			User txnServiceUser = User.builder()
//					.password(passwordEncoder.encode("txn123"))
//					.phoneNumber("txn_service")
//					.identifierValue("txn123")
//					.userIdentifier(UserIdentifier.SERVICE_ID)
//					.email("txn@gmail.com")
//					.authorities(CommonConstants.SERVICE_AUTHORITY)
//					.build();
//			userRepository.save(txnServiceUser);
		}

}
