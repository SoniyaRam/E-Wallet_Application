package com.example.wallet.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.wallet.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	User findByPhoneNumber(String phoneNumber);

}
