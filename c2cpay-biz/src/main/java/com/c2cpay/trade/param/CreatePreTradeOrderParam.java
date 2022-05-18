package com.c2cpay.trade.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.constants.Constant;
import com.c2cpay.trade.domain.PreTradeOrder;

import lombok.Data;

@Data
public class CreatePreTradeOrderParam {

	@NotBlank
	private String memberId;

	@NotBlank
	private String tradeType;

	@NotBlank
	private String receiptPaymentType;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double amount;

	private Double minAmount;

	public PreTradeOrder convertToPo() {
		PreTradeOrder po = new PreTradeOrder();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setState(Constant.预交易订单状态_进行中);
		po.setMaxAmount(po.getAmount());
		po.setAvailableAmount(po.getAmount());
		return po;
	}

}
