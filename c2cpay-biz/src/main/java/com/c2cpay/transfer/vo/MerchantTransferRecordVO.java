package com.c2cpay.transfer.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.transfer.domain.MerchantTransferRecord;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantTransferRecordVO {

	private String id;

	private String orderNo;

	private String merchantOrderNo;

	private String receiptAddr;

	private Double amount;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String transferAddr;

	private String transferUserName;

	private String transferMerchantName;

	public static List<MerchantTransferRecordVO> convertFor(List<MerchantTransferRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MerchantTransferRecordVO> vos = new ArrayList<>();
		for (MerchantTransferRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MerchantTransferRecordVO convertFor(MerchantTransferRecord po) {
		if (po == null) {
			return null;
		}
		MerchantTransferRecordVO vo = new MerchantTransferRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getTransferAccount() != null) {
			vo.setTransferAddr(po.getTransferAccount().getWalletAddr());
			vo.setTransferUserName(po.getTransferAccount().getUserName());
			vo.setTransferMerchantName(po.getTransferAccount().getMerchantName());
		}
		return vo;
	}

}
