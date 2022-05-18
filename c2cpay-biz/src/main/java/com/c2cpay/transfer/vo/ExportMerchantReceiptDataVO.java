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
import com.c2cpay.transfer.domain.MerchantReceiptRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ExportMerchantReceiptDataVO {

	@ExcelProperty("平台订单号")
	private String orderNo;

	@ExcelProperty("商户订单号")
	private String merchantOrderNo;

	@ExcelProperty("收款金额")
	private Double amount;

	@ExcelProperty("收款地址")
	private String receiptAddr;

	@ExcelProperty("收款商户号")
	private String receiptUserName;

	@ExcelProperty("收款商户名")
	private String receiptMerchantName;

	@ExcelProperty("转账地址")
	private String transferAddr;

	@ExcelProperty("订单状态")
	private String stateName;

	@ExcelProperty("创建时间")
	@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@ExcelProperty("完结时间")
	@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	public static List<ExportMerchantReceiptDataVO> convertFor(List<MerchantReceiptRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		Map<String, String> stateMap = DictHolder.findDictItem("merchantReceiptRecordState").stream()
				.collect(Collectors.toMap(DictItemVO::getDictItemCode, DictItemVO::getDictItemName));
		List<ExportMerchantReceiptDataVO> vos = new ArrayList<>();
		for (MerchantReceiptRecord po : pos) {
			ExportMerchantReceiptDataVO vo = new ExportMerchantReceiptDataVO();
			BeanUtils.copyProperties(po, vo);
			if (po.getReceiptAccount() != null) {
				vo.setReceiptAddr(po.getReceiptAccount().getWalletAddr());
				vo.setReceiptUserName(po.getReceiptAccount().getUserName());
				vo.setReceiptMerchantName(po.getReceiptAccount().getMerchantName());
			}
			vo.setStateName(stateMap.get(po.getState()));
			vos.add(vo);
		}
		return vos;
	}

}
