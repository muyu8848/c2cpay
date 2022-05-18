package com.c2cpay.setting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.c2cpay.common.vo.Result;
import com.c2cpay.setting.service.SettingService;
import com.c2cpay.setting.vo.SystemSettingVO;

@Controller
@RequestMapping("/setting")
public class SettingController {

	@Autowired
	private SettingService service;

	@GetMapping("/getSystemSetting")
	@ResponseBody
	public Result<SystemSettingVO> getSystemSetting() {
		return Result.success(service.getSystemSetting());
	}

}
