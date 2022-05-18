package com.c2cpay.transfer.vo.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.transfer.domain.statistic.MemberEverydayTransferData;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MemberEverydayTransferDataVO {

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date everyday;

	private Double successAmount;

	private Integer successCount;

	private String mobile;

	private String realName;

	public static List<MemberEverydayTransferDataVO> convertFor(List<MemberEverydayTransferData> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MemberEverydayTransferDataVO> vos = new ArrayList<>();
		for (MemberEverydayTransferData po : pos) {
			MemberEverydayTransferDataVO vo = new MemberEverydayTransferDataVO();
			BeanUtils.copyProperties(po, vo);
			if (po.getAccount() != null) {
				vo.setMobile(po.getAccount().getMobile());
				vo.setRealName(po.getAccount().getRealName());
			}
			vos.add(vo);
		}
		return vos;
	}

}
