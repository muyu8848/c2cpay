package com.c2cpay.transfer.param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import com.c2cpay.common.param.PageParam;
import com.c2cpay.transfer.domain.MerchantReceiptRecord;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantReceiptRecordQueryCondParam extends PageParam {

	private String orderNo;

	private String merchantOrderNo;

	private String merchantId;

	private String userName;

	private String receiptAddr;

	private String transferAddr;

	private String state;
	
	private String noticeState;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeEnd;

	public Specification<MerchantReceiptRecord> buildSpecification() {
		MerchantReceiptRecordQueryCondParam param = this;
		Specification<MerchantReceiptRecord> spec = new Specification<MerchantReceiptRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantReceiptRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantOrderNo())) {
					predicates.add(builder.equal(root.get("merchantOrderNo"), param.getMerchantOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantId())) {
					predicates.add(builder.equal(root.get("receiptAccountId"), param.getMerchantId()));
				}
				if (StrUtil.isNotBlank(param.getUserName())) {
					predicates.add(builder.equal(root.join("receiptAccount").get("userName"), param.getUserName()));
				}
				if (StrUtil.isNotBlank(param.getReceiptAddr())) {
					predicates
							.add(builder.equal(root.join("receiptAccount").get("walletAddr"), param.getReceiptAddr()));
				}
				if (StrUtil.isNotBlank(param.getTransferAddr())) {
					predicates.add(builder.equal(root.get("transferAddr"), param.getTransferAddr()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (StrUtil.isNotBlank(param.getNoticeState())) {
					predicates.add(builder.equal(root.get("noticeState"), param.getNoticeState()));
				}
				if (param.getTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfDay(param.getTimeStart())));
				}
				if (param.getTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getTimeEnd())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
