package com.c2cpay.transfer.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.transfer.domain.MemberReceiptRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ReceiptRecordVO {

	private String id;

	private String orderNo;

	private String transferAddr;
	
	private String bizType;
	
	private String bizTypeName;

	private Double amount;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String receiptAddr;

	private String receiptRealName;

	private String receiptMobile;

	public static List<ReceiptRecordVO> convertFor(List<MemberReceiptRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<ReceiptRecordVO> vos = new ArrayList<>();
		for (MemberReceiptRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static ReceiptRecordVO convertFor(MemberReceiptRecord po) {
		if (po == null) {
			return null;
		}
		ReceiptRecordVO vo = new ReceiptRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getReceiptAccount() != null) {
			vo.setReceiptAddr(po.getReceiptAccount().getWalletAddr());
			vo.setReceiptRealName(po.getReceiptAccount().getRealName());
			vo.setReceiptMobile(po.getReceiptAccount().getMobile());
		}
		vo.setBizTypeName(DictHolder.getDictItemName("transferBizType", vo.getBizType()));
		return vo;
	}

}
