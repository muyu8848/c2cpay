package com.c2cpay.transfer.service;

import java.util.List;
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
import com.c2cpay.log.repo.MemberBalanceChangeLogRepo;
import com.c2cpay.member.domain.Member;
import com.c2cpay.member.repo.MemberRepo;
import com.c2cpay.transfer.domain.MemberReceiptRecord;
import com.c2cpay.transfer.domain.MemberTransferRecord;
import com.c2cpay.transfer.param.ReceiptRecordQueryCondParam;
import com.c2cpay.transfer.param.TransferParam;
import com.c2cpay.transfer.param.TransferRecordQueryCondParam;
import com.c2cpay.transfer.repo.MemberReceiptRecordRepo;
import com.c2cpay.transfer.repo.MemberTransferRecordRepo;
import com.c2cpay.transfer.vo.ExportReceiptDataVO;
import com.c2cpay.transfer.vo.ExportTransferDataVO;
import com.c2cpay.transfer.vo.MemberReceiptDetailVO;
import com.c2cpay.transfer.vo.MemberReceiptRecordVO;
import com.c2cpay.transfer.vo.MemberTransferDetailVO;
import com.c2cpay.transfer.vo.MemberTransferRecordVO;
import com.c2cpay.transfer.vo.ReceiptRecordVO;
import com.c2cpay.transfer.vo.ReceiptSubtotalVO;
import com.c2cpay.transfer.vo.TransferRecordVO;
import com.c2cpay.transfer.vo.TransferSubtotalVO;
import com.zengtengpeng.annotation.Lock;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.util.NumberUtil;

@Validated
@Service
public class MemberTransferService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private MemberTransferRecordRepo transferRecordRepo;

	@Autowired
	private MemberReceiptRecordRepo receiptRecordRepo;

	@Autowired
	private MemberBalanceChangeLogRepo memberBalanceChangeLogRepo;

	@Transactional(readOnly = true)
	public MemberTransferDetailVO getTransferDetail(@NotBlank String id) {
		MemberTransferRecord transferRecord = transferRecordRepo.getOne(id);
		return MemberTransferDetailVO.convertFor(transferRecord);
	}

	@Transactional(readOnly = true)
	public TransferSubtotalVO transferSubtotal(TransferRecordQueryCondParam param) {
		TransferSubtotalVO vo = new TransferSubtotalVO();
		List<MemberTransferRecord> orders = transferRecordRepo.findAll(param.buildSpecification());
		for (MemberTransferRecord order : orders) {
			vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + order.getAmount(), 2).doubleValue());
			vo.setSuccessCount(vo.getSuccessCount() + 1);
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public List<ExportTransferDataVO> findExportTransferData(TransferRecordQueryCondParam param) {
		List<MemberTransferRecord> result = transferRecordRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		return ExportTransferDataVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<TransferRecordVO> findTransferRecordByPage(@Valid TransferRecordQueryCondParam param) {
		Page<MemberTransferRecord> result = transferRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<TransferRecordVO> pageResult = new PageResult<>(TransferRecordVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<MemberTransferRecordVO> findMemberTransferRecordByPage(
			@Valid TransferRecordQueryCondParam param) {
		Specification<MemberTransferRecord> spec = param.buildSpecification();
		Page<MemberTransferRecord> result = transferRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MemberTransferRecordVO> pageResult = new PageResult<>(
				MemberTransferRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public MemberReceiptDetailVO getReceiptDetail(@NotBlank String id) {
		MemberReceiptRecord receiptRecord = receiptRecordRepo.getOne(id);
		return MemberReceiptDetailVO.convertFor(receiptRecord);
	}

	@Transactional(readOnly = true)
	public ReceiptSubtotalVO receiptSubtotal(ReceiptRecordQueryCondParam param) {
		ReceiptSubtotalVO vo = new ReceiptSubtotalVO();
		List<MemberReceiptRecord> orders = receiptRecordRepo.findAll(param.buildSpecification());
		for (MemberReceiptRecord order : orders) {
			vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + order.getAmount(), 2).doubleValue());
			vo.setSuccessCount(vo.getSuccessCount() + 1);
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public List<ExportReceiptDataVO> findExportReceiptData(ReceiptRecordQueryCondParam param) {
		List<MemberReceiptRecord> result = receiptRecordRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		return ExportReceiptDataVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<ReceiptRecordVO> findReceiptRecordByPage(@Valid ReceiptRecordQueryCondParam param) {
		Page<MemberReceiptRecord> result = receiptRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<ReceiptRecordVO> pageResult = new PageResult<>(ReceiptRecordVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<MemberReceiptRecordVO> findMemberReceiptRecordByPage(@Valid ReceiptRecordQueryCondParam param) {
		Specification<MemberReceiptRecord> spec = param.buildSpecification();
		Page<MemberReceiptRecord> result = receiptRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MemberReceiptRecordVO> pageResult = new PageResult<>(
				MemberReceiptRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void receiptFundSync() {
		List<MemberReceiptRecord> records = receiptRecordRepo.findByStateAndReceiptFundSyncFalse(Constant.收款记录状态_已完成);
		for (MemberReceiptRecord record : records) {
			redissonClient.getTopic(Constant.收款资金同步).publish(record.getId());
		}
	}

	@Lock(keys = "'receiptFundSync' + #id")
	@Transactional
	public void receiptFundSync(@NotBlank String id) {
		MemberReceiptRecord record = receiptRecordRepo.getOne(id);
		if (!Constant.收款记录状态_已完成.equals(record.getState())) {
			return;
		}
		if (record.getReceiptFundSync()) {
			return;
		}
		record.setReceiptFundSync(true);
		receiptRecordRepo.save(record);

		Member receiptAccount = record.getReceiptAccount();
		receiptAccount.setBalance(NumberUtil.round(receiptAccount.getBalance() + record.getAmount(), 2).doubleValue());
		memberRepo.save(receiptAccount);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithReceipt(receiptAccount, record));
	}

	@Transactional
	public void transfer(@Valid TransferParam param) {
		Member transferAccount = memberRepo.getOne(param.getTransferAccountId());
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(transferAccount.getPayPwd())) {
			throw new BizException("支付密码不正确");
		}
		double balance = NumberUtil.round(transferAccount.getBalance() - param.getAmount(), 2).doubleValue();
		if (balance < 0) {
			throw new BizException("余额不足");
		}
		Member receiptAccount = memberRepo.findTopByWalletAddrAndDeletedFlagIsFalse(param.getReceiptAddr());
		if (receiptAccount == null) {
			throw new BizException("钱包地址无效");
		}
		if (transferAccount.getId().equals(receiptAccount.getId())) {
			throw new BizException("不能给自己转账");
		}

		MemberTransferRecord transferRecord = MemberTransferRecord.build(transferAccount.getId(), param.getAmount(),
				param.getReceiptAddr(), Constant.转账业务类型_C2C);
		transferRecordRepo.save(transferRecord);

		transferAccount.setBalance(balance);
		memberRepo.save(transferAccount);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithTransfer(transferAccount, transferRecord));

		MemberReceiptRecord receiptRecord = MemberReceiptRecord.build(receiptAccount.getId(), transferRecord.getAmount(),
				transferAccount.getWalletAddr(), Constant.转账业务类型_C2C);
		receiptRecordRepo.save(receiptRecord);
		ThreadPoolUtils.getTransferPool().schedule(() -> {
			redissonClient.getTopic(Constant.收款资金同步).publish(receiptRecord.getId());
		}, 1, TimeUnit.SECONDS);
	}
}
