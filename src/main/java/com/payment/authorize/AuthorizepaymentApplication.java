package com.payment.authorize;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.authorize.Environment;
import net.authorize.api.contract.v1.ANetApiResponse;
import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CreditCardType;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.PaymentType;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;

@SpringBootApplication
public class AuthorizepaymentApplication implements CommandLineRunner {
	

	public static void main(String[] args) {
		SpringApplication.run(AuthorizepaymentApplication.class, args);
		
	
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		ANetApiResponse response=run("34s9R4XwY","6ddt5EK3669GEGNW",20.00,"0");
		
		
	}

	 public static ANetApiResponse run(String apiLoginId, String transactionKey, Double transactionAmount, String transactionID) {
	        
	        //Common code to set for all requests
	        ApiOperationBase.setEnvironment(Environment.SANDBOX);

	        MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
	        merchantAuthenticationType.setName(apiLoginId);
	        merchantAuthenticationType.setTransactionKey(transactionKey);
	        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);

	        // Create a payment object, last 4 of the credit card and expiration date are required
	        PaymentType paymentType = new PaymentType();
	        CreditCardType creditCard = new CreditCardType();
	        creditCard.setCardNumber("1111");
	        creditCard.setExpirationDate("12/21");
	        paymentType.setCreditCard(creditCard);

	        // Create the payment transaction request
	        TransactionRequestType txnRequest = new TransactionRequestType();
	        txnRequest.setTransactionType(TransactionTypeEnum.REFUND_TRANSACTION.value());
	        txnRequest.setRefTransId(transactionID);
	        txnRequest.setAmount(new BigDecimal(transactionAmount.toString()));
	        txnRequest.setPayment(paymentType);

	        // Make the API Request
	        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
	        apiRequest.setTransactionRequest(txnRequest);
	        CreateTransactionController controller = new CreateTransactionController(apiRequest);
	        controller.execute(); 

	        CreateTransactionResponse response = controller.getApiResponse();

	        if (response!=null) {
	        	// If API Response is ok, go ahead and check the transaction response
	        	if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
	        		TransactionResponse result = response.getTransactionResponse();
	        		if (result.getMessages() != null) {
	        			System.out.println("Successfully created transaction with Transaction ID: " + result.getTransId());
	        			System.out.println("Response Code: " + result.getResponseCode());
	        			System.out.println("Message Code: " + result.getMessages().getMessage().get(0).getCode());
	        			System.out.println("Description: " + result.getMessages().getMessage().get(0).getDescription());
	        			System.out.println("Auth Code: " + result.getAuthCode());
	                } else {
	        			System.out.println("Failed Transaction.");
	        			if (response.getTransactionResponse().getErrors() != null) {
	        				System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
	        				System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
	        			}
	        		}
	            } else {
	        		System.out.println("Failed Transaction.");
	        		if (response.getTransactionResponse() != null && response.getTransactionResponse().getErrors() != null) {
	        			System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
	        			System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
	                } else {
	        			System.out.println("Error Code: " + response.getMessages().getMessage().get(0).getCode());
	        			System.out.println("Error message: " + response.getMessages().getMessage().get(0).getText());
	        		}
	        	}
	        } else {
	        	System.out.println("Null Response.");
	        }
	        
			return response;

	    }
}
