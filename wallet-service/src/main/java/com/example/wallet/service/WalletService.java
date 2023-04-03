package com.example.wallet.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.wallet.CommonConstants;
import com.example.wallet.UserIdentifier;
import com.example.wallet.WalletUpdateStatus;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WalletService {
	
	@Autowired
	WalletRepository walletrepositorty ;
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate ;
	
	@Autowired
	ObjectMapper objectMapper;

	
	public static Logger logger = LoggerFactory.getLogger(WalletService.class);
	
	@KafkaListener(topics = CommonConstants.USER_CREATION_TOPIC, groupId = "Ewallet_Group")
	public void createWallet(String msg) throws ParseException {
		
		//publish the event post user creation which will be listened by consumers
		JSONObject data = (JSONObject) new JSONParser().parse(msg);
		
		long userId = (long) data.get(CommonConstants.USER_CREATION_TOPIC_USER_ID);
		String phoneNumber = (String) data.get(CommonConstants.USER_CREATION_TOPIC_PHONE_NUMBER);
		String identifierKey = (String) data.get(CommonConstants.USER_CREATION_IDENTIFIER_KEY);
		String identifierValue = (String) data.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_VALUE);
		
		Wallet wallet = Wallet.builder()
				.userId(userId)
				.phoneNumber(phoneNumber)
				.userIdentifier(UserIdentifier.valueOf(identifierKey))
				.identifierValue(identifierValue)
				.balance(10.0)
				.build();
		walletrepositorty.save(wallet);
	}
	
	@KafkaListener(topics = CommonConstants.TRANSACTION_CREATION_TOPIC, groupId = "Ewallet_Group")
	public void updateWalletforTransaction(String msg) throws ParseException, JsonProcessingException {
		
		//publish the event post user creation which will be listened by consumers
		JSONObject data = (JSONObject) new JSONParser().parse(msg);
		
		String sender = (String) data.get("sender");
		String receiver = (String) data.get("receiver");
		Double amount = (Double) data.get("amount");
		String transactionId = (String) data.get("transactionId");

logger.info("Validating sender's Wallet Balance : sender - {},receiver - {},amount - {},transactionId - {}"
		,sender,receiver,amount,transactionId);

		Wallet senderWallet = walletrepositorty.findByPhoneNumber(sender);
		Wallet receiverWallet = walletrepositorty.findByPhoneNumber(receiver);
		
		 
		//publish the event post after validating and updating the wallets of sender and receiver
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sender",sender);
			jsonObject.put("receiver",receiver);
			jsonObject.put("amount",amount);
			jsonObject.put("transactionId",transactionId);
			
			if(senderWallet == null || receiverWallet == null || receiverWallet.getBalance() <= amount ) {
				jsonObject.put("walletUpdateStatus",WalletUpdateStatus.FAILED);
			}
			walletrepositorty.updateWallet(sender, 0-amount);
			walletrepositorty.updateWallet(receiver, amount);
			
			jsonObject.put("walletUpdateStatus",WalletUpdateStatus.SUCCESS);
			
			kafkaTemplate.send(CommonConstants.WALLET_UPDATED_TOPIC,
			 objectMapper.writeValueAsString(jsonObject));
				

	}
}
