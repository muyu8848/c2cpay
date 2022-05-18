package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CancelPreTradeOrderParam {
	
	@NotBlank
	private String memberId;
	
	@NotBlank
	private String preTradeOrderId;

}
