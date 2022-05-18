package com.c2cpay.transfer.vo;

import lombok.Data;

@Data
public class CreatePreReceiptReturnVO {

	/**
	 * 平台订单号
	 */
	private String platformOrderNo;

	/**
	 * 支付页面地址
	 */
	private String payUrl;

	public static CreatePreReceiptReturnVO build(String platformOrderNo, String payUrl) {
		CreatePreReceiptReturnVO vo = new CreatePreReceiptReturnVO();
		vo.setPlatformOrderNo(platformOrderNo);
		vo.setPayUrl(payUrl);
		return vo;
	}

}
