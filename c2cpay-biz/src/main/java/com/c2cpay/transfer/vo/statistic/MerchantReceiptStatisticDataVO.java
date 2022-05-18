package com.c2cpay.transfer.vo.statistic;

import lombok.Data;

@Data
public class MerchantReceiptStatisticDataVO {

	private Double todayAmount = 0d;

	private Integer todayCount = 0;

	private Double yesterdayAmount = 0d;

	private Double totalAmount = 0d;

}
