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
		if (!(Constant.??????????????????_?????????.equals(order.getState()))) {
			throw new BizException("????????????????????????????????????????????????");
		}
		if (Constant.????????????_????????????.equals(order.getNoticeState())) {
			log.warn("?????????????????????;????????????{}", id);
			return Constant.?????????????????????;
		}
		if (Constant.????????????_????????????.equals(order.getNoticeState())) {
			log.warn("?????????????????????,??????????????????;????????????{}", id);
			return Constant.?????????????????????;
		}
		Merchant receiptAccount = order.getReceiptAccount();

		String state = Constant.??????????????????_?????????.equals(order.getState()) ? Constant.???????????? : Constant.????????????;
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
		// ????????????
		for (int i = 0; i < 2; i++) {
			try {
				result = HttpUtil.post(order.getNotifyUrl(), paramMap, 3500 + (i * 1000));
				if (Constant.?????????????????????.equals(result)) {
					exceptionFlag = false;
					break;
				}
			} catch (Exception e) {
				exceptionFlag = true;
				result = e.getMessage();
				log.error(MessageFormat.format("??????????????????????????????,????????????{0}", id), e);
			}
		}
		if (exceptionFlag) {
			return result;
		}
		merchantReceiptRecordRepo.updateNoticeState(order.getId(),
				Constant.?????????????????????.equals(result) ? Constant.????????????_???????????? : Constant.????????????_????????????);
		return result;
	}

	@Transactional
	public void receiptDeadline() {
		Date now = new Date();
		List<MerchantReceiptRecord> lockDeadlineOrders = merchantReceiptRecordRepo
				.findByStateAndLockDeadlineLessThan(Constant.??????????????????_?????????, now);
		for (MerchantReceiptRecord order : lockDeadlineOrders) {
			redissonClient.getTopic(Constant.???????????????????????????).publish(order.getId());
		}
		List<MerchantReceiptRecord> transferDeadlineOrders = merchantReceiptRecordRepo
				.findByStateAndTransferDeadlineLessThan(Constant.??????????????????_?????????, now);
		for (MerchantReceiptRecord order : transferDeadlineOrders) {
			redissonClient.getTopic(Constant.???????????????????????????).publish(order.getId());
		}
	}

	@Transactional
	public void merchantReceiptTimeOutUnPaid(String id) {
		MerchantReceiptRecord order = merchantReceiptRecordRepo.getOne(id);
		if (!Constant.??????????????????_?????????.equals(order.getState())) {
			return;
		}
		order.transferTimeOut();
		merchantReceiptRecordRepo.save(order);
	}

	@Transactional
	public void merchantReceiptTimeOutUnLock(String id) {
		MerchantReceiptRecord order = merchantReceiptRecordRepo.getOne(id);
		if (!Constant.??????????????????_?????????.equals(order.getState())) {
			return;
		}
		order.lockTimeOut();
		merchantReceiptRecordRepo.save(order);
	}

	@Transactional
	public void merchantReceiptFundSync() {
		List<MerchantReceiptRecord> records = merchantReceiptRecordRepo
				.findByStateAndReceiptFundSyncFalse(Constant.??????????????????_?????????);
		for (MerchantReceiptRecord record : records) {
			redissonClient.getTopic(Constant.????????????????????????).publish(record.getId());
		}
	}

	@Lock(keys = "'merchantReceiptFundSync' + #id")
	@Transactional
	public void merchantReceiptFundSync(@NotBlank String id) {
		MerchantReceiptRecord record = merchantReceiptRecordRepo.getOne(id);
		if (!Constant.??????????????????_?????????.equals(record.getState())) {
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
			if (!Constant.??????????????????_?????????.equals(order.getState())) {
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
		if (!Constant.??????????????????_?????????.equals(order.getState())) {
			throw new BizException("??????????????????");
		}
		if (!order.getTransferAddr().equals(transferAccount.getWalletAddr())) {
			throw new BizException("????????????");
		}
		transferAccount.validBasicTradeRisk();
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(transferAccount.getPayPwd())) {
			throw new BizException("?????????????????????");
		}
		double balance = NumberUtil.round(transferAccount.getBalance() - order.getAmount(), 2).doubleValue();
		if (balance < 0) {
			throw new BizException("????????????");
		}

		MemberTransferRecord transferRecord = MemberTransferRecord.build(transferAccount.getId(), order.getAmount(),
				order.getReceiptAccount().getWalletAddr(), Constant.??????????????????_C2B);
		transferRecordRepo.save(transferRecord);

		transferAccount.setBalance(balance);
		memberRepo.save(transferAccount);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithTransfer(transferAccount, transferRecord));

		order.receiptCompleted();
		merchantReceiptRecordRepo.save(order);

		ThreadPoolUtils.getTransferPool().schedule(() -> {
			redissonClient.getTopic(Constant.????????????????????????).publish(order.getId());
			redissonClient.getTopic(Constant.????????????????????????).publish(order.getId());
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
			throw new BizException("???????????????");
		}
		if (StrUtil.isNotBlank(order.getTransferAddr()) && !order.getTransferAddr().equals(member.getWalletAddr())) {
			throw new BizException("?????????????????????");
		}
		if (!Constant.??????????????????_?????????.equals(order.getState())) {
			return;
		}
		order.lockOrder(member.getWalletAddr());
		merchantReceiptRecordRepo.save(order);
	}

	@Transactional
	public CreatePreReceiptReturnVO preReceiptApi(@Valid PreReceiptApiParam param, @NotBlank String ipAddr) {
		Merchant merchant = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getMerchantNum());
		if (merchant == null) {
			throw new BizException("??????????????????????????????");
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
				throw new BizException("ip:" + ipAddr + " ??????????????????");
			}
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException("?????????????????????");
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException("???????????????????????????0");
		}
		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + param.getNotifyUrl()
				+ merchant.getApiSecretKey();
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException("???????????????");
		}
		return preReceiptInner(merchant.getId(), Double.parseDouble(param.getAmount()), param.getOrderNo(),
				param.getNotifyUrl());
	}

	@Transactional
	public void preReceipt(@Valid PreReceiptParam param) {
		Merchant receiptAccount = merchantRepo.getOne(param.getReceiptAccountId());
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(receiptAccount.getPayPwd())) {
			throw new BizException("?????????????????????");
		}
		preReceiptInner(param.getReceiptAccountId(), param.getAmount(), param.getMerchantOrderNo(),
				param.getNotifyUrl());
	}

	@Transactional
	public CreatePreReceiptReturnVO preReceiptInner(String receiptAccountId, Double amount, String merchantOrderNo,
			String notifyUrl) {
		Merchant receiptAccount = merchantRepo.getOne(receiptAccountId);
		if (Constant.????????????_??????.equals(receiptAccount.getState())) {
			throw new BizException("??????????????????");
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
			if (!Constant.??????????????????_?????????.equals(order.getState())) {
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
			throw new BizException("??????????????????????????????");
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
				throw new BizException("ip:" + ipAddr + " ??????????????????");
			}
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException("?????????????????????");
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException("???????????????????????????0");
		}
		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + merchant.getApiSecretKey();
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException("???????????????");
		}
		transferInner(merchant.getId(), Double.parseDouble(param.getAmount()), param.getReceiptAddr(),
				param.getOrderNo());
	}

	@Transactional
	public void transfer(@Valid MerchantTransferParam param) {
		Merchant transferAccount = merchantRepo.getOne(param.getTransferAccountId());
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(transferAccount.getPayPwd())) {
			throw new BizException("?????????????????????");
		}
		transferInner(param.getTransferAccountId(), param.getAmount(), param.getReceiptAddr(),
				param.getMerchantOrderNo());
	}

	@Transactional
	public void transferInner(String transferAccountId, Double amount, String receiptAddr, String merchantOrderNo) {
		Merchant transferAccount = merchantRepo.getOne(transferAccountId);
		double balance = NumberUtil.round(transferAccount.getBalance() - amount, 2).doubleValue();
		if (balance < 0) {
			throw new BizException("????????????");
		}
		Member receiptAccount = memberRepo.findTopByWalletAddrAndDeletedFlagIsFalse(receiptAddr);
		if (receiptAccount == null) {
			throw new BizException("??????????????????");
		}

		MerchantTransferRecord transferRecord = MerchantTransferRecord.build(transferAccountId, amount, receiptAddr,
				merchantOrderNo);
		merchantTransferRecordRepo.save(transferRecord);

		transferAccount.setBalance(balance);
		merchantRepo.save(transferAccount);

		merchantBalanceChangeLogRepo.save(MerchantBalanceChangeLog.buildWithTransfer(transferAccount, transferRecord));

		MemberReceiptRecord receiptRecord = MemberReceiptRecord.build(receiptAccount.getId(), transferRecord.getAmount(),
				transferAccount.getWalletAddr(), Constant.??????????????????_B2C);
		receiptRecordRepo.save(receiptRecord);
		ThreadPoolUtils.getTransferPool().schedule(() -> {
			redissonClient.getTopic(Constant.??????????????????).publish(receiptRecord.getId());
		}, 1, TimeUnit.SECONDS);
	}

}
