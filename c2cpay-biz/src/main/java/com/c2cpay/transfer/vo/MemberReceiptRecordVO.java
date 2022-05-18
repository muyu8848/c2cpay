package com.c2cpay.transfer.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.transfer.domain.MemberReceiptRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MemberReceiptRecordVO {

	private String id;

	private Double amount;

	@JsonFormat(pattern = "HH:mm dd/MM/yyyy", timezone = "GMT+8")
	private Date createTime;

	public static List<MemberReceiptRecordVO> convertFor(List<MemberReceiptRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MemberReceiptRecordVO> vos = new ArrayList<>();
		for (MemberReceiptRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MemberReceiptRecordVO convertFor(MemberReceiptRecord po) {
		if (po == null) {
			return null;
		}
		MemberReceiptRecordVO vo = new MemberReceiptRecordVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
