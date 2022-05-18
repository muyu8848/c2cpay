package com.c2cpay.member.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2cpay.common.exception.BizException;
import com.c2cpay.common.vo.Result;
import com.c2cpay.common.vo.TokenInfo;
import com.c2cpay.constants.Constant;
import com.c2cpay.log.domain.LoginLog;
import com.c2cpay.log.service.LoginLogService;
import com.c2cpay.member.param.ForgetLoginPwdParam;
import com.c2cpay.member.param.LoginParam;
import com.c2cpay.member.param.RegisterParam;
import com.c2cpay.member.service.MemberService;
import com.c2cpay.member.vo.AccountAuthInfoVO;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgentUtil;

@RestController
public class LoginRegisterController {

	@Autowired
	private MemberService memberService;

	@Autowired
	private LoginLogService loginLogService;

	@PostMapping("/forgetLoginPwd")
	public Result<String> forgetLoginPwd(ForgetLoginPwdParam param) {
		memberService.forgetLoginPwd(param);
		return Result.success();
	}

	@GetMapping("/sendForgetLoginPwdVerificationCode")
	public Result<String> sendForgetLoginPwdVerificationCode(String mobile) {
		memberService.sendForgetLoginPwdVerificationCode(mobile);
		return Result.success();
	}

	@GetMapping("/sendRegisterVerificationCode")
	public Result<String> sendRegisterVerificationCode(String mobile) {
		memberService.sendRegisterVerificationCode(mobile);
		return Result.success();
	}

	@PostMapping("/register")
	public Result<String> register(RegisterParam param) {
		memberService.register(param);
		return Result.success();
	}

	@PostMapping("/login")
	public Result<TokenInfo> login(LoginParam param, HttpServletRequest request) {
		LoginLog loginlLog = LoginLog.buildLog(param.getMobile(), Constant.子系统_会员端, HttpUtil.getClientIP(request),
				UserAgentUtil.parse(request.getHeader("User-Agent")));
		AccountAuthInfoVO vo = memberService.getAccountAuthInfo(param.getMobile());
		if (vo == null) {
			loginLogService.recordLoginLog(loginlLog.loginFail("账号或密码不正确"));
			throw new BizException("账号或密码不正确");
		}
		if (!SaSecureUtil.sha256(param.getLoginPwd()).equals(vo.getLoginPwd())) {
			loginLogService.recordLoginLog(loginlLog.loginFail("账号或密码不正确"));
			throw new BizException("账号或密码不正确");
		}
		if (Constant.功能状态_禁用.equals(vo.getState())) {
			loginLogService.recordLoginLog(loginlLog.loginFail("账号已被禁用"));
			throw new BizException("账号已被禁用");
		}
		loginLogService.recordLoginLog(loginlLog.loginSuccess());
		memberService.updateLatelyLoginTime(vo.getId());
		StpUtil.kickout(vo.getId());
		StpUtil.login(vo.getId(),
				new SaLoginModel().setIsLastingCookie(false).setTimeout(60 * 60 * vo.getKeepLoginDuration()));
		StpUtil.getSession().set("mobile", vo.getMobile());
		TokenInfo tokenInfo = TokenInfo.build();
		tokenInfo.setAccountId(vo.getId());
		return Result.success(tokenInfo);
	}

	@PostMapping("/logout")
	public Result<String> login() {
		StpUtil.logout();
		return Result.success();
	}

}
