package com.c2cpay.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2cpay.common.vo.PageResult;
import com.c2cpay.common.vo.Result;
import com.c2cpay.transfer.param.LockPreReceiptOrderParam;
import com.c2cpay.transfer.param.PreReceiptTransferParam;
import com.c2cpay.transfer.param.ReceiptRecordQueryCondParam;
import com.c2cpay.transfer.param.TransferParam;
import com.c2cpay.transfer.param.TransferRecordQueryCondParam;
import com.c2cpay.transfer.service.MerchantTransferService;
import com.c2cpay.transfer.service.TransferService;
import com.c2cpay.transfer.vo.MemberReceiptDetailVO;
import com.c2cpay.transfer.vo.MemberReceiptRecordVO;
import com.c2cpay.transfer.vo.MemberTransferDetailVO;
import com.c2cpay.transfer.vo.MemberTransferRecordVO;
import com.c2cpay.transfer.vo.PreReceiptOrderDetailVO;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/transfer")
public class TransferController {

	@Autowired
	private TransferService transferService;

	@Autowired
	private MerchantTransferService merchantTransferService;

	@PostMapping("/preReceiptTransfer")
	public Result<String> preReceiptTransfer(PreReceiptTransferParam param) {
		param.setTransferAccountId(StpUtil.getLoginIdAsString());
		merchantTransferService.preReceiptTransfer(param);
		return Result.success();
	}

	@GetMapping("/getPreReceiptOrderDetail")
	public Result<PreReceiptOrderDetailVO> getPreReceiptOrderDetail(String orderNo) {
		return Result.success(merchantTransferService.getPreReceiptOrderDetail(orderNo));
	}

	@PostMapping("/lockPreReceiptOrder")
	public Result<String> lockPreReceiptOrder(LockPreReceiptOrderParam param) {
		param.setTransferAccountId(StpUtil.getLoginIdAsString());
		merchantTransferService.lockPreReceiptOrder(param);
		return Result.success();
	}

	@GetMapping("/getTransferDetail")
	public Result<MemberTransferDetailVO> getTransferDetail(String id) {
		return Result.success(transferService.getTransferDetail(id));
	}

	@GetMapping("/findMemberTransferRecordByPage")
	public Result<PageResult<MemberTransferRecordVO>> findMemberTransferRecordByPage(TransferRecordQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(transferService.findMemberTransferRecordByPage(param));
	}

	@GetMapping("/getReceiptDetail")
	public Result<MemberReceiptDetailVO> getReceiptDetail(String id) {
		return Result.success(transferService.getReceiptDetail(id));
	}

	@GetMapping("/findMemberReceiptRecordByPage")
	public Result<PageResult<MemberReceiptRecordVO>> findMemberReceiptRecordByPage(ReceiptRecordQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(transferService.findMemberReceiptRecordByPage(param));
	}

	@PostMapping("/transfer")
	public Result<String> transfer(TransferParam param) {
		param.setTransferAccountId(StpUtil.getLoginIdAsString());
		transferService.transfer(param);
		return Result.success();
	}

}
