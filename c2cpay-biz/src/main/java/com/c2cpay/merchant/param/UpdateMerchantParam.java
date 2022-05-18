package com.c2cpay.merchant.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpdateMerchantParam {

	@NotBlank
	private String id;

	@NotBlank
	private String userName;

	@NotBlank
	private String merchantName;

	@NotBlank
	private String apiSecretKey;

	@NotBlank
	private String state;

}
