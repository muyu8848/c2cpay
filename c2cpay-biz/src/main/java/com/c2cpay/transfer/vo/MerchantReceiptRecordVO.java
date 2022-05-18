package com.c2cpay.transfer.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.transfer.domain.MerchantReceiptRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantReceiptRecordVO {

	private String id;

	private String orderNo;

	private String merchantOrderNo;

	private String payUrl;

	private String notifyUrl;

	private String noticeState;

	private String noticeStateName;

	private String transferAddr;

	private Double amount;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lockTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date endTime;

	private String receiptAddr;

	private String receiptUserName;

	private String receiptMerchantName;

	public static List<MerchantReceiptRecordVO> convertFor(List<MerchantReceiptRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MerchantReceiptRecordVO> vos = new ArrayList<>();
		for (MerchantReceiptRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MerchantReceiptRecordVO convertFor(MerchantReceiptRecord po) {
		if (po == null) {
			return null;
		}
		MerchantReceiptRecordVO vo = new MerchantReceiptRecordVO();
		BeanUtils.copyProperties(po, vo);
		vo.setPayUrl(po.formatPayUrl());
		if (po.getReceiptAccount() != null) {
			vo.setReceiptAddr(po.getReceiptAccount().getWalletAddr());
			vo.setReceiptUserName(po.getReceiptAccount().getUserName());
			vo.setReceiptMerchantName(po.getReceiptAccount().getMerchantName());
		}
		vo.setStateName(DictHolder.getDictItemName("merchantReceiptRecordState", vo.getState()));
		vo.setNoticeStateName(DictHolder.getDictItemName("noticeState", vo.getNoticeState()));
		return vo;
	}

}
