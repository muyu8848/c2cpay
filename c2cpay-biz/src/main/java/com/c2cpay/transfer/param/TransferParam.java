package com.c2cpay.transfer.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TransferParam {

	@NotBlank
	private String transferAccountId;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double amount;

	@NotBlank
	private String receiptAddr;
	
	@NotBlank
	private String payPwd;

}
