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
public class PreTradeOrderVO {

	private String id;

	private String orderNo;

	private Double amount;

	private Double minAmount;

	private Double maxAmount;

	private Double availableAmount;

	private Double completedAmount;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String tradeType;

	private String tradeTypeName;

	private String receiptPaymentType;

	private String memberNickName;

	private String memberRealName;

	private String memberMobile;

	public static List<PreTradeOrderVO> convertFor(List<PreTradeOrder> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<PreTradeOrderVO> vos = new ArrayList<>();
		for (PreTradeOrder po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static PreTradeOrderVO convertFor(PreTradeOrder po) {
		if (po == null) {
			return null;
		}
		PreTradeOrderVO vo = new PreTradeOrderVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getMember() != null) {
			vo.setMemberNickName(po.getMember().getNickName());
			vo.setMemberRealName(po.getMember().getRealName());
			vo.setMemberMobile(po.getMember().getMobile());
		}
		vo.setStateName(DictHolder.getDictItemName("preTradeOrderState", vo.getState()));
		vo.setTradeTypeName(DictHolder.getDictItemName("tradeType", vo.getTradeType()));
		vo.setCompletedAmount(NumberUtil.round(vo.getAmount() - vo.getAvailableAmount(), 2).doubleValue());
		return vo;
	}

}
