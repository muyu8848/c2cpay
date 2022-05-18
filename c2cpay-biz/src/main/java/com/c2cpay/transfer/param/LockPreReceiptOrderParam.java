package com.c2cpay.transfer.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LockPreReceiptOrderParam {
	
	@NotBlank
	private String transferAccountId;
	
	@NotBlank
	private String orderNo;

}
