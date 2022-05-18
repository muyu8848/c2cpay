package com.c2cpay.trade.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.constants.Constant;
import com.c2cpay.trade.domain.TradeOrder;

import lombok.Data;

@Data
public class BuyParam {
	
	@NotBlank
	private String buyerId;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double amount;
	
	@NotBlank
	private String preTradeOrderId;
	
	@NotBlank
	private String paymentInfoId;
	
	public TradeOrder convertToPo() {
		TradeOrder po = new TradeOrder();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setState(Constant.交易订单状态_待接单);
		return po;
	}

}
