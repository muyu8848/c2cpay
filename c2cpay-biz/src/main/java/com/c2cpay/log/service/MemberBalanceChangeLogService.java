package com.c2cpay.log.service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.c2cpay.common.vo.PageResult;
import com.c2cpay.log.domain.MemberBalanceChangeLog;
import com.c2cpay.log.param.MemberBalanceChangeLogQueryCondParam;
import com.c2cpay.log.repo.MemberBalanceChangeLogRepo;
import com.c2cpay.log.vo.MemberBalanceChangeLogVO;
import com.c2cpay.log.vo.MemberFinanceDetailVO;
import com.c2cpay.log.vo.MemberFinanceRecordVO;

@Validated
@Service
public class MemberBalanceChangeLogService {

	@Autowired
	private MemberBalanceChangeLogRepo memberBalanceChangeLogRepo;

	@Transactional(readOnly = true)
	public MemberFinanceDetailVO getDetail(@NotBlank String id) {
		return MemberFinanceDetailVO.convertFor(memberBalanceChangeLogRepo.getOne(id));
	}

	@Transactional(readOnly = true)
	public PageResult<MemberBalanceChangeLogVO> findByPage(@Valid MemberBalanceChangeLogQueryCondParam param) {
		Specification<MemberBalanceChangeLog> spec = param.buildSpecification();
		Page<MemberBalanceChangeLog> result = memberBalanceChangeLogRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("changeTime"))));
		PageResult<MemberBalanceChangeLogVO> pageResult = new PageResult<>(
				MemberBalanceChangeLogVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<MemberFinanceRecordVO> findMemberFinanceRecordByPage(
			@Valid MemberBalanceChangeLogQueryCondParam param) {
		Specification<MemberBalanceChangeLog> spec = param.buildSpecification();
		Page<MemberBalanceChangeLog> result = memberBalanceChangeLogRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("changeTime"))));
		PageResult<MemberFinanceRecordVO> pageResult = new PageResult<>(
				MemberFinanceRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

}
