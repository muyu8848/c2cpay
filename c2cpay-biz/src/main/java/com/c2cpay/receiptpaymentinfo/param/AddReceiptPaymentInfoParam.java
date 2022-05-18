package com.c2cpay.receiptpaymentinfo.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.receiptpaymentinfo.domain.ReceiptPaymentInfo;

import lombok.Data;

@Data
public class AddReceiptPaymentInfoParam {

	@NotBlank
	private String memberId;

	@NotBlank
	private String type;

	private String cardNumber;

	private String bankName;

	private String account;

	private String qrcode;

	public ReceiptPaymentInfo convertToPo() {
		ReceiptPaymentInfo po = new ReceiptPaymentInfo();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setActivated(false);
		po.setDeletedFlag(false);
		return po;
	}

}
