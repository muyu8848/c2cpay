package com.c2cpay.transfer.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ReceiptAsynNoticeParam {

	/**
	 * 商户号
	 */
	@NotBlank
	private String merchantNum;

	/**
	 * 订单号
	 */
	@NotBlank
	private String orderNo;

	/**
	 * 平台订单号
	 */
	@NotBlank
	private String platformOrderNo;

	/**
	 * 金额
	 */
	@NotBlank
	private String amount;

	/**
	 * 状态 <br>1-支付成功 <br>0-支付失败
	 */
	@NotBlank
	private String state;

	/**
	 * 完结时间
	 */
	@NotBlank
	private String endTime;

	/**
	 * 签名,见上方签名规则
	 */
	private String sign;

}
