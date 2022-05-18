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
import com.c2cpay.transfer.domain.MerchantTransferRecord;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantTransferRecordQueryCondParam extends PageParam {

	private String orderNo;

	private String merchantOrderNo;

	private String merchantId;

	private String userName;

	private String transferAddr;

	private String receiptAddr;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeEnd;

	public Specification<MerchantTransferRecord> buildSpecification() {
		MerchantTransferRecordQueryCondParam param = this;
		Specification<MerchantTransferRecord> spec = new Specification<MerchantTransferRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantTransferRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantOrderNo())) {
					predicates.add(builder.equal(root.get("merchantOrderNo"), param.getMerchantOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantId())) {
					predicates.add(builder.equal(root.get("transferAccountId"), param.getMerchantId()));
				}
				if (StrUtil.isNotBlank(param.getUserName())) {
					predicates.add(builder.equal(root.join("transferAccount").get("userName"), param.getUserName()));
				}
				if (StrUtil.isNotBlank(param.getTransferAddr())) {
					predicates.add(
							builder.equal(root.join("transferAccount").get("walletAddr"), param.getTransferAddr()));
				}
				if (StrUtil.isNotBlank(param.getReceiptAddr())) {
					predicates.add(builder.equal(root.get("receiptAddr"), param.getReceiptAddr()));
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
