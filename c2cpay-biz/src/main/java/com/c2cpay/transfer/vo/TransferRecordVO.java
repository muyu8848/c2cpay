package com.c2cpay.transfer.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.transfer.domain.MemberTransferRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class TransferRecordVO {

	private String id;

	private String orderNo;

	private String receiptAddr;

	private String bizType;

	private String bizTypeName;

	private Double amount;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String transferAddr;

	private String transferRealName;

	private String transferMobile;

	public static List<TransferRecordVO> convertFor(List<MemberTransferRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<TransferRecordVO> vos = new ArrayList<>();
		for (MemberTransferRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static TransferRecordVO convertFor(MemberTransferRecord po) {
		if (po == null) {
			return null;
		}
		TransferRecordVO vo = new TransferRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getTransferAccount() != null) {
			vo.setTransferAddr(po.getTransferAccount().getWalletAddr());
			vo.setTransferRealName(po.getTransferAccount().getRealName());
			vo.setTransferMobile(po.getTransferAccount().getMobile());
		}
		vo.setBizTypeName(DictHolder.getDictItemName("transferBizType", vo.getBizType()));
		return vo;
	}

}
