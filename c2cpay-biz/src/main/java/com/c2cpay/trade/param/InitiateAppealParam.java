package com.c2cpay.trade.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.constants.Constant;
import com.c2cpay.trade.domain.TradeAppealRecord;

import lombok.Data;

@Data
public class InitiateAppealParam {

	@NotBlank
	private String initiatorId;
	
	@NotBlank
	private String tradeOrderId;

	@NotBlank
	private String appealType;

	private String appealDesc;
	
	public TradeAppealRecord convertToPo(String defendantId) {
		TradeAppealRecord po = new TradeAppealRecord();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setState(Constant.申诉状态_待处理);
		po.setDefendantId(defendantId);
		return po;
	}

}
