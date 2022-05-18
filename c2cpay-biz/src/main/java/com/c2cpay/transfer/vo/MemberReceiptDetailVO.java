package com.c2cpay.transfer.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.c2cpay.transfer.domain.MemberReceiptRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MemberReceiptDetailVO {

	private String id;

	private Double amount;

	private String transferAddr;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	public static MemberReceiptDetailVO convertFor(MemberReceiptRecord po) {
		if (po == null) {
			return null;
		}
		MemberReceiptDetailVO vo = new MemberReceiptDetailVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
