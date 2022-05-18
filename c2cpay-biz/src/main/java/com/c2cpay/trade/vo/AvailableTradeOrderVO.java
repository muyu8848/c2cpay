package com.c2cpay.trade.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.trade.domain.PreTradeOrder;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class AvailableTradeOrderVO {

	private String id;

	private String tradeType;

	private String receiptPaymentType;

	private Double minAmount;
	
	private Double maxAmount;

	private Double availableAmount;

	private String nickName;

	public static List<AvailableTradeOrderVO> convertFor(List<PreTradeOrder> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<AvailableTradeOrderVO> vos = new ArrayList<>();
		for (PreTradeOrder po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static AvailableTradeOrderVO convertFor(PreTradeOrder po) {
		if (po == null) {
			return null;
		}
		AvailableTradeOrderVO vo = new AvailableTradeOrderVO();
		BeanUtils.copyProperties(po, vo);
		vo.setNickName(po.getMember().getNickName());
		return vo;
	}

}
