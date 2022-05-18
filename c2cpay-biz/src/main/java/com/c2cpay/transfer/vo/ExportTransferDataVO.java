package com.c2cpay.transfer.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.dictconfig.vo.DictItemVO;
import com.c2cpay.transfer.domain.MemberTransferRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ExportTransferDataVO {

	@ExcelProperty("订单号")
	private String orderNo;

	@ExcelProperty("转账金额")
	private Double amount;

	@ExcelProperty("转账地址")
	private String transferAddr;

	@ExcelProperty("转账姓名")
	private String transferRealName;

	@ExcelProperty("转账手机号")
	private String transferMobile;

	@ExcelProperty("收款地址")
	private String receiptAddr;

	@ExcelProperty("业务类型")
	private String bizTypeName;

	@ExcelProperty("创建时间")
	@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	public static List<ExportTransferDataVO> convertFor(List<MemberTransferRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		Map<String, String> transferBizTypeMap = DictHolder.findDictItem("transferBizType").stream()
				.collect(Collectors.toMap(DictItemVO::getDictItemCode, DictItemVO::getDictItemName));
		List<ExportTransferDataVO> vos = new ArrayList<>();
		for (MemberTransferRecord po : pos) {
			ExportTransferDataVO vo = new ExportTransferDataVO();
			BeanUtils.copyProperties(po, vo);
			if (po.getTransferAccount() != null) {
				vo.setTransferAddr(po.getTransferAccount().getWalletAddr());
				vo.setTransferRealName(po.getTransferAccount().getRealName());
				vo.setTransferMobile(po.getTransferAccount().getMobile());
			}
			vo.setBizTypeName(transferBizTypeMap.get(po.getBizType()));
			vos.add(vo);
		}
		return vos;
	}

}
