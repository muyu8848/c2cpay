package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AppealProcessParam {
	
	@NotBlank
	private String tradeOrderId;
	
	@NotBlank
	private String processWay;
	
	@NotNull
	private Boolean approveAppeal;

}
