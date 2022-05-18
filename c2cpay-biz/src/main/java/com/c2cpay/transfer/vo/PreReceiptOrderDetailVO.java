package com.c2cpay.transfer.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.c2cpay.transfer.domain.MerchantReceiptRecord;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

@Data
public class PreReceiptOrderDetailVO {

	private String orderNo;

	private String state;

	private Double amount;

	private String merchantName;

	private Long lockMinuteCount = 0L;
	
	private Long lockRemainingTime = 0L;

	private Long transferMinuteCount = 0L;

	private Long transferRemainingTime = 0L;

	public static PreReceiptOrderDetailVO convertFor(MerchantReceiptRecord po) {
		if (po == null) {
			return null;
		}
		PreReceiptOrderDetailVO vo = new PreReceiptOrderDetailVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getReceiptAccount() != null) {
			vo.setMerchantName(po.getReceiptAccount().getMerchantName());
		}
		vo.setLockMinuteCount(DateUtil.between(po.getCreateTime(), po.getLockDeadline(), DateUnit.MINUTE));
		long lockRemainingTime = DateUtil.between(new Date(), po.getLockDeadline(), DateUnit.SECOND, false);
		if (lockRemainingTime < 0) {
			lockRemainingTime = 0;
		}
		vo.setLockRemainingTime(lockRemainingTime);
		if (po.getTransferDeadline() != null) {
			vo.setTransferMinuteCount(DateUtil.between(po.getLockTime(), po.getTransferDeadline(), DateUnit.MINUTE));
			long transferRemainingTime = DateUtil.between(new Date(), po.getTransferDeadline(), DateUnit.SECOND, false);
			if (transferRemainingTime < 0) {
				transferRemainingTime = 0;
			}
			vo.setTransferRemainingTime(transferRemainingTime);
		}
		return vo;
	}

}
