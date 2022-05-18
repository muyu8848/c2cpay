package com.c2cpay.setting.vo;

import org.springframework.beans.BeanUtils;

import com.c2cpay.setting.domain.SystemSetting;

import lombok.Data;

@Data
public class LatestAppInfoVO {

	private String appUrl;

	private Double appVersion;

	public static LatestAppInfoVO convertFor(SystemSetting setting) {
		LatestAppInfoVO vo = new LatestAppInfoVO();
		if (setting != null) {
			BeanUtils.copyProperties(setting, vo);
		}
		return vo;
	}

}
