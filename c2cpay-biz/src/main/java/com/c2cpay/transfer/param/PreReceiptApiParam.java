package com.c2cpay.transfer.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PreReceiptApiParam {

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
	 * 异步通知地址
	 */
	@NotBlank
	private String notifyUrl;

	/**
	 * 签名,见上方签名规则
	 */
	@NotBlank
	private String sign;

}
