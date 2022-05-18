package com.c2cpay.trade.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.receiptpaymentinfo.domain.ReceiptPaymentInfo;
import com.c2cpay.receiptpaymentinfo.vo.ReceiptPaymentInfoVO;
import com.c2cpay.trade.domain.TradeOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MemberTradeOrderDetailVO {

	private String id;

	private String orderNo;

	private Double amount;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date orderDeadline;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date tradeDeadline;

	private String paymentCertificate;

	private ReceiptPaymentInfoVO paymentInfo;

	private ReceiptPaymentInfoVO receiptInfo;
	
	private String sellerId;

	private String sellerNickName;
	
	private String buyerId;

	private String buyerNickName;

	private String appealType;

	private String appealTypeName;

	private String appealDesc;

	private String appealInitiatorId;

	public static MemberTradeOrderDetailVO convertFor(TradeOrder po) {
		if (po == null) {
			return null;
		}
		MemberTradeOrderDetailVO vo = new MemberTradeOrderDetailVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getSeller() != null) {
			vo.setSellerNickName(po.getSeller().getNickName());
		}
		if (po.getBuyer() != null) {
			vo.setBuyerNickName(po.getBuyer().getNickName());
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
		return vo;
	}

}
