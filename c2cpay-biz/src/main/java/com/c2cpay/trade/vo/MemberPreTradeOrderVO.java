package com.c2cpay.trade.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.trade.domain.PreTradeOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.Data;

@Data
public class MemberPreTradeOrderVO {

	private String id;

	private Double amount;

	private Double minAmount;

	private Double maxAmount;

	private Double availableAmount;

	private Double completedAmount;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "HH:mm dd/MM/yyyy", timezone = "GMT+8")
	private Date createTime;

	private String tradeType;

	private String receiptPaymentType;

	public static List<MemberPreTradeOrderVO> convertFor(List<PreTradeOrder> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MemberPreTradeOrderVO> vos = new ArrayList<>();
		for (PreTradeOrder po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MemberPreTradeOrderVO convertFor(PreTradeOrder po) {
		if (po == null) {
			return null;
		}
		MemberPreTradeOrderVO vo = new MemberPreTradeOrderVO();
		BeanUtils.copyProperties(po, vo);
		vo.setStateName(DictHolder.getDictItemName("preTradeOrderState", vo.getState()));
		vo.setCompletedAmount(NumberUtil.round(vo.getAmount() - vo.getAvailableAmount(), 2).doubleValue());
		return vo;
	}

}
