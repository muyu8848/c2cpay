package com.c2cpay.trade.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.constants.Constant;
import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.trade.domain.TradeOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MemberTradeOrderVO {

	private String id;

	private Double amount;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "HH:mm dd/MM/yyyy", timezone = "GMT+8")
	private Date createTime;

	private String tradeType;

	private String receiptPaymentType;
	
	private String receiptPaymentTypeName;

	private String sellerNickName;

	private String buyerNickName;

	public static List<MemberTradeOrderVO> convertFor(List<TradeOrder> pos, String memberId) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MemberTradeOrderVO> vos = new ArrayList<>();
		for (TradeOrder po : pos) {
			vos.add(convertFor(po, memberId));
		}
		return vos;
	}

	public static MemberTradeOrderVO convertFor(TradeOrder po, String memberId) {
		if (po == null) {
			return null;
		}
		MemberTradeOrderVO vo = new MemberTradeOrderVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getSeller() != null) {
			vo.setSellerNickName(po.getSeller().getNickName());
		}
		if (po.getBuyer() != null) {
			vo.setBuyerNickName(po.getBuyer().getNickName());
		}
		vo.setTradeType(po.getSellerId().equals(memberId) ? Constant.订单交易类型_出售 : Constant.订单交易类型_购买);
		vo.setStateName(DictHolder.getDictItemName("tradeOrderState", vo.getState()));
		vo.setReceiptPaymentTypeName(DictHolder.getDictItemName("receiptPaymentType", vo.getReceiptPaymentType()));
		return vo;
	}

}
