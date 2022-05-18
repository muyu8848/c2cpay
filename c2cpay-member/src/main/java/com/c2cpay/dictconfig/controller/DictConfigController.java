package com.c2cpay.dictconfig.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.c2cpay.common.vo.Result;
import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.dictconfig.vo.DictItemVO;

@Controller
@RequestMapping("/dictconfig")
public class DictConfigController {
	
	@GetMapping("/findDictItemInCache")
	@ResponseBody
	public Result<List<DictItemVO>> findDictItemInCache(String dictTypeCode) {
		return Result.success(DictHolder.findDictItem(dictTypeCode));
	}

}
