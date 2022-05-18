package com.c2cpay.member.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.c2cpay.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class MemberSecurityInfoVO {
	
	private String nickName;

	private String realName;

	private String mobile;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	private Integer keepLoginDuration;
	
	private Boolean notSetPayPwd;
	
	public static MemberSecurityInfoVO convertFor(Member po) {
		if (po == null) {
			return null;
		}
		MemberSecurityInfoVO vo = new MemberSecurityInfoVO();
		BeanUtils.copyProperties(po, vo);
		vo.setNotSetPayPwd(StrUtil.isBlank(po.getPayPwd()));
		return vo;
	}

}
