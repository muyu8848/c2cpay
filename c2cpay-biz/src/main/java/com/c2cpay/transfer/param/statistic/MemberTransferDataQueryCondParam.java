package com.c2cpay.transfer.param.statistic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import com.c2cpay.transfer.domain.statistic.MemberEverydayTransferData;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class MemberTransferDataQueryCondParam {

	private String accountId;

	private String userName;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date everyday;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date timeEnd;

	public Specification<MemberEverydayTransferData> buildSpecification() {
		MemberTransferDataQueryCondParam param = this;
		Specification<MemberEverydayTransferData> spec = new Specification<MemberEverydayTransferData>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MemberEverydayTransferData> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getAccountId())) {
					predicates.add(builder.equal(root.get("accountId"), param.getAccountId()));
				}
				if (StrUtil.isNotBlank(param.getUserName())) {
					predicates.add(
							builder.equal(root.join("account", JoinType.INNER).get("mobile"), param.getUserName()));
				}
				if (param.getEveryday() != null) {
					predicates.add(builder.equal(root.get("everyday").as(Date.class),
							DateUtil.beginOfDay(param.getEveryday())));
				}
				if (param.getTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("everyday").as(Date.class),
							DateUtil.beginOfDay(param.getTimeStart())));
				}
				if (param.getTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("everyday").as(Date.class),
							DateUtil.endOfDay(param.getTimeEnd())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
