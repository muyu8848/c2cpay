package com.c2cpay.trade.param;

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
import com.c2cpay.trade.domain.PreTradeOrder;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PreTradeOrderQueryCondParam extends PageParam {

	private String orderNo;

	private String memberId;

	private String state;

	private String tradeType;

	private Double availableAmount;

	private String receiptPaymentType;

	private Double tradeAmountRange;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeEnd;

	public Specification<PreTradeOrder> buildSpecification() {
		PreTradeOrderQueryCondParam param = this;
		Specification<PreTradeOrder> spec = new Specification<PreTradeOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<PreTradeOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMemberId())) {
					predicates.add(builder.equal(root.get("memberId"), param.getMemberId()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (StrUtil.isNotBlank(param.getTradeType())) {
					predicates.add(builder.equal(root.get("tradeType"), param.getTradeType()));
				}
				if (param.getAvailableAmount() != null) {
					predicates.add(builder.gt(root.get("availableAmount"), param.getAvailableAmount()));
				}
				if (StrUtil.isNotBlank(param.getReceiptPaymentType())) {
					predicates.add(
							builder.like(root.get("receiptPaymentType"), "%" + param.getReceiptPaymentType() + "%"));
				}
				if (param.getTradeAmountRange() != null) {
					predicates.add(builder.le(root.get("minAmount"), param.getTradeAmountRange()));
					predicates.add(builder.ge(root.get("maxAmount"), param.getTradeAmountRange()));
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
