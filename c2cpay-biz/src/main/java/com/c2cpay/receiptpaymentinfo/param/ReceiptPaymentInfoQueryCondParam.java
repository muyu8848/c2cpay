package com.c2cpay.receiptpaymentinfo.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceiptPaymentInfoQueryCondParam {

	@NotBlank
	private String memberId;

	private Boolean activated;

	private String type;

}
