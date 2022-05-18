package com.c2cpay.merchant.vo;

import org.springframework.beans.BeanUtils;

import com.c2cpay.merchant.domain.Merchant;

import lombok.Data;

@Data
public class AccountAuthInfoVO {

	private String id;

	private String userName;

	private String loginPwd;
	
	private String googleSecretKey;

	private String state;
	
	private String ipWhiteList;

	public static AccountAuthInfoVO convertFor(Merchant merchant) {
		if (merchant == null) {
			return null;
		}
		AccountAuthInfoVO vo = new AccountAuthInfoVO();
		BeanUtils.copyProperties(merchant, vo);
		return vo;
	}

}
