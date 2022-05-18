package com.c2cpay.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2cpay.common.vo.PageResult;
import com.c2cpay.common.vo.Result;
import com.c2cpay.constants.Constant;
import com.c2cpay.log.param.LoginLogQueryCondParam;
import com.c2cpay.log.service.LoginLogService;
import com.c2cpay.log.vo.LoginLogVO;
import com.c2cpay.member.param.ModifyLoginPwdParam;
import com.c2cpay.member.param.ModifyPayPwdParam;
import com.c2cpay.member.service.MemberService;
import com.c2cpay.member.vo.MemberFundInfoVO;
import com.c2cpay.member.vo.MemberSecurityInfoVO;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private LoginLogService loginLogService;

	@GetMapping("/sendModifyPayPwdVerificationCode")
	public Result<String> sendModifyPayPwdVerificationCode() {
		memberService.sendModifyPayPwdVerificationCode(StpUtil.getLoginIdAsString());
		return Result.success();
	}

	@GetMapping("/getMemberFundInfo")
	public Result<MemberFundInfoVO> getMemberFundInfo() {
		return Result.success(memberService.getMemberFundInfo(StpUtil.getLoginIdAsString()));
	}

	@GetMapping("/findLoginLogByPage")
	public Result<PageResult<LoginLogVO>> findLoginLogByPage(LoginLogQueryCondParam param) {
		param.setUserName(StpUtil.getSession().getString("mobile"));
		param.setSubSystem(Constant.子系统_会员端);
		return Result.success(loginLogService.findLoginLogByPage(param));
	}

	@PostMapping("/updateKeepLoginDuration")
	public Result<String> updateKeepLoginDuration(Integer keepLoginDuration) {
		memberService.updateKeepLoginDuration(StpUtil.getLoginIdAsString(), keepLoginDuration);
		return Result.success();
	}

	@GetMapping("/getMemberSecurityInfo")
	public Result<MemberSecurityInfoVO> getMemberSecurityInfo() {
		return Result.success(memberService.getMemberSecurityInfo(StpUtil.getLoginIdAsString()));
	}

	@GetMapping("/getRealName")
	public Result<String> getRealName() {
		return Result.success(memberService.getRealName(StpUtil.getLoginIdAsString()));
	}

	@PostMapping("/updateNickName")
	public Result<String> updateNickName(String nickName) {
		memberService.updateNickName(StpUtil.getLoginIdAsString(), nickName);
		return Result.success();
	}

	@PostMapping("/bindRealName")
	public Result<String> bindRealName(String realName) {
		memberService.bindRealName(StpUtil.getLoginIdAsString(), realName);
		return Result.success();
	}

	@PostMapping("/modifyLoginPwd")
	public Result<String> modifyLoginPwd(ModifyLoginPwdParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		memberService.modifyLoginPwd(param);
		return Result.success();
	}

	@PostMapping("/modifyPayPwd")
	public Result<String> modifyPayPwd(ModifyPayPwdParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		memberService.modifyPayPwd(param);
		return Result.success();
	}

}
