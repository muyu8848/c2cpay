package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SellerConfirmOrderParam {
	
	@NotBlank
	private String tradeOrderId;
	
	@NotBlank
	private String sellerId;

}
