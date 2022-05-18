package com.c2cpay.trade.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.trade.domain.TradeRiskRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class TradeRiskRecordVO {

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String riskCause;

	private String riskCauseName;
	
	private Long hitCount;

	private String riskPunish;

	private String riskPunishName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date riskFinishTime;
	
	private String memberRealName;

	private String memberMobile;
	
	public static List<TradeRiskRecordVO> convertFor(List<TradeRiskRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<TradeRiskRecordVO> vos = new ArrayList<>();
		for (TradeRiskRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static TradeRiskRecordVO convertFor(TradeRiskRecord po) {
		if (po == null) {
			return null;
		}
		TradeRiskRecordVO vo = new TradeRiskRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getMember() != null) {
			vo.setMemberRealName(po.getMember().getRealName());
			vo.setMemberMobile(po.getMember().getMobile());
		}
		vo.setRiskCauseName(DictHolder.getDictItemName("riskCause", vo.getRiskCause()));
		vo.setRiskPunishName(DictHolder.getDictItemName("riskPunish", vo.getRiskPunish()));
		return vo;
	}

}
