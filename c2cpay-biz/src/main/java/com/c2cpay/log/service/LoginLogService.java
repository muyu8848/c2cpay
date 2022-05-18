package com.c2cpay.log.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.c2cpay.common.vo.PageResult;
import com.c2cpay.log.domain.LoginLog;
import com.c2cpay.log.param.LoginLogQueryCondParam;
import com.c2cpay.log.repo.LoginLogRepo;
import com.c2cpay.log.vo.LoginLogVO;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class LoginLogService {

	@Autowired
	private LoginLogRepo loginLogRepo;

	@Transactional(readOnly = true)
	public PageResult<LoginLogVO> findLoginLogByPage(@Valid LoginLogQueryCondParam param) {
		Specification<LoginLog> spec = new Specification<LoginLog>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<LoginLog> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotEmpty(param.getIpAddr())) {
					predicates.add(builder.equal(root.get("ipAddr"), param.getIpAddr()));
				}
				if (StrUtil.isNotEmpty(param.getUserName())) {
					predicates.add(builder.equal(root.get("userName"), param.getUserName()));
				}
				if (StrUtil.isNotEmpty(param.getSubSystem())) {
					predicates.add(builder.equal(root.get("subSystem"), param.getSubSystem()));
				}
				if (StrUtil.isNotEmpty(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (param.getStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("loginTime").as(Date.class),
							DateUtil.beginOfDay(param.getStartTime())));
				}
				if (param.getEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("loginTime").as(Date.class),
							DateUtil.endOfDay(param.getEndTime())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<LoginLog> result = loginLogRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("loginTime"))));
		PageResult<LoginLogVO> pageResult = new PageResult<>(LoginLogVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void recordLoginLog(LoginLog loginLog) {
		loginLogRepo.save(loginLog);
	}

}
