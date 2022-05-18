package com.c2cpay.receiptpaymentinfo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.c2cpay.common.exception.BizError;
import com.c2cpay.common.exception.BizException;
import com.c2cpay.constants.Constant;
import com.c2cpay.member.domain.Member;
import com.c2cpay.member.repo.MemberRepo;
import com.c2cpay.receiptpaymentinfo.domain.ReceiptPaymentInfo;
import com.c2cpay.receiptpaymentinfo.param.AddReceiptPaymentInfoParam;
import com.c2cpay.receiptpaymentinfo.param.ReceiptPaymentInfoQueryCondParam;
import com.c2cpay.receiptpaymentinfo.param.UpdateActivatedFlagParam;
import com.c2cpay.receiptpaymentinfo.repo.ReceiptPaymentInfoRepo;
import com.c2cpay.receiptpaymentinfo.vo.ReceiptPaymentInfoVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class ReceiptPaymentInfoService {

	@Autowired
	private ReceiptPaymentInfoRepo receiptPaymentInfoRepo;

	@Autowired
	private MemberRepo memberRepo;

	@Transactional
	public void updateActivatedFlag(@Valid UpdateActivatedFlagParam param) {
		ReceiptPaymentInfo receiptPaymentInfo = receiptPaymentInfoRepo.getOne(param.getId());
		if (!receiptPaymentInfo.getMemberId().equals(param.getMemberId())) {
			throw new BizException("操作异常");
		}
		List<ReceiptPaymentInfo> activatedPaymentReceivedInfos = receiptPaymentInfoRepo
				.findByMemberIdAndActivatedTrueAndDeletedFlagFalseOrderByActivatedTimeDesc(param.getMemberId());
		if (activatedPaymentReceivedInfos.size() >= 3 && param.getActivated()) {
			throw new BizException("最多只能激活3个收付款方式");
		}

		receiptPaymentInfo.setActivated(param.getActivated());
		receiptPaymentInfo.setActivatedTime(receiptPaymentInfo.getActivated() ? new Date() : null);
		receiptPaymentInfoRepo.save(receiptPaymentInfo);
	}

	public Specification<ReceiptPaymentInfo> buildQueryCond(ReceiptPaymentInfoQueryCondParam param) {
		Specification<ReceiptPaymentInfo> spec = new Specification<ReceiptPaymentInfo>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<ReceiptPaymentInfo> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (param.getActivated() != null) {
					predicates.add(builder.equal(root.get("activated"), param.getActivated()));
				}
				if (StrUtil.isNotBlank(param.getMemberId())) {
					predicates.add(builder.equal(root.get("memberId"), param.getMemberId()));
				}
				if (StrUtil.isNotBlank(param.getType())) {
					predicates.add(root.get("type").in(Arrays.asList(param.getType().split(","))));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional(readOnly = true)
	public List<ReceiptPaymentInfoVO> findAll(@Valid ReceiptPaymentInfoQueryCondParam param) {
		Specification<ReceiptPaymentInfo> spec = buildQueryCond(param);
		List<ReceiptPaymentInfo> receiptPaymentInfos = receiptPaymentInfoRepo.findAll(spec,
				Sort.by(Sort.Order.desc("activatedTime")));
		return ReceiptPaymentInfoVO.convertFor(receiptPaymentInfos);
	}

	@Transactional
	public void del(@NotBlank String id, @NotBlank String memberId) {
		ReceiptPaymentInfo receiptPaymentInfo = receiptPaymentInfoRepo.getOne(id);
		if (!receiptPaymentInfo.getMemberId().equals(memberId)) {
			throw new BizException("操作异常");
		}
		del(id);
	}

	@Transactional
	public void del(@NotBlank String id) {
		ReceiptPaymentInfo receiptPaymentInfo = receiptPaymentInfoRepo.getOne(id);
		receiptPaymentInfo.deleted();
		receiptPaymentInfoRepo.save(receiptPaymentInfo);
	}

	@Transactional
	public void add(@Valid AddReceiptPaymentInfoParam param) {
		ReceiptPaymentInfo receiptPaymentInfo = param.convertToPo();
		if (Constant.收付款信息_银行卡.equals(param.getType())) {
			if (StrUtil.isBlank(param.getCardNumber()) || StrUtil.isBlank(param.getBankName())) {
				throw new BizException(BizError.参数异常);
			}
		}
		if (Constant.收付款信息_微信.equals(param.getType()) || Constant.收付款信息_支付宝.equals(param.getType())) {
			if (StrUtil.isBlank(param.getAccount())) {
				throw new BizException(BizError.参数异常);
			}
			if (StrUtil.isBlank(param.getQrcode())) {
				throw new BizException(BizError.参数异常);
			}
		}
		List<ReceiptPaymentInfo> all = receiptPaymentInfoRepo
				.findByMemberIdAndDeletedFlagFalseOrderByActivatedTimeDesc(param.getMemberId());
		if (all.size() >= 10) {
			throw new BizException("最多只能添加10个收付款方式");
		}
		Member member = memberRepo.getOne(param.getMemberId());
		receiptPaymentInfo.setRealName(member.getRealName());
		receiptPaymentInfoRepo.save(receiptPaymentInfo);
	}

}
