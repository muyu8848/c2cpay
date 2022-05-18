package com.c2cpay.merchant.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.constants.Constant;
import com.c2cpay.merchant.domain.Merchant;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.Data;

@Data
public class AddMerchantParam {

	@NotBlank
	private String userName;

	@NotBlank
	private String loginPwd;
	
	@NotBlank
	private String payPwd;

	@NotBlank
	private String merchantName;

	public Merchant convertToPo() {
		Merchant po = new Merchant();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setDeletedFlag(false);
		po.setCreateTime(new Date());
		po.setBalance(0d);
		po.setState(Constant.功能状态_启用);
		po.setWalletAddr(IdUtil.fastSimpleUUID().substring(0, 16));
		po.setLoginPwd(SaSecureUtil.sha256(po.getLoginPwd()));
		po.setPayPwd(SaSecureUtil.sha256(po.getPayPwd()));
		po.setApiSecretKey(SecureUtil.md5(UUID.fastUUID().toString()));
		return po;
	}

}
