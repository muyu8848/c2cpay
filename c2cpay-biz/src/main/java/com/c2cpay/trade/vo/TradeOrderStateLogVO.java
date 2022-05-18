package com.c2cpay.trade.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.trade.domain.TradeOrderStateLog;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class TradeOrderStateLogVO {

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date logTime;

	public static List<TradeOrderStateLogVO> convertFor(List<TradeOrderStateLog> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<TradeOrderStateLogVO> vos = new ArrayList<>();
		for (TradeOrderStateLog po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static TradeOrderStateLogVO convertFor(TradeOrderStateLog po) {
		if (po == null) {
			return null;
		}
		TradeOrderStateLogVO vo = new TradeOrderStateLogVO();
		BeanUtils.copyProperties(po, vo);
		vo.setStateName(DictHolder.getDictItemName("tradeOrderState", vo.getState()));
		return vo;
	}

}
