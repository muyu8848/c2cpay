package com.c2cpay.transfer.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class MerchantTransferApiParam {

	/**
	 * 商户号
	 */
	@NotBlank
	private String merchantNum;

	/**
	 * 商户订单号
	 */
	@NotBlank
	private String orderNo;

	/**
	 * 金额
	 */
	@NotBlank
	private String amount;

	/**
	 * 收款地址
	 */
	@NotBlank
	private String receiptAddr;

	/**
	 * 签名,见上方签名规则
	 */
	@NotBlank
	private String sign;

}
