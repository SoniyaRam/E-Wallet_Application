package com.example.wallet.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.wallet.enums.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String transactionId;
	
	//sender and receivers are usually user id's of
		// our application
	private String sender;
	
	private String receiver;
	
	private String purpose;
	
	private Double amount;
	
	@Enumerated(value = EnumType.STRING)
	private TransactionStatus transactionStatus;
	
	@CreationTimestamp
	private Date createOn;
	
	@UpdateTimestamp
	private Date updateOn;
}
