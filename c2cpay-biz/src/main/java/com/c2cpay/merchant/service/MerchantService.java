package com.c2cpay.merchant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.c2cpay.common.exception.BizException;
import com.c2cpay.common.googleauth.GoogleAuthenticator;
import com.c2cpay.common.vo.PageResult;
import com.c2cpay.log.domain.MerchantBalanceChangeLog;
import com.c2cpay.log.repo.MerchantBalanceChangeLogRepo;
import com.c2cpay.merchant.domain.Merchant;
import com.c2cpay.merchant.param.AddMerchantParam;
import com.c2cpay.merchant.param.MerchantQueryCondParam;
import com.c2cpay.merchant.param.ModifyLoginPwdParam;
import com.c2cpay.merchant.param.ModifyPayPwdParam;
import com.c2cpay.merchant.param.UpdateMerchantParam;
import com.c2cpay.merchant.repo.MerchantRepo;
import com.c2cpay.merchant.vo.AccountAuthInfoVO;
import com.c2cpay.merchant.vo.MerchantFundInfoVO;
import com.c2cpay.merchant.vo.MerchantInfoVO;
import com.c2cpay.merchant.vo.MerchantStatisticDataVO;
import com.c2cpay.merchant.vo.MerchantVO;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class MerchantService {

	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private MerchantBalanceChangeLogRepo merchantBalanceChangeLogRepo;

	@Transactional(readOnly = true)
	public MerchantStatisticDataVO getMerchantStatisticData() {
		List<Merchant> result = merchantRepo.findAll(new MerchantQueryCondParam().buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		MerchantStatisticDataVO vo = new MerchantStatisticDataVO();
		String today = DateUtil.today();
		for (Merchant data : result) {
			String createDate = DateUtil.format(data.getCreateTime(), DatePattern.NORM_DATE_PATTERN);
			if (createDate.equals(today)) {
				vo.setTodayRegisterCount(vo.getTodayRegisterCount() + 1);
			}
			if (data.getLatelyLoginTime() != null
					&& DateUtil.format(data.getLatelyLoginTime(), DatePattern.NORM_DATE_PATTERN).equals(today)) {
				vo.setActiveCount(vo.getActiveCount() + 1);
			}
		}
		vo.setAccountCount(result.size());
		return vo;
	}

	@Transactional(readOnly = true)
	public MerchantFundInfoVO getFundInfo() {
		List<Merchant> result = merchantRepo.findAll(new MerchantQueryCondParam().buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		return MerchantFundInfoVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public MerchantFundInfoVO getFundInfo(@NotBlank String id) {
		return MerchantFundInfoVO.convertFor(merchantRepo.getOne(id));
	}

	@Transactional(readOnly = true)
	public MerchantInfoVO getMerchantInfo(@NotBlank String id) {
		return MerchantInfoVO.convertFor(merchantRepo.getOne(id));
	}

	@Transactional
	public void updateLatelyLoginTime(@NotBlank String id) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setLatelyLoginTime(new Date());
		merchantRepo.save(merchant);
	}

	@Transactional(readOnly = true)
	public AccountAuthInfoVO getAccountAuthInfo(@NotBlank String userName) {
		return AccountAuthInfoVO.convertFor(merchantRepo.findByUserNameAndDeletedFlagIsFalse(userName));
	}

	@Transactional
	public void addBalance(@NotBlank String id, @NotNull @DecimalMin(value = "0", inclusive = true) Double amount,
			@NotBlank String backgroundAccountId) {
		Merchant merchant = merchantRepo.getOne(id);
		double balanceAfter = NumberUtil.round(merchant.getBalance() + amount, 2).doubleValue();
		merchant.setBalance(balanceAfter);
		merchantRepo.save(merchant);

		merchantBalanceChangeLogRepo.save(MerchantBalanceChangeLog.buildWithSystem(merchant, amount));
	}

	@Transactional
	public void reduceBalance(@NotBlank String id, @NotNull @DecimalMin(value = "0", inclusive = true) Double amount,
			@NotBlank String backgroundAccountId) {
		Merchant merchant = merchantRepo.getOne(id);
		double balanceAfter = NumberUtil.round(merchant.getBalance() - amount, 2).doubleValue();
		if (balanceAfter < 0) {
			throw new BizException("余额不能少于0");
		}
		merchant.setBalance(balanceAfter);
		merchantRepo.save(merchant);

		merchantBalanceChangeLogRepo.save(MerchantBalanceChangeLog.buildWithSystem(merchant, -amount));
	}

	@Transactional
	public void updateIpWhiteList(@NotBlank String accountId, String ipWhiteList) {
		Merchant merchant = merchantRepo.getOne(accountId);
		List<String> ips = new ArrayList<String>();
		if (StrUtil.isNotBlank(ipWhiteList)) {
			ipWhiteList = ipWhiteList.replace("，", ",");
			String[] tmpIps = ipWhiteList.split(",");
			for (String ip : tmpIps) {
				ips.add(StrUtil.trim(ip));
			}
		}
		merchant.setIpWhiteList(String.join(",", ips));
		merchantRepo.save(merchant);
	}

	@Transactional
	public void unBindGoogleAuth(@NotBlank String id) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.unBindGoogleAuth();
		merchantRepo.save(merchant);
	}

	@Transactional
	public void bindGoogleAuth(@NotBlank String id, @NotBlank String googleSecretKey, @NotBlank String googleVerCode) {
		if (!GoogleAuthenticator.checkCode(googleSecretKey, googleVerCode, System.currentTimeMillis())) {
			throw new BizException("谷歌验证码不正确");
		}
		Merchant merchant = merchantRepo.getOne(id);
		if (merchant.getGoogleAuthBindTime() != null) {
			throw new BizException("该账号已绑定谷歌验证器");
		}
		merchant.bindGoogleAuth(googleSecretKey);
		merchantRepo.save(merchant);
	}

	@Transactional
	public void modifyPayPwd(@Valid ModifyPayPwdParam param) {
		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		if (!SaSecureUtil.sha256(param.getOldPwd()).equals(merchant.getPayPwd())) {
			throw new BizException("旧密码不正确");
		}
		modifyPayPwd(merchant.getId(), param.getNewPwd());
	}

	@Transactional
	public void modifyPayPwd(@NotBlank String id, @NotBlank String newPwd) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setPayPwd(SaSecureUtil.sha256(newPwd));
		merchantRepo.save(merchant);
	}

	@Transactional
	public void modifyLoginPwd(@Valid ModifyLoginPwdParam param) {
		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		if (!SaSecureUtil.sha256(param.getOldPwd()).equals(merchant.getLoginPwd())) {
			throw new BizException("旧密码不正确");
		}
		modifyLoginPwd(merchant.getId(), param.getNewPwd());
	}

	@Transactional
	public void modifyLoginPwd(@NotBlank String id, @NotBlank String newPwd) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setLoginPwd(SaSecureUtil.sha256(newPwd));
		merchantRepo.save(merchant);
	}

	@Transactional
	public void delMerchant(@NotBlank String accountId) {
		Merchant merchant = merchantRepo.getOne(accountId);
		merchant.deleted();
		merchantRepo.save(merchant);
	}

	@Transactional
	public void updateMerchant(@Valid UpdateMerchantParam param) {
		if (!Validator.isGeneral(param.getUserName())) {
			throw new BizException("账号只能包含字母,数字或下划线");
		}
		Merchant merchantWithUserName = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (merchantWithUserName != null && !merchantWithUserName.getId().equals(param.getId())) {
			throw new BizException("账号已存在");
		}
		Merchant merchant = merchantRepo.getOne(param.getId());
		BeanUtils.copyProperties(param, merchant);
		merchantRepo.save(merchant);
	}

	@Transactional
	public void addMerchant(@Valid AddMerchantParam param) {
		if (!Validator.isGeneral(param.getUserName())) {
			throw new BizException("账号只能包含字母,数字或下划线");
		}
		Merchant existAccount = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (existAccount != null) {
			throw new BizException("账号已存在");
		}
		Merchant merchant = param.convertToPo();
		merchantRepo.save(merchant);
	}

	@Transactional(readOnly = true)
	public MerchantVO findMerchantById(@NotBlank String accountId) {
		return MerchantVO.convertFor(merchantRepo.getOne(accountId));
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantVO> findByPage(@Valid MerchantQueryCondParam param) {
		Page<Merchant> result = merchantRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MerchantVO> pageResult = new PageResult<>(MerchantVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

}
