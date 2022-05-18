package com.c2cpay.transfer.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.c2cpay.transfer.domain.MemberTransferRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MemberTransferDetailVO {

	private String id;

	private Double amount;

	private String receiptAddr;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	public static MemberTransferDetailVO convertFor(MemberTransferRecord po) {
		if (po == null) {
			return null;
		}
		MemberTransferDetailVO vo = new MemberTransferDetailVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
