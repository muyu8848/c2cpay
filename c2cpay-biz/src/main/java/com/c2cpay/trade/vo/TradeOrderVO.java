package com.c2cpay.trade.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.receiptpaymentinfo.domain.ReceiptPaymentInfo;
import com.c2cpay.receiptpaymentinfo.vo.ReceiptPaymentInfoVO;
import com.c2cpay.trade.domain.TradeOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class TradeOrderVO {

	private String id;

	private String orderNo;

	private Double amount;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date endTime;

	private String receiptPaymentType;

	private String receiptPaymentTypeName;

	private String paymentCertificate;

	private ReceiptPaymentInfoVO paymentInfo;

	private ReceiptPaymentInfoVO receiptInfo;

	private String sellerId;

	private String sellerNickName;

	private String sellerRealName;

	private String sellerMobile;

	private String buyerId;

	private String buyerNickName;

	private String buyerRealName;

	private String buyerMobile;

	private String appealType;

	private String appealTypeName;

	private String appealDesc;

	private String appealInitiatorId;

	public static List<TradeOrderVO> convertFor(List<TradeOrder> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<TradeOrderVO> vos = new ArrayList<>();
		for (TradeOrder po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static TradeOrderVO convertFor(TradeOrder po) {
		if (po == null) {
			return null;
		}
		TradeOrderVO vo = new TradeOrderVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getSeller() != null) {
			vo.setSellerNickName(po.getSeller().getNickName());
			vo.setSellerRealName(po.getSeller().getRealName());
			vo.setSellerMobile(po.getSeller().getMobile());
		}
		if (po.getBuyer() != null) {
			vo.setBuyerNickName(po.getBuyer().getNickName());
			vo.setBuyerRealName(po.getBuyer().getRealName());
			vo.setBuyerMobile(po.getBuyer().getMobile());
		}
		ReceiptPaymentInfo receiptInfo = po.getReceiptInfo();
		if (receiptInfo != null) {
			vo.setReceiptInfo(ReceiptPaymentInfoVO.convertFor(receiptInfo));
		}
		ReceiptPaymentInfo paymentInfo = po.getPaymentInfo();
		if (paymentInfo != null) {
			vo.setPaymentInfo(ReceiptPaymentInfoVO.convertFor(paymentInfo));
		}
		if (po.getAppealRecord() != null) {
			vo.setAppealInitiatorId(po.getAppealRecord().getInitiatorId());
			vo.setAppealType(po.getAppealRecord().getAppealType());
			vo.setAppealDesc(po.getAppealRecord().getAppealDesc());
			vo.setAppealTypeName(DictHolder.getDictItemName("tradeAppealType", vo.getAppealType()));
		}
		vo.setStateName(DictHolder.getDictItemName("tradeOrderState", vo.getState()));
		vo.setReceiptPaymentTypeName(DictHolder.getDictItemName("receiptPaymentType", vo.getReceiptPaymentType()));
		return vo;
	}

}
