package com.c2cpay.merchant.param;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.c2cpay.common.param.PageParam;
import com.c2cpay.merchant.domain.Merchant;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantQueryCondParam extends PageParam {

	private String userName;

	public Specification<Merchant> buildSpecification() {
		MerchantQueryCondParam param = this;
		Specification<Merchant> spec = new Specification<Merchant>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<Merchant> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (StrUtil.isNotBlank(param.getUserName())) {
					predicates.add(builder.equal(root.get("userName"), param.getUserName()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
