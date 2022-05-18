package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SellerRejectOrderParam {
	
	@NotBlank
	private String sellerId;
	
	@NotBlank
	private String tradeOrderId;

}
