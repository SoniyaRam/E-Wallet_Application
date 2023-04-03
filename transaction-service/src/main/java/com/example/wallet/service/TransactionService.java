package com.example.wallet.service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.common.Uuid;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.wallet.CommonConstants;
import com.example.wallet.enums.TransactionStatus;
import com.example.wallet.WalletUpdateStatus;
import com.example.wallet.model.Transaction;
import com.example.wallet.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TransactionService implements UserDetailsService{

	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	KafkaTemplate<String, String> kafkaTemplate ;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired(required=true)
	RestTemplate restTemplate;
	
	private static Logger logger = LoggerFactory.getLogger(TransactionService.class);
	
	public String initateTransaction(String sender, String receiver, String purpose, Double amount) throws JsonProcessingException {
		
		Transaction transaction = Transaction.builder()
							.sender(sender)
							.receiver(receiver)
							.purpose(purpose).transactionId(Uuid.randomUuid().toString())
							.amount(amount)
							.transactionStatus(TransactionStatus.PENDING)
							.build();
		transactionRepository.save(transaction );
		 
		//publish the event post after initiating the Transaction which will be listen by consumer
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sender",sender);
			jsonObject.put("receiver",receiver);
			jsonObject.put("amount",amount);
			jsonObject.put("transactionId",transaction.getTransactionId());
		 
			kafkaTemplate.send(CommonConstants.TRANSACTION_CREATION_TOPIC,
					 objectMapper.writeValueAsString(jsonObject));
			
			return transaction.getTransactionId();
	}
	
	@KafkaListener(topics = CommonConstants.WALLET_UPDATED_TOPIC, groupId = "Ewallet_Group")
	public void updateTransaction(String msg) throws ParseException, JsonProcessingException {
		
		//publish the event post user creation which will be listened by consumers
		JSONObject data = (JSONObject) new JSONParser().parse(msg);
		
		String sender = (String) data.get("sender");
		String receiver = (String) data.get("receiver");
		Double amount = (Double) data.get("amount");
		String transactionId = (String) data.get("transactionId");
		WalletUpdateStatus walletUpdateStatus = (WalletUpdateStatus) data.get("transactionId");
		
		JSONObject senderObj = getUserFromUserService(sender);
		
		if(walletUpdateStatus == walletUpdateStatus.SUCCESS) {
			transactionRepository.updateTransaction(transactionId,TransactionStatus.SUCCESS);
		}else {
			transactionRepository.updateTransaction(transactionId,TransactionStatus.FAILED);
		}
		
		String senderMsg = "Hi,Your Transaction with Id"+transactionId+"got"+ walletUpdateStatus;
		
		JSONObject senderEmailObj = new JSONObject();
		
		senderEmailObj.put("sender", sender);
		 
		//publish the event post after validating and updating the wallets of sender and receiver
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("sender",sender);
			jsonObject.put("receiver",receiver);
			jsonObject.put("amount",amount);
			jsonObject.put("transactionId",transactionId);
			jsonObject.put("walletUpdateStatus",walletUpdateStatus);
			
		kafkaTemplate.send(CommonConstants.TRANSACTION_COMPLETED_TOPIC,
							objectMapper.writeValueAsString(jsonObject));
	}
	private JSONObject getUserFromUserService(String username) {
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBasicAuth("txn", "txn123");
		HttpEntity request= new HttpEntity(httpHeaders);
		
		return restTemplate.exchange("http://localhost:6000/admin/user"+username,
									  HttpMethod.GET,request,JSONObject.class).getBody();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		JSONObject requestedUser = getUserFromUserService(username);
		
		List<GrantedAuthority> authorities ;
		
		List<LinkedHashMap<String, String>> requestAuthorities =(List<LinkedHashMap<String, String>>)requestedUser.get("authorities");
		
		authorities = requestAuthorities.stream()
										.map(x -> x.get("authority"))
										.map(x -> new SimpleGrantedAuthority(x)).collect(Collectors.toList());
		
		return new User((String)requestedUser.get("username"), 
						(String)requestedUser.get("password"), authorities) ;
	}
}
