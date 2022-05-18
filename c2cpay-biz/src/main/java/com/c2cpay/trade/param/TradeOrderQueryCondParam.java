package com.c2cpay.trade.param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import com.c2cpay.common.param.PageParam;
import com.c2cpay.constants.Constant;
import com.c2cpay.trade.domain.TradeOrder;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TradeOrderQueryCondParam extends PageParam {

	private String orderNo;

	private String memberId;

	private String tradeType;

	private String state;

	private String receiptPaymentType;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeEnd;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date finishTimeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date finishTimeEnd;
	
	private String finishOperator;

	public Specification<TradeOrder> buildSpecification() {
		TradeOrderQueryCondParam param = this;
		Specification<TradeOrder> spec = new Specification<TradeOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<TradeOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(root.get("state").in(Arrays.asList(param.getState().split(","))));
				}
				if (StrUtil.isNotBlank(param.getReceiptPaymentType())) {
					predicates.add(builder.equal(root.get("receiptPaymentType"), param.getReceiptPaymentType()));
				}
				if (StrUtil.isNotBlank(param.getMemberId())) {
					Predicate buyerId = builder.equal(root.get("buyerId"), param.getMemberId());
					Predicate sellerId = builder.equal(root.get("sellerId"), param.getMemberId());
					if (StrUtil.isNotBlank(param.getTradeType())) {
						if (Constant.订单交易类型_购买.equals(param.getTradeType())) {
							predicates.add(buyerId);
						} else if (Constant.订单交易类型_出售.equals(param.getTradeType())) {
							predicates.add(sellerId);
						}
					} else {
						Predicate or = builder.or(buyerId, sellerId);
						Predicate and = builder.and(or);
						predicates.add(and);
					}
				}
				if (param.getTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfDay(param.getTimeStart())));
				}
				if (param.getTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getTimeEnd())));
				}
				if (param.getFinishTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("finishTime").as(Date.class),
							DateUtil.beginOfDay(param.getFinishTimeStart())));
				}
				if (param.getFinishTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("finishTime").as(Date.class),
							DateUtil.endOfDay(param.getFinishTimeEnd())));
				}
				if (StrUtil.isNotBlank(param.getFinishOperator())) {
					predicates.add(root.get("finishOperator").in(Arrays.asList(param.getFinishOperator().split(","))));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
