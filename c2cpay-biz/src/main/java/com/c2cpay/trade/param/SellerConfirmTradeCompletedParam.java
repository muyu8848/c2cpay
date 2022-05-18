package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SellerConfirmTradeCompletedParam {

	@NotBlank
	private String sellerId;

	private String tradeOrderId;
	
	private String payPwd;

}
