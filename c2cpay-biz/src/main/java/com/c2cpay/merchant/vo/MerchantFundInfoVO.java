package com.c2cpay.merchant.vo;

import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.merchant.domain.Merchant;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;

@Data
public class MerchantFundInfoVO {

	private Double balance = 0d;

	private Double freezeFund = 0d;

	private Double totalFund = 0d;

	public static MerchantFundInfoVO convertFor(Merchant po) {
		if (po == null) {
			return null;
		}
		MerchantFundInfoVO vo = new MerchantFundInfoVO();
		BeanUtils.copyProperties(po, vo);
		vo.setTotalFund(NumberUtil.round(vo.getBalance() + vo.getFreezeFund(), 2).doubleValue());
		return vo;
	}

	public static MerchantFundInfoVO convertFor(List<Merchant> pos) {
		MerchantFundInfoVO vo = new MerchantFundInfoVO();
		for (Merchant po : pos) {
			vo.setBalance(NumberUtil.round(vo.getBalance() + po.getBalance(), 2).doubleValue());
			vo.setFreezeFund(NumberUtil.round(vo.getFreezeFund() + 0, 2).doubleValue());
		}
		vo.setTotalFund(NumberUtil.round(vo.getBalance() + vo.getFreezeFund(), 2).doubleValue());
		return vo;
	}

}
