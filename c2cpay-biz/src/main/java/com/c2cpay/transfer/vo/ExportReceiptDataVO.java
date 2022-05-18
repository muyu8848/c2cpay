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
import com.c2cpay.transfer.domain.MemberReceiptRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ExportReceiptDataVO {

	@ExcelProperty("订单号")
	private String orderNo;

	@ExcelProperty("收款金额")
	private Double amount;

	@ExcelProperty("收款地址")
	private String receiptAddr;

	@ExcelProperty("收款姓名")
	private String receiptRealName;

	@ExcelProperty("收款手机号")
	private String receiptMobile;

	@ExcelProperty("转账地址")
	private String transferAddr;

	@ExcelProperty("业务类型")
	private String bizTypeName;

	@ExcelProperty("创建时间")
	@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	public static List<ExportReceiptDataVO> convertFor(List<MemberReceiptRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		Map<String, String> transferBizTypeMap = DictHolder.findDictItem("transferBizType").stream()
				.collect(Collectors.toMap(DictItemVO::getDictItemCode, DictItemVO::getDictItemName));
		List<ExportReceiptDataVO> vos = new ArrayList<>();
		for (MemberReceiptRecord po : pos) {
			ExportReceiptDataVO vo = new ExportReceiptDataVO();
			BeanUtils.copyProperties(po, vo);
			if (po.getReceiptAccount() != null) {
				vo.setReceiptAddr(po.getReceiptAccount().getWalletAddr());
				vo.setReceiptRealName(po.getReceiptAccount().getRealName());
				vo.setReceiptMobile(po.getReceiptAccount().getMobile());
			}
			vo.setBizTypeName(transferBizTypeMap.get(po.getBizType()));
			vos.add(vo);
		}
		return vos;
	}

}
