package com.c2cpay.transfer.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PreReceiptParam {

	@NotBlank
	private String receiptAccountId;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double amount;

	private String merchantOrderNo;

	private String notifyUrl;
	
	@NotBlank
	private String payPwd;

}
