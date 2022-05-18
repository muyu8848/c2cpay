package com.c2cpay.merchant.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ModifyLoginPwdParam {

	@NotBlank
	private String oldPwd;

	@NotBlank
	private String newPwd;

	@NotBlank
	private String merchantId;

}
