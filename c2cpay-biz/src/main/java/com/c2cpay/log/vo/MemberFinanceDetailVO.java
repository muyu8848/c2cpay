package com.c2cpay.log.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.log.domain.MemberBalanceChangeLog;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MemberFinanceDetailVO {

	private String id;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date changeTime;

	private String changeType;

	private String changeTypeName;

	private Double balanceChange;

	public static MemberFinanceDetailVO convertFor(MemberBalanceChangeLog po) {
		if (po == null) {
			return null;
		}
		MemberFinanceDetailVO vo = new MemberFinanceDetailVO();
		BeanUtils.copyProperties(po, vo);
		vo.setChangeTypeName(DictHolder.getDictItemName("memberBalanceChangeType", vo.getChangeType()));
		return vo;
	}

}
