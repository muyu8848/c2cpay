package com.c2cpay.member.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginParam {

	@NotBlank
	private String mobile;

	@NotBlank
	private String loginPwd;

}
