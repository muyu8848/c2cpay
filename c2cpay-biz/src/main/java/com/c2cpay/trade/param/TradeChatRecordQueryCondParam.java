package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TradeChatRecordQueryCondParam {

	private String memberId;

	@NotBlank
	private String tradeOrderId;

	private Long lastTimeStamp;

}
