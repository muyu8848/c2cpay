package com.c2cpay.transfer.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.transfer.domain.MemberTransferRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MemberTransferRecordVO {

	private String id;

	private Double amount;

	@JsonFormat(pattern = "HH:mm dd/MM/yyyy", timezone = "GMT+8")
	private Date createTime;

	public static List<MemberTransferRecordVO> convertFor(List<MemberTransferRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MemberTransferRecordVO> vos = new ArrayList<>();
		for (MemberTransferRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MemberTransferRecordVO convertFor(MemberTransferRecord po) {
		if (po == null) {
			return null;
		}
		MemberTransferRecordVO vo = new MemberTransferRecordVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
