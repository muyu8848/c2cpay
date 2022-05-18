package com.c2cpay.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.merchant.domain.Merchant;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantVO {

	private String id;

	private String userName;

	private String merchantName;
	
	private String walletAddr;

	private Double balance;

	private String ipWhiteList;

	private String apiSecretKey;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	private String googleSecretKey;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date googleAuthBindTime;

	public static List<MerchantVO> convertFor(List<Merchant> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MerchantVO> vos = new ArrayList<>();
		for (Merchant po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MerchantVO convertFor(Merchant po) {
		if (po == null) {
			return null;
		}
		MerchantVO vo = new MerchantVO();
		BeanUtils.copyProperties(po, vo);
		vo.setStateName(DictHolder.getDictItemName("functionState", vo.getState()));
		return vo;
	}

}
