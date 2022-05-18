package com.c2cpay.trade.vo.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.trade.domain.statistic.EverydayTradeData;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class EverydayTradeDataVO {

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date everyday;

	private Double successAmount;

	private Integer successCount;

	public static List<EverydayTradeDataVO> convertFor(List<EverydayTradeData> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<EverydayTradeDataVO> vos = new ArrayList<>();
		for (EverydayTradeData po : pos) {
			EverydayTradeDataVO vo = new EverydayTradeDataVO();
			BeanUtils.copyProperties(po, vo);
			vos.add(vo);
		}
		return vos;
	}

}
