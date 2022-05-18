package com.c2cpay.transfer.vo.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.transfer.domain.statistic.MerchantEverydayTransferData;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantEverydayTransferDataVO {

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date everyday;

	private Double successAmount;

	private Integer successCount;

	private String userName;

	private String merchantName;

	public static List<MerchantEverydayTransferDataVO> convertFor(List<MerchantEverydayTransferData> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MerchantEverydayTransferDataVO> vos = new ArrayList<>();
		for (MerchantEverydayTransferData po : pos) {
			MerchantEverydayTransferDataVO vo = new MerchantEverydayTransferDataVO();
			BeanUtils.copyProperties(po, vo);
			if (po.getAccount() != null) {
				vo.setUserName(po.getAccount().getUserName());
				vo.setMerchantName(po.getAccount().getMerchantName());
			}
			vos.add(vo);
		}
		return vos;
	}

}
