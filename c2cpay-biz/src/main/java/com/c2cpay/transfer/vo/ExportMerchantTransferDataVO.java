package com.c2cpay.transfer.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.c2cpay.transfer.domain.MerchantTransferRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ExportMerchantTransferDataVO {

	@ExcelProperty("平台订单号")
	private String orderNo;

	@ExcelProperty("商户订单号")
	private String merchantOrderNo;

	@ExcelProperty("转账金额")
	private Double amount;

	@ExcelProperty("转账地址")
	private String transferAddr;

	@ExcelProperty("转账商户号")
	private String transferUserName;

	@ExcelProperty("转账商户名")
	private String transferMerchantName;

	@ExcelProperty("收款地址")
	private String receiptAddr;

	@ExcelProperty("创建时间")
	@DateTimeFormat("yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	public static List<ExportMerchantTransferDataVO> convertFor(List<MerchantTransferRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<ExportMerchantTransferDataVO> vos = new ArrayList<>();
		for (MerchantTransferRecord po : pos) {
			ExportMerchantTransferDataVO vo = new ExportMerchantTransferDataVO();
			BeanUtils.copyProperties(po, vo);
			if (po.getTransferAccount() != null) {
				vo.setTransferAddr(po.getTransferAccount().getWalletAddr());
				vo.setTransferUserName(po.getTransferAccount().getUserName());
				vo.setTransferMerchantName(po.getTransferAccount().getMerchantName());
			}
			vos.add(vo);
		}
		return vos;
	}

}
