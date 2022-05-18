package com.c2cpay.member.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ModifyPayPwdParam {
	
	@NotBlank
	private String verificationCode;

	@NotBlank
	private String newPwd;

	@NotBlank
	private String memberId;

}
