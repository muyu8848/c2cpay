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
import com.c2cpay.transfer.domain.MemberTransferRecord;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TransferRecordQueryCondParam extends PageParam {

	private String orderNo;

	private String transferAddr;

	private String memberId;
	
	private String receiptAddr;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeEnd;

	public Specification<MemberTransferRecord> buildSpecification() {
		TransferRecordQueryCondParam param = this;
		Specification<MemberTransferRecord> spec = new Specification<MemberTransferRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MemberTransferRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMemberId())) {
					predicates.add(builder.equal(root.get("transferAccountId"), param.getMemberId()));
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
