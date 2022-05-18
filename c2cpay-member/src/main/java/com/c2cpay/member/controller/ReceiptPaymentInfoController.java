package com.c2cpay.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2cpay.common.vo.Result;
import com.c2cpay.receiptpaymentinfo.param.AddReceiptPaymentInfoParam;
import com.c2cpay.receiptpaymentinfo.param.ReceiptPaymentInfoQueryCondParam;
import com.c2cpay.receiptpaymentinfo.param.UpdateActivatedFlagParam;
import com.c2cpay.receiptpaymentinfo.service.ReceiptPaymentInfoService;
import com.c2cpay.receiptpaymentinfo.vo.ReceiptPaymentInfoVO;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/receiptPaymentInfo")
public class ReceiptPaymentInfoController {

	@Autowired
	private ReceiptPaymentInfoService receiptPaymentInfoService;
	
	@PostMapping("/updateActivatedFlag")
	public Result<String> updateActivatedFlag(UpdateActivatedFlagParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		receiptPaymentInfoService.updateActivatedFlag(param);
		return Result.success();
	}

	@GetMapping("/findAll")
	public Result<List<ReceiptPaymentInfoVO>> findAll(ReceiptPaymentInfoQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(receiptPaymentInfoService.findAll(param));
	}

	@PostMapping("/del")
	public Result<String> del(String id) {
		receiptPaymentInfoService.del(id, StpUtil.getLoginIdAsString());
		return Result.success();
	}

	@PostMapping("/add")
	public Result<String> add(AddReceiptPaymentInfoParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		receiptPaymentInfoService.add(param);
		return Result.success();
	}

}
