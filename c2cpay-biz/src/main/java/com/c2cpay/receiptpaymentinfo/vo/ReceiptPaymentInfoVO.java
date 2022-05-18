package com.c2cpay.receiptpaymentinfo.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.receiptpaymentinfo.domain.ReceiptPaymentInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ReceiptPaymentInfoVO {

	private String id;

	private String realName;

	private String type;

	private String typeName;

	private Boolean activated;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date activatedTime;

	private String cardNumber;

	private String bankName;

	private String account;

	private String qrcode;

	public static List<ReceiptPaymentInfoVO> convertFor(List<ReceiptPaymentInfo> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<ReceiptPaymentInfoVO> vos = new ArrayList<>();
		for (ReceiptPaymentInfo po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static ReceiptPaymentInfoVO convertFor(ReceiptPaymentInfo po) {
		if (po == null) {
			return null;
		}
		ReceiptPaymentInfoVO vo = new ReceiptPaymentInfoVO();
		BeanUtils.copyProperties(po, vo);
		vo.setTypeName(DictHolder.getDictItemName("receiptPaymentType", vo.getType()));
		return vo;
	}

}
