package com.c2cpay.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2cpay.common.vo.PageResult;
import com.c2cpay.common.vo.Result;
import com.c2cpay.log.param.MemberBalanceChangeLogQueryCondParam;
import com.c2cpay.log.service.MemberBalanceChangeLogService;
import com.c2cpay.log.vo.MemberFinanceDetailVO;
import com.c2cpay.log.vo.MemberFinanceRecordVO;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/memberBalanceChangeLog")
public class MemberBalanceChangeLogController {

	@Autowired
	private MemberBalanceChangeLogService memberBalanceChangeLogService;

	@GetMapping("/getDetail")
	public Result<MemberFinanceDetailVO> getDetail(String id) {
		return Result.success(memberBalanceChangeLogService.getDetail(id));
	}

	@GetMapping("/findByPage")
	public Result<PageResult<MemberFinanceRecordVO>> findByPage(MemberBalanceChangeLogQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(memberBalanceChangeLogService.findMemberFinanceRecordByPage(param));
	}

}
