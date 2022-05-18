package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class BuyerCancelTradeParam {
	
	@NotBlank
	private String buyerId;
	
	@NotBlank
	private String tradeOrderId;

}
