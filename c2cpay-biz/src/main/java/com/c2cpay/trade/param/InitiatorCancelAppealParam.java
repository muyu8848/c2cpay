
package com.c2cpay.trade.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class InitiatorCancelAppealParam {

	@NotBlank
	private String initiatorId;

	@NotBlank
	private String tradeOrderId;

}
