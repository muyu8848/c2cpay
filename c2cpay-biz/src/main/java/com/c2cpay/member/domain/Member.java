package com.c2cpay.member.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.c2cpay.common.exception.BizException;
import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.common.utils.RedisUtils;
import com.c2cpay.constants.Constant;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Member implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String realName;

	private String mobile;

	private String nickName;

	private String walletAddr;

	private String loginPwd;

	private String payPwd;

	private Double balance;

	private Double freezeFund;

	private String state;

	private String buyState;

	private String sellState;

	private Integer keepLoginDuration;

	private Date registeredTime;

	private Date latelyLoginTime;

	private Boolean deletedFlag;

	private Date deletedTime;

	@Version
	private Long version;

	public static Member build(String nickName, String mobile, String loginPwd) {
		Member po = new Member();
		po.setId(IdUtils.getId());
		po.setNickName(nickName);
		po.setMobile(mobile);
		po.setLoginPwd(SaSecureUtil.sha256(loginPwd));
		po.setState(Constant.功能状态_启用);
		po.setBuyState(Constant.功能状态_启用);
		po.setSellState(Constant.功能状态_启用);
		po.setDeletedFlag(false);
		po.setRegisteredTime(new Date());
		po.setBalance(2000d);
		po.setFreezeFund(0d);
		po.setWalletAddr(IdUtil.fastSimpleUUID().substring(0, 16));
		po.setKeepLoginDuration(12);
		return po;
	}

	public void validBasicTradeRisk() {
		if (Constant.功能状态_禁用.equals(this.getState())) {
			throw new BizException("账号已被禁用");
		}
		if (StrUtil.isBlank(this.getRealName())) {
			throw new BizException("请先进行实名认证");
		}
		if (StrUtil.isBlank(this.getPayPwd())) {
			throw new BizException("请先设置支付密码");
		}
	}

	public void validSellRisk() {
		validBasicTradeRisk();
		if (Constant.功能状态_禁用.equals(this.getSellState())) {
			throw new BizException("卖出功能已被禁用");
		}
		if (this.checkRiskLimit(Constant.风控处罚_限制卖出)) {
			throw new BizException("已被限制卖出功能");
		}
	}

	public void validBuyRisk() {
		validBasicTradeRisk();
		if (Constant.功能状态_禁用.equals(this.getBuyState())) {
			throw new BizException("买入功能已被禁用");
		}
		if (this.checkRiskLimit(Constant.风控处罚_限制买入)) {
			throw new BizException("已被限制买入功能");
		}
	}

	public Boolean checkRiskLimit(String riskPunish) {
		String limit = RedisUtils.opsForValueGet(riskPunish + this.getId());
		return StrUtil.isNotBlank(limit);
	}

	public void deleted() {
		this.setDeletedFlag(true);
		this.setDeletedTime(new Date());
	}

}
