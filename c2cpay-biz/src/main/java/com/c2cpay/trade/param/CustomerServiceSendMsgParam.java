package com.c2cpay.trade.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.trade.domain.TradeChatRecord;

import lombok.Data;

@Data
public class CustomerServiceSendMsgParam {
	
	@NotBlank
	private String tradeOrderId;
	
	@NotBlank
	private String msgType;

	@NotBlank
	private String content;
	
	public TradeChatRecord convertToPo() {
		TradeChatRecord po = new TradeChatRecord();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setDeletedFlag(false);
		return po;
	}

}
