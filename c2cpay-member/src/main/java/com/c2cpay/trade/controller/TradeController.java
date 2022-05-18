package com.c2cpay.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2cpay.common.vo.PageResult;
import com.c2cpay.common.vo.Result;
import com.c2cpay.trade.param.BuyParam;
import com.c2cpay.trade.param.BuyerCancelTradeParam;
import com.c2cpay.trade.param.BuyerMarkPaidParam;
import com.c2cpay.trade.param.CancelPreTradeOrderParam;
import com.c2cpay.trade.param.CreatePreTradeOrderParam;
import com.c2cpay.trade.param.InitiateAppealParam;
import com.c2cpay.trade.param.InitiatorCancelAppealParam;
import com.c2cpay.trade.param.PreTradeOrderQueryCondParam;
import com.c2cpay.trade.param.SellParam;
import com.c2cpay.trade.param.SellerConfirmOrderParam;
import com.c2cpay.trade.param.SellerConfirmTradeCompletedParam;
import com.c2cpay.trade.param.SellerRejectOrderParam;
import com.c2cpay.trade.param.SendMsgParam;
import com.c2cpay.trade.param.TradeChatRecordQueryCondParam;
import com.c2cpay.trade.param.TradeOrderQueryCondParam;
import com.c2cpay.trade.service.TradeService;
import com.c2cpay.trade.vo.AvailableTradeOrderVO;
import com.c2cpay.trade.vo.MemberPreTradeOrderVO;
import com.c2cpay.trade.vo.MemberTradeOrderDetailVO;
import com.c2cpay.trade.vo.MemberTradeOrderVO;
import com.c2cpay.trade.vo.TradeChatRecordVO;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/trade")
public class TradeController {

	@Autowired
	private TradeService tradeService;
	
	@PostMapping("/initiatorCancelAppeal")
	public Result<String> initiatorCancelAppeal(InitiatorCancelAppealParam param) {
		param.setInitiatorId(StpUtil.getLoginIdAsString());
		tradeService.initiatorCancelAppeal(param);
		return Result.success();
	}

	@PostMapping("/initiateAppeal")
	public Result<String> initiateAppeal(InitiateAppealParam param) {
		param.setInitiatorId(StpUtil.getLoginIdAsString());
		tradeService.initiateAppeal(param);
		return Result.success();
	}

	@PostMapping("/chatRecordMarkRead")
	public Result<String> chatRecordMarkRead(String tradeOrderId) {
		tradeService.chatRecordMarkRead(tradeOrderId, StpUtil.getLoginIdAsString());
		return Result.success();
	}

	@GetMapping("/getUnreadChatCount")
	public Result<Long> getUnreadChatCount(String tradeOrderId) {
		return Result.success(tradeService.getUnreadChatCount(tradeOrderId, StpUtil.getLoginIdAsString()));
	}

	@GetMapping("/findChatRecord")
	public Result<List<TradeChatRecordVO>> findChatRecord(TradeChatRecordQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(tradeService.findMemberChatRecord(param));
	}

	@PostMapping("/sendMsg")
	public Result<String> sendMsg(SendMsgParam param) {
		param.setSenderId(StpUtil.getLoginIdAsString());
		tradeService.sendMsg(param);
		return Result.success();
	}

	@PostMapping("/cancelPreTradeOrder")
	public Result<String> cancelPreTradeOrder(CancelPreTradeOrderParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		tradeService.cancelPreTradeOrder(param);
		return Result.success();
	}

	@GetMapping("/findMemberPreTradeOrderByPage")
	public Result<PageResult<MemberPreTradeOrderVO>> findMemberPreTradeOrderByPage(PreTradeOrderQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(tradeService.findMemberPreTradeOrderByPage(param));
	}

	@GetMapping("/findMemberTradeOrderByPage")
	public Result<PageResult<MemberTradeOrderVO>> findMemberTradeOrderByPage(TradeOrderQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(tradeService.findMemberTradeOrderByPage(param));
	}

	@GetMapping("/getMemberTradeOrderDetail")
	public Result<MemberTradeOrderDetailVO> getMemberTradeOrderDetail(String tradeOrderId) {
		return Result.success(tradeService.getMemberTradeOrderDetail(tradeOrderId));
	}

	@GetMapping("/findAvailableTradeOrderByPage")
	public Result<PageResult<AvailableTradeOrderVO>> findAvailableTradeOrderByPage(PreTradeOrderQueryCondParam param) {
		return Result.success(tradeService.findAvailableTradeOrderByPage(param));
	}

	@PostMapping("/sellerConfirmTradeCompleted")
	public Result<String> sellerConfirmTradeCompleted(SellerConfirmTradeCompletedParam param) {
		param.setSellerId(StpUtil.getLoginIdAsString());
		tradeService.sellerConfirmTradeCompleted(param);
		return Result.success();
	}

	@PostMapping("/buyerMarkPaid")
	public Result<String> buyerMarkPaid(BuyerMarkPaidParam param) {
		param.setBuyerId(StpUtil.getLoginIdAsString());
		tradeService.buyerMarkPaid(param);
		return Result.success();
	}

	@PostMapping("/buyerCancelTrade")
	public Result<String> buyerCancelTrade(BuyerCancelTradeParam param) {
		param.setBuyerId(StpUtil.getLoginIdAsString());
		tradeService.buyerCancelTrade(param);
		return Result.success();
	}

	@PostMapping("/sellerRejectOrder")
	public Result<String> sellerRejectOrder(SellerRejectOrderParam param) {
		param.setSellerId(StpUtil.getLoginIdAsString());
		tradeService.sellerRejectOrder(param);
		return Result.success();
	}

	@PostMapping("/sellerConfirmOrder")
	public Result<String> sellerConfirmOrder(SellerConfirmOrderParam param) {
		param.setSellerId(StpUtil.getLoginIdAsString());
		tradeService.sellerConfirmOrder(param);
		return Result.success();
	}

	@PostMapping("/createSellOrder")
	public Result<String> createSellOrder(SellParam param) {
		param.setSellerId(StpUtil.getLoginIdAsString());
		return Result.success().setData(tradeService.createSellOrder(param));
	}

	@PostMapping("/createBuyOrder")
	public Result<String> createBuyOrder(BuyParam param) {
		param.setBuyerId(StpUtil.getLoginIdAsString());
		return Result.success(tradeService.createBuyOrder(param));
	}

	@PostMapping("/createPreTradeOrder")
	public Result<String> createPreTradeOrder(CreatePreTradeOrderParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		tradeService.createPreTradeOrder(param);
		return Result.success();
	}

}
