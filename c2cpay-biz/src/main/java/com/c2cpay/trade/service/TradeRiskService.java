package com.c2cpay.trade.service;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.c2cpay.common.vo.PageResult;
import com.c2cpay.constants.Constant;
import com.c2cpay.setting.repo.TradeRiskSettingRepo;
import com.c2cpay.trade.domain.TradeRiskRecord;
import com.c2cpay.trade.param.TradeAppealRecordQueryCondParam;
import com.c2cpay.trade.param.TradeOrderQueryCondParam;
import com.c2cpay.trade.param.TradeRiskRecordQueryCondParam;
import com.c2cpay.trade.repo.TradeAppealRecordRepo;
import com.c2cpay.trade.repo.TradeOrderRepo;
import com.c2cpay.trade.repo.TradeRiskRecordRepo;
import com.c2cpay.trade.vo.TradeRiskRecordVO;

@Validated
@Service
public class TradeRiskService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private TradeRiskSettingRepo tradeRiskSettingRepo;

	@Autowired
	private TradeRiskRecordRepo tradeRiskRecordRepo;

	@Autowired
	private TradeOrderRepo tradeOrderRepo;

	@Autowired
	private TradeAppealRecordRepo tradeAppealRecordRepo;

	@Transactional
	public void relieveRisk(@NotBlank String riskPunish, @NotBlank String memberId) {
		redisTemplate.delete(riskPunish + memberId);
	}

	@Transactional(readOnly = true)
	public PageResult<TradeRiskRecordVO> findTradeRiskRecordByPage(@Valid TradeRiskRecordQueryCondParam param) {
		Page<TradeRiskRecord> result = tradeRiskRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<TradeRiskRecordVO> pageResult = new PageResult<>(TradeRiskRecordVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void paidSellerUnConfirm(String memberId) {
		TradeAppealRecordQueryCondParam param = new TradeAppealRecordQueryCondParam();
		param.setDefendantId(memberId);
		param.setState(Constant.申诉状态_已完成);
		param.setAppealType(Constant.交易申诉类型_已付款卖方未放行);
		param.setProcessTimeStart(new Date());
		param.setProcessTimeEnd(param.getProcessTimeStart());
		param.setApproveAppeal(true);
		long count = tradeAppealRecordRepo.count(param.buildSpecification());
		Long presetCount = tradeRiskSettingRepo.findTopByOrderByLatelyUpdateTime().getPaidSellerUnConfirm();
		if (count < presetCount) {
			return;
		}
		TradeRiskRecord riskRecord = TradeRiskRecord.buildRiskToday(memberId, Constant.风控原因_已付款卖方未放行, count,
				Constant.风控处罚_限制卖出);
		tradeRiskRecordRepo.save(riskRecord);
		redisTemplate.opsForValue().set(riskRecord.getRiskPunish() + memberId, memberId, riskRecord.getRiskSecond(),
				TimeUnit.SECONDS);
	}

	@Transactional
	public void notMySelfPaid(String memberId) {
		TradeAppealRecordQueryCondParam param = new TradeAppealRecordQueryCondParam();
		param.setDefendantId(memberId);
		param.setState(Constant.申诉状态_已完成);
		param.setAppealType(Constant.交易申诉类型_使用非本人支付账户付款);
		param.setProcessTimeStart(new Date());
		param.setProcessTimeEnd(param.getProcessTimeStart());
		param.setApproveAppeal(true);
		long count = tradeAppealRecordRepo.count(param.buildSpecification());
		Long presetCount = tradeRiskSettingRepo.findTopByOrderByLatelyUpdateTime().getNotMySelfPaid();
		if (count < presetCount) {
			return;
		}
		TradeRiskRecord riskRecord = TradeRiskRecord.buildRisk48Hour(memberId, Constant.风控原因_使用非本人支付账户付款, count,
				Constant.风控处罚_限制买入);
		tradeRiskRecordRepo.save(riskRecord);
		redisTemplate.opsForValue().set(riskRecord.getRiskPunish() + memberId, memberId, riskRecord.getRiskSecond(),
				TimeUnit.SECONDS);
	}

	@Transactional
	public void buyerUnPaid(String memberId) {
		TradeAppealRecordQueryCondParam param = new TradeAppealRecordQueryCondParam();
		param.setDefendantId(memberId);
		param.setState(Constant.申诉状态_已完成);
		param.setAppealType(Constant.交易申诉类型_买方未付款);
		param.setProcessTimeStart(new Date());
		param.setProcessTimeEnd(param.getProcessTimeStart());
		param.setApproveAppeal(true);
		long count = tradeAppealRecordRepo.count(param.buildSpecification());
		Long presetCount = tradeRiskSettingRepo.findTopByOrderByLatelyUpdateTime().getBuyerUnPaid();
		if (count < presetCount) {
			return;
		}
		TradeRiskRecord riskRecord = TradeRiskRecord.buildRiskToday(memberId, Constant.风控原因_买方未付款, count,
				Constant.风控处罚_限制买入);
		tradeRiskRecordRepo.save(riskRecord);
		redisTemplate.opsForValue().set(riskRecord.getRiskPunish() + memberId, memberId, riskRecord.getRiskSecond(),
				TimeUnit.SECONDS);
	}

	@Transactional
	public void sellerRejectOrder(String memberId) {
		TradeOrderQueryCondParam param = new TradeOrderQueryCondParam();
		param.setMemberId(memberId);
		param.setTradeType(Constant.订单交易类型_出售);
		param.setState(Constant.交易订单状态_接单已拒绝);
		param.setFinishTimeStart(new Date());
		param.setFinishTimeEnd(param.getFinishTimeStart());
		param.setFinishOperator(Constant.操作方_卖方);
		long count = tradeOrderRepo.count(param.buildSpecification());
		Long presetCount = tradeRiskSettingRepo.findTopByOrderByLatelyUpdateTime().getSellerRejectOrder();
		if (count < presetCount) {
			return;
		}
		TradeRiskRecord riskRecord = TradeRiskRecord.buildRiskToday(memberId, Constant.风控原因_卖方拒绝接单, count,
				Constant.风控处罚_限制卖出);
		tradeRiskRecordRepo.save(riskRecord);
		redisTemplate.opsForValue().set(riskRecord.getRiskPunish() + memberId, memberId, riskRecord.getRiskSecond(),
				TimeUnit.SECONDS);
	}

	@Transactional
	public void buyerCancelTrade(String memberId) {
		TradeOrderQueryCondParam param = new TradeOrderQueryCondParam();
		param.setMemberId(memberId);
		param.setTradeType(Constant.订单交易类型_购买);
		param.setState(Constant.交易订单状态_已取消);
		param.setFinishTimeStart(new Date());
		param.setFinishTimeEnd(param.getFinishTimeStart());
		param.setFinishOperator(String.join(",", Arrays.asList(Constant.操作方_买方, Constant.操作方_系统)));
		long count = tradeOrderRepo.count(param.buildSpecification());
		Long presetCount = tradeRiskSettingRepo.findTopByOrderByLatelyUpdateTime().getBuyerCancelTrade();
		if (count < presetCount) {
			return;
		}
		TradeRiskRecord riskRecord = TradeRiskRecord.buildRiskToday(memberId, Constant.风控原因_买方取消交易, count,
				Constant.风控处罚_限制买入);
		tradeRiskRecordRepo.save(riskRecord);
		redisTemplate.opsForValue().set(riskRecord.getRiskPunish() + memberId, memberId, riskRecord.getRiskSecond(),
				TimeUnit.SECONDS);
	}

}
