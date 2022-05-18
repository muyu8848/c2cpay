package com.c2cpay.dataclean.service;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.c2cpay.dataclean.param.DataCleanParam;
import com.c2cpay.log.repo.LoginLogRepo;
import com.c2cpay.log.repo.MemberBalanceChangeLogRepo;
import com.c2cpay.log.repo.MerchantBalanceChangeLogRepo;
import com.c2cpay.log.repo.OperLogRepo;
import com.c2cpay.sms.repo.SmsSendRecordRepo;
import com.c2cpay.trade.repo.PreTradeOrderRepo;
import com.c2cpay.trade.repo.TradeAppealRecordRepo;
import com.c2cpay.trade.repo.TradeChatRecordRepo;
import com.c2cpay.trade.repo.TradeChatUnreadRepo;
import com.c2cpay.trade.repo.TradeOrderRepo;
import com.c2cpay.trade.repo.TradeOrderStateLogRepo;
import com.c2cpay.trade.repo.TradeRiskRecordRepo;
import com.c2cpay.transfer.repo.MemberReceiptRecordRepo;
import com.c2cpay.transfer.repo.MemberTransferRecordRepo;
import com.c2cpay.transfer.repo.MerchantReceiptRecordRepo;
import com.c2cpay.transfer.repo.MerchantTransferRecordRepo;

import cn.hutool.core.date.DateUtil;

@Validated
@Service
public class DataCleanService {

	@Autowired
	private LoginLogRepo loginLogRepo;

	@Autowired
	private OperLogRepo operLogRepo;

	@Autowired
	private MemberBalanceChangeLogRepo memberBalanceChangeLogRepo;

	@Autowired
	private MerchantBalanceChangeLogRepo merchantBalanceChangeLogRepo;

	@Autowired
	private SmsSendRecordRepo smsSendRecordRepo;

	@Autowired
	private MemberTransferRecordRepo memberTransferRecordRepo;

	@Autowired
	private MemberReceiptRecordRepo memberReceiptRecordRepo;

	@Autowired
	private MerchantTransferRecordRepo merchantTransferRecordRepo;

	@Autowired
	private MerchantReceiptRecordRepo merchantReceiptRecordRepo;

	@Autowired
	private PreTradeOrderRepo preTradeOrderRepo;

	@Autowired
	private TradeOrderRepo tradeOrderRepo;

	@Autowired
	private TradeOrderStateLogRepo tradeOrderStateLogRepo;

	@Autowired
	private TradeChatRecordRepo tradeChatRecordRepo;
	
	@Autowired
	private TradeChatUnreadRepo tradeChatUnreadRepo;
	
	@Autowired
	private TradeAppealRecordRepo tradeAppealRecordRepo;

	@Autowired
	private TradeRiskRecordRepo tradeRiskRecordRepo;

	@Transactional
	public void clean(@Valid DataCleanParam param) {
		List<String> dataTypes = param.getDataTypes();
		Date startTime = DateUtil.beginOfDay(param.getStartTime()).toJdkDate();
		Date endTime = DateUtil.endOfDay(param.getEndTime()).toJdkDate();
		if (dataTypes.contains("loginLog")) {
			loginLogRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("operLog")) {
			operLogRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("memberBalanceChangeLog")) {
			memberBalanceChangeLogRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("merchantBalanceChangeLog")) {
			merchantBalanceChangeLogRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("smsSendRecord")) {
			smsSendRecordRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("memberTransferRecord")) {
			memberTransferRecordRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("memberReceiptRecord")) {
			memberReceiptRecordRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("merchantTransferRecord")) {
			merchantTransferRecordRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("merchantReceiptRecord")) {
			merchantReceiptRecordRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("preTradeOrder")) {
			preTradeOrderRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("tradeRiskRecord")) {
			tradeRiskRecordRepo.dataClean(startTime, endTime);
		}
		if (dataTypes.contains("tradeOrder")) {
			tradeChatUnreadRepo.dataClean(startTime, endTime);
			tradeChatRecordRepo.dataClean(startTime, endTime);
			tradeAppealRecordRepo.dataClean(startTime, endTime);
			tradeOrderStateLogRepo.dataClean(startTime, endTime);
			tradeOrderRepo.dataClean(startTime, endTime);
		}
	}

}
