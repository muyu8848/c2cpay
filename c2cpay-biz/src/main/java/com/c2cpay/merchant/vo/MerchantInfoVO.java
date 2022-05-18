package com.c2cpay.merchant.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.c2cpay.merchant.domain.Merchant;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MerchantInfoVO {

	private String id;

	private String userName;

	private String merchantName;

	private String walletAddr;

	private String apiSecretKey;

	private String ipWhiteList;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date googleAuthBindTime;

	public static MerchantInfoVO convertFor(Merchant merchant) {
		if (merchant == null) {
			return null;
		}
		MerchantInfoVO vo = new MerchantInfoVO();
		BeanUtils.copyProperties(merchant, vo);
		return vo;
	}

}
