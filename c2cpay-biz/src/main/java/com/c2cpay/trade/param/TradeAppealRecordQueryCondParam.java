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
import com.c2cpay.trade.domain.TradeAppealRecord;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TradeAppealRecordQueryCondParam extends PageParam {

	private String defendantId;

	private String state;
	
	private String appealType;
	
	private Boolean approveAppeal;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeEnd;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date processTimeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date processTimeEnd;

	public Specification<TradeAppealRecord> buildSpecification() {
		TradeAppealRecordQueryCondParam param = this;
		Specification<TradeAppealRecord> spec = new Specification<TradeAppealRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<TradeAppealRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(root.get("state").in(Arrays.asList(param.getState().split(","))));
				}
				if (StrUtil.isNotBlank(param.getDefendantId())) {
					predicates.add(builder.equal(root.get("defendantId"), param.getDefendantId()));
				}
				if (StrUtil.isNotBlank(param.getAppealType())) {
					predicates.add(builder.equal(root.get("appealType"), param.getAppealType()));
				}
				if (param.getApproveAppeal() != null) {
					predicates.add(builder.equal(root.get("approveAppeal"), param.getApproveAppeal()));
				}
				if (param.getTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfDay(param.getTimeStart())));
				}
				if (param.getTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getTimeEnd())));
				}
				if (param.getProcessTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("processTime").as(Date.class),
							DateUtil.beginOfDay(param.getProcessTimeStart())));
				}
				if (param.getProcessTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("processTime").as(Date.class),
							DateUtil.endOfDay(param.getProcessTimeEnd())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
