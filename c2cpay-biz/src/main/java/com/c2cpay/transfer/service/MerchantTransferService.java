package com.c2cpay.transfer.service;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.c2cpay.common.exception.BizException;
import com.c2cpay.common.utils.ThreadPoolUtils;
import com.c2cpay.common.vo.PageResult;
import com.c2cpay.constants.Constant;
import com.c2cpay.log.domain.MemberBalanceChangeLog;
import com.c2cpay.log.domain.MerchantBalanceChangeLog;
import com.c2cpay.log.repo.MemberBalanceChangeLogRepo;
import com.c2cpay.log.repo.MerchantBalanceChangeLogRepo;
import com.c2cpay.member.domain.Member;
import com.c2cpay.member.repo.MemberRepo;
import com.c2cpay.merchant.domain.Merchant;
import com.c2cpay.merchant.repo.MerchantRepo;
import com.c2cpay.setting.repo.SystemSettingRepo;
import com.c2cpay.transfer.domain.MerchantReceiptRecord;
import com.c2cpay.transfer.domain.MerchantTransferRecord;
import com.c2cpay.transfer.domain.MemberReceiptRecord;
import com.c2cpay.transfer.domain.MemberTransferRecord;
import com.c2cpay.transfer.param.LockPreReceiptOrderParam;
import com.c2cpay.transfer.param.MerchantReceiptRecordQueryCondParam;
import com.c2cpay.transfer.param.MerchantTransferApiParam;
import com.c2cpay.transfer.param.MerchantTransferParam;
import com.c2cpay.transfer.param.MerchantTransferRecordQueryCondParam;
import com.c2cpay.transfer.param.PreReceiptApiParam;
import com.c2cpay.transfer.param.PreReceiptParam;
import com.c2cpay.transfer.param.PreReceiptTransferParam;
import com.c2cpay.transfer.repo.MerchantReceiptRecordRepo;
import com.c2cpay.transfer.repo.MerchantTransferRecordRepo;
import com.c2cpay.transfer.repo.MemberReceiptRecordRepo;
import com.c2cpay.transfer.repo.MemberTransferRecordRepo;
import com.c2cpay.transfer.vo.CreatePreReceiptReturnVO;
import com.c2cpay.transfer.vo.ExportMerchantReceiptDataVO;
import com.c2cpay.transfer.vo.ExportMerchantTransferDataVO;
import com.c2cpay.transfer.vo.MerchantReceiptRecordVO;
import com.c2cpay.transfer.vo.MerchantReceiptSubtotalVO;
import com.c2cpay.transfer.vo.MerchantTransferRecordVO;
import com.c2cpay.transfer.vo.MerchantTransferSubtotalVO;
import com.c2cpay.transfer.vo.PreReceiptOrderDetailVO;
import com.zengtengpeng.annotation.Lock;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class MerchantTransferService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private MerchantTransferRecordRepo merchantTransferRecordRepo;

	@Autowired
	private MerchantReceiptRecordRepo merchantReceiptRecordRepo;

	@Autowired
	private MemberTransferRecordRepo transferRecordRepo;

	@Autowired
	private MemberReceiptRecordRepo receiptRecordRepo;

	@Autowired
	private MerchantBalanceChangeLogRepo merchantBalanceChangeLogRepo;

	@Autowired
	private MemberBalanceChangeLogRepo memberBalanceChangeLogRepo;

	@Autowired
	private SystemSettingRepo systemSettingRepo;

	@Lock(keys = "'merchantReceiptAsynNotice' + #id")
	@Transactional
	public String receiptAsynNotice(@NotBlank String id) {
		MerchantReceiptRecord order = merchantReceiptRecordRepo.getOne(id);
		if (!(Constant.收款记录状态_已完成.equals(order.getState()))) {
			throw new BizException("只有已完成的订单才能进行异步通知");
		}
		if (Constant.通知状态_无需通知.equals(order.getNoticeState())) {
			log.warn("该订单无需通知;订单号为{}", id);
			return Constant.通知成功返回值;
		}
		if (Constant.通知状态_通知成功.equals(order.getNoticeState())) {
			log.warn("订单已通知成功,无需重复通知;订单号为{}", id);
			return Constant.通知成功返回值;
		}
		Merchant receiptAccount = order.getReceiptAccount();

		String state = Constant.收款记录状态_已完成.equals(order.getState()) ? Constant.支付成功 : Constant.支付失败;
		String sign = state + receiptAccount.getUserName() + order.getMerchantOrderNo()
				+ new DecimalFormat("###################.###########").format(order.getAmount())
				+ receiptAccount.getApiSecretKey();
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("merchantNum", receiptAccount.getUserName());
		paramMap.put("orderNo", order.getMerchantOrderNo());
		paramMap.put("platformOrderNo", order.getOrderNo());
		paramMap.put("amount", new DecimalFormat("###################.###########").format(order.getAmount()));
		paramMap.put("state", state);
		paramMap.put("endTime", DateUtil.format(order.getEndTime(), DatePattern.NORM_DATETIME_PATTERN));
		paramMap.put("sign", sign);
		String result = "fail";
		boolean exceptionFlag = false;
		// 通知两次
		for (int i = 0; i < 2; i++) {
			try {
				result = HttpUtil.post(order.getNotifyUrl(), paramMap, 3500 + (i * 1000));
				if (Constant.通知成功返回值.equals(result)) {
					exceptionFlag = false;
					break;
				}
			} catch (Exception e) {
				exceptionFlag = true;
				result = e.getMessage();
				log.error(MessageFormat.format("异步通知地址请求异常,订单号为{0}", id), e);
			}
		}
		if (exceptionFlag) {
			return result;
		}
		merchantReceiptRecordRepo.updateNoticeState(order.getId(),
				Constant.通知成功返回值.equals(result) ? Constant.通知状态_通知成功 : Constant.通知状态_通知失败);
		return result;
	}

	@Transactional
	public void receiptDeadline() {
		Date now = new Date();
		List<MerchantReceiptRecord> lockDeadlineOrders = merchantReceiptRecordRepo
				.findByStateAndLockDeadlineLessThan(Constant.收款记录状态_未拉起, now);
		for (MerchantReceiptRecord order : lockDeadlineOrders) {
			redissonClient.getTopic(Constant.商户收款超时未拉起).publish(order.getId());
		}
		List<MerchantReceiptRecord> transferDeadlineOrders = merchantReceiptRecordRepo
				.findByStateAndTransferDeadlineLessThan(Constant.收款记录状态_未付款, now);
		for (MerchantReceiptRecord order : transferDeadlineOrders) {
			redissonClient.getTopic(Constant.商户收款超时未付款).publish(order.getId());
		}
	}

	@Transactional
	public void merchantReceiptTimeOutUnPaid(String id) {
		MerchantReceiptRecord order = merchantReceiptRecordRepo.getOne(id);
		if (!Constant.收款记录状态_未付款.equals(order.getState())) {
			return;
		}
		order.transferTimeOut();
		merchantReceiptRecordRepo.save(order);
	}

	@Transactional
	public void merchantReceiptTimeOutUnLock(String id) {
		MerchantReceiptRecord order = merchantReceiptRecordRepo.getOne(id);
		if (!Constant.收款记录状态_未拉起.equals(order.getState())) {
			return;
		}
		order.lockTimeOut();
		merchantReceiptRecordRepo.save(order);
	}

	@Transactional
	public void merchantReceiptFundSync() {
		List<MerchantReceiptRecord> records = merchantReceiptRecordRepo
				.findByStateAndReceiptFundSyncFalse(Constant.收款记录状态_已完成);
		for (MerchantReceiptRecord record : records) {
			redissonClient.getTopic(Constant.商户收款资金同步).publish(record.getId());
		}
	}

	@Lock(keys = "'merchantReceiptFundSync' + #id")
	@Transactional
	public void merchantReceiptFundSync(@NotBlank String id) {
		MerchantReceiptRecord record = merchantReceiptRecordRepo.getOne(id);
		if (!Constant.收款记录状态_已完成.equals(record.getState())) {
			return;
		}
		if (record.getReceiptFundSync()) {
			return;
		}
		record.setReceiptFundSync(true);
		merchantReceiptRecordRepo.save(record);

		Merchant receiptAccount = record.getReceiptAccount();
		receiptAccount.setBalance(NumberUtil.round(receiptAccount.getBalance() + record.getAmount(), 2).doubleValue());
		merchantRepo.save(receiptAccount);

		merchantBalanceChangeLogRepo.save(MerchantBalanceChangeLog.buildWithReceipt(receiptAccount, record));
	}

	@Transactional(readOnly = true)
	public MerchantReceiptSubtotalVO receiptSubtotal(MerchantReceiptRecordQueryCondParam param) {
		MerchantReceiptSubtotalVO vo = new MerchantReceiptSubtotalVO();
		List<MerchantReceiptRecord> orders = merchantReceiptRecordRepo.findAll(param.buildSpecification());
		for (MerchantReceiptRecord order : orders) {
			if (!Constant.收款记录状态_已完成.equals(order.getState())) {
				continue;
			}
			vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + order.getAmount(), 2).doubleValue());
			vo.setSuccessCount(vo.getSuccessCount() + 1);
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public List<ExportMerchantReceiptDataVO> findExportReceiptData(MerchantReceiptRecordQueryCondParam param) {
		List<MerchantReceiptRecord> result = merchantReceiptRecordRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		return ExportMerchantReceiptDataVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantReceiptRecordVO> findReceiptRecordByPage(
			@Valid MerchantReceiptRecordQueryCondParam param) {
		Page<MerchantReceiptRecord> result = merchantReceiptRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MerchantReceiptRecordVO> pageResult = new PageResult<>(
				MerchantReceiptRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void preReceiptTransfer(@Valid PreReceiptTransferParam param) {
		Member transferAccount = memberRepo.getOne(param.getTransferAccountId());
		MerchantReceiptRecord order = merchantReceiptRecordRepo.findTopByOrderNo(param.getOrderNo());
		if (!Constant.收款记录状态_未付款.equals(order.getState())) {
			throw new BizException("订单状态异常");
		}
		if (!order.getTransferAddr().equals(transferAccount.getWalletAddr())) {
			throw new BizException("无权操作");
		}
		transferAccount.validBasicTradeRisk();
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(transferAccount.getPayPwd())) {
			throw new BizException("支付密码不正确");
		}
		double balance = NumberUtil.round(transferAccount.getBalance() - order.getAmount(), 2).doubleValue();
		if (balance < 0) {
			throw new BizException("余额不足");
		}

		MemberTransferRecord transferRecord = MemberTransferRecord.build(transferAccount.getId(), order.getAmount(),
				order.getReceiptAccount().getWalletAddr(), Constant.转账业务类型_C2B);
		transferRecordRepo.save(transferRecord);

		transferAccount.setBalance(balance);
		memberRepo.save(transferAccount);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithTransfer(transferAccount, transferRecord));

		order.receiptCompleted();
		merchantReceiptRecordRepo.save(order);

		ThreadPoolUtils.getTransferPool().schedule(() -> {
			redissonClient.getTopic(Constant.商户收款资金同步).publish(order.getId());
			redissonClient.getTopic(Constant.商户收款异步通知).publish(order.getId());
		}, 1, TimeUnit.SECONDS);
	}

	@Transactional(readOnly = true)
	public PreReceiptOrderDetailVO getPreReceiptOrderDetail(@NotBlank String orderNo) {
		MerchantReceiptRecord order = merchantReceiptRecordRepo.findTopByOrderNo(orderNo);
		return PreReceiptOrderDetailVO.convertFor(order);
	}

	@Lock(keys = "'lockPreReceiptOrder' + #param.orderNo")
	@Transactional
	public void lockPreReceiptOrder(@Valid LockPreReceiptOrderParam param) {
		Member member = memberRepo.getOne(param.getTransferAccountId());
		member.validBasicTradeRisk();
		MerchantReceiptRecord order = merchantReceiptRecordRepo.findTopByOrderNo(param.getOrderNo());
		if (order == null) {
			throw new BizException("订单不存在");
		}
		if (StrUtil.isNotBlank(order.getTransferAddr()) && !order.getTransferAddr().equals(member.getWalletAddr())) {
			throw new BizException("请重新拉起订单");
		}
		if (!Constant.收款记录状态_未拉起.equals(order.getState())) {
			return;
		}
		order.lockOrder(member.getWalletAddr());
		merchantReceiptRecordRepo.save(order);
	}

	@Transactional
	public CreatePreReceiptReturnVO preReceiptApi(@Valid PreReceiptApiParam param, @NotBlank String ipAddr) {
		Merchant merchant = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getMerchantNum());
		if (merchant == null) {
			throw new BizException("请检查商户号是否正确");
		}
		if (StrUtil.isNotBlank(merchant.getIpWhiteList())) {
			String[] ips = merchant.getIpWhiteList().split(",");
			boolean ipHintFlag = false;
			for (String ip : ips) {
				if (ip.equals(ipAddr)) {
					ipHintFlag = true;
					break;
				}
			}
			if (!ipHintFlag) {
				throw new BizException("ip:" + ipAddr + " 不在白名单内");
			}
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException("金额格式不正确");
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException("金额不能小于或等于0");
		}
		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + param.getNotifyUrl()
				+ merchant.getApiSecretKey();
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException("签名不正确");
		}
		return preReceiptInner(merchant.getId(), Double.parseDouble(param.getAmount()), param.getOrderNo(),
				param.getNotifyUrl());
	}

	@Transactional
	public void preReceipt(@Valid PreReceiptParam param) {
		Merchant receiptAccount = merchantRepo.getOne(param.getReceiptAccountId());
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(receiptAccount.getPayPwd())) {
			throw new BizException("支付密码不正确");
		}
		preReceiptInner(param.getReceiptAccountId(), param.getAmount(), param.getMerchantOrderNo(),
				param.getNotifyUrl());
	}

	@Transactional
	public CreatePreReceiptReturnVO preReceiptInner(String receiptAccountId, Double amount, String merchantOrderNo,
			String notifyUrl) {
		Merchant receiptAccount = merchantRepo.getOne(receiptAccountId);
		if (Constant.功能状态_禁用.equals(receiptAccount.getState())) {
			throw new BizException("账号已被禁用");
		}
		MerchantReceiptRecord receiptRecord = MerchantReceiptRecord.build(receiptAccountId, amount, merchantOrderNo,
				notifyUrl);
		merchantReceiptRecordRepo.save(receiptRecord);

		String apiGateway = systemSettingRepo.findTopByOrderByLatelyUpdateTime().getApiGateway();
		return CreatePreReceiptReturnVO.build(receiptRecord.getOrderNo(), apiGateway + receiptRecord.formatPayUrl());
	}

	@Transactional(readOnly = true)
	public MerchantTransferSubtotalVO transferSubtotal(MerchantTransferRecordQueryCondParam param) {
		MerchantTransferSubtotalVO vo = new MerchantTransferSubtotalVO();
		List<MerchantTransferRecord> orders = merchantTransferRecordRepo.findAll(param.buildSpecification());
		for (MerchantTransferRecord order : orders) {
			if (!Constant.转账记录状态_已完成.equals(order.getState())) {
				continue;
			}
			vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + order.getAmount(), 2).doubleValue());
			vo.setSuccessCount(vo.getSuccessCount() + 1);
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public List<ExportMerchantTransferDataVO> findExportTransferData(MerchantTransferRecordQueryCondParam param) {
		List<MerchantTransferRecord> result = merchantTransferRecordRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		return ExportMerchantTransferDataVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantTransferRecordVO> findTransferRecordByPage(
			@Valid MerchantTransferRecordQueryCondParam param) {
		Specification<MerchantTransferRecord> spec = param.buildSpecification();
		Page<MerchantTransferRecord> result = merchantTransferRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MerchantTransferRecordVO> pageResult = new PageResult<>(
				MerchantTransferRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void transferApi(@Valid MerchantTransferApiParam param, @NotBlank String ipAddr) {
		Merchant merchant = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getMerchantNum());
		if (merchant == null) {
			throw new BizException("请检查商户号是否正确");
		}
		if (StrUtil.isNotBlank(merchant.getIpWhiteList())) {
			String[] ips = merchant.getIpWhiteList().split(",");
			boolean ipHintFlag = false;
			for (String ip : ips) {
				if (ip.equals(ipAddr)) {
					ipHintFlag = true;
					break;
				}
			}
			if (!ipHintFlag) {
				throw new BizException("ip:" + ipAddr + " 不在白名单内");
			}
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException("金额格式不正确");
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException("金额不能小于或等于0");
		}
		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + merchant.getApiSecretKey();
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException("签名不正确");
		}
		transferInner(merchant.getId(), Double.parseDouble(param.getAmount()), param.getReceiptAddr(),
				param.getOrderNo());
	}

	@Transactional
	public void transfer(@Valid MerchantTransferParam param) {
		Merchant transferAccount = merchantRepo.getOne(param.getTransferAccountId());
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(transferAccount.getPayPwd())) {
			throw new BizException("支付密码不正确");
		}
		transferInner(param.getTransferAccountId(), param.getAmount(), param.getReceiptAddr(),
				param.getMerchantOrderNo());
	}

	@Transactional
	public void transferInner(String transferAccountId, Double amount, String receiptAddr, String merchantOrderNo) {
		Merchant transferAccount = merchantRepo.getOne(transferAccountId);
		double balance = NumberUtil.round(transferAccount.getBalance() - amount, 2).doubleValue();
		if (balance < 0) {
			throw new BizException("余额不足");
		}
		Member receiptAccount = memberRepo.findTopByWalletAddrAndDeletedFlagIsFalse(receiptAddr);
		if (receiptAccount == null) {
			throw new BizException("钱包地址无效");
		}

		MerchantTransferRecord transferRecord = MerchantTransferRecord.build(transferAccountId, amount, receiptAddr,
				merchantOrderNo);
		merchantTransferRecordRepo.save(transferRecord);

		transferAccount.setBalance(balance);
		merchantRepo.save(transferAccount);

		merchantBalanceChangeLogRepo.save(MerchantBalanceChangeLog.buildWithTransfer(transferAccount, transferRecord));

		MemberReceiptRecord receiptRecord = MemberReceiptRecord.build(receiptAccount.getId(), transferRecord.getAmount(),
				transferAccount.getWalletAddr(), Constant.转账业务类型_B2C);
		receiptRecordRepo.save(receiptRecord);
		ThreadPoolUtils.getTransferPool().schedule(() -> {
			redissonClient.getTopic(Constant.收款资金同步).publish(receiptRecord.getId());
		}, 1, TimeUnit.SECONDS);
	}

}
