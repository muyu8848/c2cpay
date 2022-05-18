package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class BuyerMarkPaidParam {
	
	@NotBlank
	private String buyerId;
	
	@NotBlank
	private String tradeOrderId;
	
	private String paymentCertificate;

}
