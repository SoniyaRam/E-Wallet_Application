package com.example.wallet.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.wallet.model.Wallet;

@Transactional
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
	
	Wallet findByPhoneNumber(String phoneNumber);
	 
	@Modifying
	@Query("update Wallet w set w.balance = w.balance+ ?2 where phoneNumber = ?1")
	void updateWallet(String phoneNumber, Double amount);

}
