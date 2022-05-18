package com.c2cpay.log.service;

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
import com.c2cpay.log.domain.MerchantBalanceChangeLog;
import com.c2cpay.log.param.MerchantBalanceChangeLogQueryCondParam;
import com.c2cpay.log.repo.MerchantBalanceChangeLogRepo;
import com.c2cpay.log.vo.MerchantBalanceChangeLogVO;

@Validated
@Service
public class MerchantBalanceChangeLogService {

	@Autowired
	private MerchantBalanceChangeLogRepo merchantBalanceChangeLogRepo;

	@Transactional(readOnly = true)
	public PageResult<MerchantBalanceChangeLogVO> findByPage(@Valid MerchantBalanceChangeLogQueryCondParam param) {
		Specification<MerchantBalanceChangeLog> spec = param.buildSpecification();
		Page<MerchantBalanceChangeLog> result = merchantBalanceChangeLogRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("changeTime"))));
		PageResult<MerchantBalanceChangeLogVO> pageResult = new PageResult<>(
				MerchantBalanceChangeLogVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

}
