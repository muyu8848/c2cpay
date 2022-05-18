package com.c2cpay.log.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.log.domain.MerchantBalanceChangeLog;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantBalanceChangeLogVO {

	private String id;

	private String bizOrderNo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date changeTime;

	private String changeType;

	private String changeTypeName;

	private Double balanceChange;

	private Double balanceBefore;

	private Double balanceAfter;

	private String userName;

	private String merchantName;

	public static List<MerchantBalanceChangeLogVO> convertFor(List<MerchantBalanceChangeLog> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MerchantBalanceChangeLogVO> vos = new ArrayList<>();
		for (MerchantBalanceChangeLog po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MerchantBalanceChangeLogVO convertFor(MerchantBalanceChangeLog po) {
		if (po == null) {
			return null;
		}
		MerchantBalanceChangeLogVO vo = new MerchantBalanceChangeLogVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getMerchant() != null) {
			vo.setUserName(po.getMerchant().getUserName());
			vo.setMerchantName(po.getMerchant().getMerchantName());
		}
		vo.setChangeTypeName(DictHolder.getDictItemName("merchantBalanceChangeType", vo.getChangeType()));
		return vo;
	}

}
