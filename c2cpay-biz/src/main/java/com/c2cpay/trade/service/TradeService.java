package com.c2cpay.trade.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
import com.c2cpay.receiptpaymentinfo.domain.ReceiptPaymentInfo;
import com.c2cpay.receiptpaymentinfo.repo.ReceiptPaymentInfoRepo;
import com.c2cpay.trade.domain.PreTradeOrder;
import com.c2cpay.trade.domain.TradeAppealRecord;
import com.c2cpay.trade.domain.TradeChatRecord;
import com.c2cpay.trade.domain.TradeChatUnread;
import com.c2cpay.trade.domain.TradeOrder;
import com.c2cpay.trade.domain.TradeOrderStateLog;
import com.c2cpay.trade.param.AppealProcessParam;
import com.c2cpay.trade.param.BuyParam;
import com.c2cpay.trade.param.BuyerCancelTradeParam;
import com.c2cpay.trade.param.BuyerMarkPaidParam;
import com.c2cpay.trade.param.CancelPreTradeOrderParam;
import com.c2cpay.trade.param.CreatePreTradeOrderParam;
import com.c2cpay.trade.param.CustomerServiceSendMsgParam;
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
import com.c2cpay.trade.repo.PreTradeOrderRepo;
import com.c2cpay.trade.repo.TradeAppealRecordRepo;
import com.c2cpay.trade.repo.TradeChatRecordRepo;
import com.c2cpay.trade.repo.TradeChatUnreadRepo;
import com.c2cpay.trade.repo.TradeOrderRepo;
import com.c2cpay.trade.repo.TradeOrderStateLogRepo;
import com.c2cpay.trade.vo.AvailableTradeOrderVO;
import com.c2cpay.trade.vo.MemberPreTradeOrderVO;
import com.c2cpay.trade.vo.MemberTradeOrderDetailVO;
import com.c2cpay.trade.vo.MemberTradeOrderVO;
import com.c2cpay.trade.vo.PreTradeOrderSubtotalVO;
import com.c2cpay.trade.vo.PreTradeOrderVO;
import com.c2cpay.trade.vo.TradeChatRecordVO;
import com.c2cpay.trade.vo.TradeOrderStateLogVO;
import com.c2cpay.trade.vo.TradeOrderSubtotalVO;
import com.c2cpay.trade.vo.TradeOrderVO;
import com.zengtengpeng.annotation.Lock;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class TradeService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private ReceiptPaymentInfoRepo receiptPaymentInfoRepo;

	@Autowired
	private PreTradeOrderRepo preTradeOrderRepo;

	@Autowired
	private TradeOrderRepo tradeOrderRepo;

	@Autowired
	private MemberBalanceChangeLogRepo memberBalanceChangeLogRepo;

	@Autowired
	private TradeChatRecordRepo tradeChatRecordRepo;

	@Autowired
	private TradeChatUnreadRepo tradeChatUnreadRepo;

	@Autowired
	private TradeAppealRecordRepo tradeAppealRecordRepo;

	@Autowired
	private TradeOrderStateLogRepo tradeOrderStateLogRepo;

	@Transactional
	public void orderDeadline() {
		Date now = new Date();
		List<TradeOrder> tradeOrders = tradeOrderRepo.findByStateAndOrderDeadlineLessThan(Constant.??????????????????_?????????, now);
		for (TradeOrder tradeOrder : tradeOrders) {
			cancel(tradeOrder.getId(), Constant.??????????????????_???????????????, Constant.?????????_??????);
		}
	}

	@Transactional
	public void tradeDeadline() {
		Date now = new Date();
		List<TradeOrder> tradeOrders = tradeOrderRepo.findByStateAndTradeDeadlineLessThan(Constant.??????????????????_?????????, now);
		for (TradeOrder tradeOrder : tradeOrders) {
			cancel(tradeOrder.getId(), Constant.??????????????????_?????????, Constant.?????????_??????);
		}
	}

	@Transactional
	public void appealProcess(@Valid AppealProcessParam param) {
		updateAppealProcessWay(param.getTradeOrderId(), param.getProcessWay(), param.getApproveAppeal());
		if (Constant.??????????????????_???????????????.equals(param.getProcessWay())) {
			confirmTradeCompleted(param.getTradeOrderId(), Constant.?????????_??????);
		} else if (Constant.??????????????????_????????????.equals(param.getProcessWay())) {
			cancel(param.getTradeOrderId(), Constant.??????????????????_?????????, Constant.?????????_??????);
		}
	}

	@Transactional
	public void initiatorCancelAppeal(@Valid InitiatorCancelAppealParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		TradeAppealRecord appealRecord = tradeOrder.getAppealRecord();
		if (!param.getInitiatorId().equals(appealRecord.getInitiatorId())) {
			throw new BizException("?????????????????????????????????");
		}
		updateAppealProcessWay(tradeOrder.getId(), Constant.??????????????????_????????????, false);
	}

	@Lock(keys = "'updateAppealProcessWay' + #tradeOrderId")
	@Transactional
	public void updateAppealProcessWay(String tradeOrderId, String processWay, Boolean approveAppeal) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(tradeOrderId);
		TradeAppealRecord appealRecord = tradeOrder.getAppealRecord();
		if (!Constant.??????????????????_?????????.equals(tradeOrder.getState())) {
			throw new BizException("???????????????????????????");
		}
		if (!Constant.????????????_?????????.equals(appealRecord.getState())) {
			throw new BizException("??????????????????");
		}
		appealRecord.setState(Constant.????????????_?????????);
		appealRecord.setProcessWay(processWay);
		appealRecord.setProcessTime(new Date());
		appealRecord.setApproveAppeal(approveAppeal);
		tradeAppealRecordRepo.save(appealRecord);

		tradeOrder.setState(Constant.??????????????????_?????????);
		tradeOrder.setAppealRecordId(null);
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());

		if (approveAppeal) {
			if (Constant.??????????????????_???????????????.equals(appealRecord.getAppealType())) {
				publishRiskMq(Constant.????????????_???????????????, appealRecord.getDefendantId());
			}
			if (Constant.??????????????????_?????????????????????????????????.equals(appealRecord.getAppealType())) {
				publishRiskMq(Constant.????????????_?????????????????????????????????, appealRecord.getDefendantId());
			}
			if (Constant.??????????????????_????????????????????????.equals(appealRecord.getAppealType())) {
				publishRiskMq(Constant.????????????_????????????????????????, appealRecord.getDefendantId());
			}
		}
	}

	public void publishRiskMq(String topic, String value) {
		ThreadPoolUtils.getRiskPool().schedule(() -> {
			redissonClient.getTopic(topic).publish(value);
		}, 1, TimeUnit.SECONDS);
	}

	@Lock(keys = "'initiateAppeal' + #param.tradeOrderId")
	@Transactional
	public void initiateAppeal(@Valid InitiateAppealParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!(param.getInitiatorId().equals(tradeOrder.getSellerId())
				|| param.getInitiatorId().equals(tradeOrder.getBuyerId()))) {
			throw new BizException("????????????");
		}
		if (!Constant.??????????????????_?????????.equals(tradeOrder.getState())) {
			throw new BizException("???????????????????????????????????????");
		}
		if (Constant.??????????????????_?????????.equals(tradeOrder.getState())) {
			throw new BizException("????????????????????????");
		}
		String initiator = param.getInitiatorId().equals(tradeOrder.getSellerId()) ? Constant.?????????_?????? : Constant.?????????_??????;
		if (Constant.?????????_??????.equals(initiator) && !Constant.??????????????????.contains(param.getAppealType())) {
			throw new BizException("??????????????????");
		}
		if (Constant.?????????_??????.equals(initiator) && !Constant.??????????????????.contains(param.getAppealType())) {
			throw new BizException("??????????????????");
		}
		TradeAppealRecord lastAppealRecord = tradeAppealRecordRepo
				.findTopByTradeOrderIdOrderByProcessTimeDesc(tradeOrder.getId());
		if (lastAppealRecord != null
				&& DateUtil.between(new Date(), lastAppealRecord.getProcessTime(), DateUnit.SECOND) < 300) {
			throw new BizException("????????????5??????????????????????????????");
		}

		TradeAppealRecord appealRecord = param
				.convertToPo(Constant.?????????_??????.equals(initiator) ? tradeOrder.getBuyerId() : tradeOrder.getSellerId());
		tradeAppealRecordRepo.save(appealRecord);

		tradeOrder.setState(Constant.??????????????????_?????????);
		tradeOrder.setAppealRecordId(appealRecord.getId());
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());
	}

	@Transactional
	public Long getUnreadChatCount(@NotBlank String tradeOrderId, @NotBlank String receiverId) {
		return tradeChatUnreadRepo.countByTradeOrderIdAndReceiverIdAndUnreadFlagIsTrue(tradeOrderId, receiverId);
	}

	@Transactional
	public void chatRecordMarkRead(@NotBlank String tradeOrderId, @NotBlank String receiverId) {
		List<TradeChatUnread> unreadChatRecords = tradeChatUnreadRepo
				.findByTradeOrderIdAndReceiverIdAndUnreadFlagIsTrue(tradeOrderId, receiverId);
		for (TradeChatUnread chatRecord : unreadChatRecords) {
			chatRecordMarkRead(chatRecord);
		}
	}

	@Transactional
	public void chatRecordMarkRead(TradeChatUnread chatRecord) {
		chatRecord.setUnreadFlag(false);
		chatRecord.setReadTime(new Date());
		tradeChatUnreadRepo.save(chatRecord);
	}

	@Transactional(readOnly = true)
	public List<TradeChatRecordVO> findMemberChatRecord(@Valid TradeChatRecordQueryCondParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!(param.getMemberId().equals(tradeOrder.getSellerId())
				|| param.getMemberId().equals(tradeOrder.getBuyerId()))) {
			throw new BizException("????????????");
		}
		return findChatRecord(param);
	}

	@Transactional(readOnly = true)
	public List<TradeChatRecordVO> findChatRecord(@Valid TradeChatRecordQueryCondParam param) {
		Specification<TradeChatRecord> spec = buildQueryCond(param);
		List<TradeChatRecord> result = tradeChatRecordRepo.findAll(spec, Sort.by(Sort.Order.asc("createTime")));
		return TradeChatRecordVO.convertFor(result, new Date());
	}

	public Specification<TradeChatRecord> buildQueryCond(TradeChatRecordQueryCondParam param) {
		Specification<TradeChatRecord> spec = new Specification<TradeChatRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<TradeChatRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (StrUtil.isNotBlank(param.getTradeOrderId())) {
					predicates.add(builder.equal(root.get("tradeOrderId"), param.getTradeOrderId()));
				}
				if (param.getLastTimeStamp() != null) {
					Date lastTime = new Date(param.getLastTimeStamp());
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), lastTime));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional
	public void recallMsg(@NotBlank String tradeChatRecordId) {
		TradeChatRecord tradeChatRecord = tradeChatRecordRepo.getOne(tradeChatRecordId);
		tradeChatRecord.deleted();
		tradeChatRecordRepo.save(tradeChatRecord);

		List<TradeChatUnread> unreads = tradeChatUnreadRepo
				.findByTradeChatRecordIdAndUnreadFlagIsTrue(tradeChatRecord.getId());
		for (TradeChatUnread unread : unreads) {
			chatRecordMarkRead(unread);
		}
	}

	@Transactional
	public void customerServiceSendMsg(@Valid CustomerServiceSendMsgParam param) {
		TradeOrder order = tradeOrderRepo.getOne(param.getTradeOrderId());
		TradeChatRecord tradeChatRecord = param.convertToPo();
		tradeChatRecord.setSenderId(Constant.?????????_??????);
		tradeChatRecordRepo.save(tradeChatRecord);

		tradeChatUnreadRepo.save(TradeChatUnread.build(tradeChatRecord.getId(), order.getSellerId(), order.getId()));
		tradeChatUnreadRepo.save(TradeChatUnread.build(tradeChatRecord.getId(), order.getBuyerId(), order.getId()));
	}

	@Transactional
	public void sendMsg(@Valid SendMsgParam param) {
		TradeOrder order = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!(param.getSenderId().equals(order.getBuyerId()) || param.getSenderId().equals(order.getSellerId()))) {
			throw new BizException("????????????");
		}
		String receiverId = param.getSenderId().equals(order.getBuyerId()) ? order.getSellerId() : order.getBuyerId();
		TradeChatRecord tradeChatRecord = param.convertToPo(receiverId);
		tradeChatRecordRepo.save(tradeChatRecord);

		tradeChatUnreadRepo.save(TradeChatUnread.build(tradeChatRecord.getId(), receiverId, order.getId()));
	}

	@Transactional(readOnly = true)
	public PreTradeOrderSubtotalVO preTradeOrderSubtotal(PreTradeOrderQueryCondParam param) {
		PreTradeOrderSubtotalVO vo = new PreTradeOrderSubtotalVO();
		List<PreTradeOrder> orders = preTradeOrderRepo.findAll(param.buildSpecification());
		for (PreTradeOrder order : orders) {
			vo.setTotalAmount(NumberUtil.round(vo.getTotalAmount() + order.getAmount(), 2).doubleValue());
			vo.setSuccessAmount(NumberUtil
					.round(vo.getSuccessAmount() + order.getAmount() - order.getAvailableAmount(), 2).doubleValue());
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public PageResult<PreTradeOrderVO> findPreTradeOrderByPage(@Valid PreTradeOrderQueryCondParam param) {
		Page<PreTradeOrder> result = preTradeOrderRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<PreTradeOrderVO> pageResult = new PageResult<>(PreTradeOrderVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public List<TradeOrderStateLogVO> findTradeOrderStateLog(@NotBlank String tradeOrderId) {
		List<TradeOrderStateLog> logs = tradeOrderStateLogRepo.findByTradeOrderIdOrderByLogTimeAsc(tradeOrderId);
		return TradeOrderStateLogVO.convertFor(logs);
	}

	@Transactional(readOnly = true)
	public PageResult<MemberPreTradeOrderVO> findMemberPreTradeOrderByPage(@Valid PreTradeOrderQueryCondParam param) {
		Page<PreTradeOrder> result = preTradeOrderRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MemberPreTradeOrderVO> pageResult = new PageResult<>(
				MemberPreTradeOrderVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public TradeOrderSubtotalVO tradeOrderSubtotal(TradeOrderQueryCondParam param) {
		TradeOrderSubtotalVO vo = new TradeOrderSubtotalVO();
		List<TradeOrder> orders = tradeOrderRepo.findAll(param.buildSpecification());
		for (TradeOrder order : orders) {
			vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + order.getAmount(), 2).doubleValue());
			vo.setSuccessCount(vo.getSuccessCount() + 1);
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public PageResult<TradeOrderVO> findTradeOrderByPage(@Valid TradeOrderQueryCondParam param) {
		Page<TradeOrder> result = tradeOrderRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<TradeOrderVO> pageResult = new PageResult<>(TradeOrderVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<MemberTradeOrderVO> findMemberTradeOrderByPage(@Valid TradeOrderQueryCondParam param) {
		Page<TradeOrder> result = tradeOrderRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MemberTradeOrderVO> pageResult = new PageResult<>(
				MemberTradeOrderVO.convertFor(result.getContent(), param.getMemberId()), param.getPageNum(),
				param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<AvailableTradeOrderVO> findAvailableTradeOrderByPage(@Valid PreTradeOrderQueryCondParam param) {
		param.setState(Constant.?????????????????????_?????????);
		param.setAvailableAmount(0d);
		Page<PreTradeOrder> result = preTradeOrderRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<AvailableTradeOrderVO> pageResult = new PageResult<>(
				AvailableTradeOrderVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void cancelPreTradeOrder(@Valid CancelPreTradeOrderParam param) {
		PreTradeOrder preTradeOrder = preTradeOrderRepo.getOne(param.getPreTradeOrderId());
		if (!Constant.?????????????????????_?????????.equals(preTradeOrder.getState())) {
			throw new BizException("????????????");
		}
		List<TradeOrder> tradeOrders = tradeOrderRepo.findByPreTradeOrderId(param.getPreTradeOrderId());
		for (TradeOrder tradeOrder : tradeOrders) {
			if (!(Constant.??????????????????_?????????.equals(tradeOrder.getState())
					|| Constant.??????????????????_?????????.equals(tradeOrder.getState()))) {
				throw new BizException("?????????????????????????????????,????????????????????????????????????");
			}
		}
		preTradeOrder.setState(Constant.?????????????????????_?????????);
		preTradeOrder.setCancelTime(new Date());
		preTradeOrderRepo.save(preTradeOrder);

		if (Constant.??????????????????_??????.equals(preTradeOrder.getTradeType())) {
			Member member = preTradeOrder.getMember();
			Double availableAmount = preTradeOrder.getAvailableAmount();
			member.setBalance(NumberUtil.round(member.getBalance() + availableAmount, 2).doubleValue());
			member.setFreezeFund(NumberUtil.round(member.getFreezeFund() - availableAmount, 2).doubleValue());
			memberRepo.save(member);

			memberBalanceChangeLogRepo
					.save(MemberBalanceChangeLog.buildWithCancelPreTradeOrderSell(member, preTradeOrder));
		}
	}

	@Transactional
	public void createPreTradeOrder(@Valid CreatePreTradeOrderParam param) {
		Member member = memberRepo.getOne(param.getMemberId());
		if (Constant.??????????????????_??????.equals(param.getTradeType())) {
			member.validBuyRisk();
		}
		if (Constant.??????????????????_??????.equals(param.getTradeType())) {
			member.validSellRisk();
		}
		if (param.getMinAmount() != null) {
			if (NumberUtil.round(param.getAmount() - param.getMinAmount(), 2).doubleValue() < 0) {
				throw new BizException("????????????");
			}
		} else {
			param.setMinAmount(param.getAmount());
		}
		if (Constant.??????????????????_??????.equals(param.getTradeType())
				&& NumberUtil.round(member.getBalance() - param.getAmount(), 2).doubleValue() < 0) {
			throw new BizException("????????????");
		}
		PreTradeOrder preTradeOrder = param.convertToPo();
		preTradeOrderRepo.save(preTradeOrder);

		if (Constant.??????????????????_??????.equals(param.getTradeType())) {
			double balance = NumberUtil.round(member.getBalance() - param.getAmount(), 2).doubleValue();
			member.setBalance(balance);
			member.setFreezeFund(NumberUtil.round(member.getFreezeFund() + param.getAmount(), 2).doubleValue());
			memberRepo.save(member);

			memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithPreTradeOrderSell(member, preTradeOrder));
		}
	}

	@Transactional(readOnly = true)
	public MemberTradeOrderDetailVO getMemberTradeOrderDetail(@NotBlank String id) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(id);
		return MemberTradeOrderDetailVO.convertFor(tradeOrder);
	}

	@Transactional
	public void sellerConfirmTradeCompleted(@Valid SellerConfirmTradeCompletedParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!param.getSellerId().equals(tradeOrder.getSellerId())) {
			throw new BizException("????????????");
		}
		if (!Constant.??????????????????_?????????.equals(tradeOrder.getState())) {
			throw new BizException("??????????????????");
		}
		Member seller = tradeOrder.getSeller();
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(seller.getPayPwd())) {
			throw new BizException("?????????????????????");
		}
		confirmTradeCompleted(tradeOrder.getId(), Constant.?????????_??????);
	}

	public void confirmTradeCompleted(String tradeOrderId, String endOperator) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(tradeOrderId);
		tradeOrder.setState(Constant.??????????????????_?????????);
		tradeOrder.setFinishTime(new Date());
		tradeOrder.setFinishOperator(endOperator);
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());

		tradeChatRecordRepo.save(tradeOrder.buildStateChangeChatRecord());

		PreTradeOrder preTradeOrder = tradeOrder.getPreTradeOrder();
		if (preTradeOrder != null && preTradeOrder.getAvailableAmount() == 0) {
			preTradeOrder.setState(Constant.?????????????????????_?????????);
			preTradeOrder.setCompletedTime(new Date());
			preTradeOrderRepo.save(preTradeOrder);
		}
		if (preTradeOrder != null && Constant.??????????????????_??????.equals(preTradeOrder.getTradeType())) {
			Member seller = preTradeOrder.getMember();
			seller.setFreezeFund(NumberUtil.round(seller.getFreezeFund() - tradeOrder.getAmount(), 2).doubleValue());
			memberRepo.save(seller);
		}
		Member buyer = tradeOrder.getBuyer();
		buyer.setBalance(NumberUtil.round(buyer.getBalance() + tradeOrder.getAmount(), 2).doubleValue());
		memberRepo.save(buyer);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithBuy(buyer, tradeOrder));
	}

	@Transactional
	public void buyerMarkPaid(@Valid BuyerMarkPaidParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!param.getBuyerId().equals(tradeOrder.getBuyerId())) {
			throw new BizException("????????????");
		}
		if (!Constant.??????????????????_?????????.equals(tradeOrder.getState())) {
			throw new BizException("??????????????????");
		}
		if (StrUtil.isNotBlank(param.getPaymentCertificate())) {
			tradeOrder.setPaymentCertificate(param.getPaymentCertificate());
		}
		tradeOrder.setState(Constant.??????????????????_?????????);
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());

		tradeChatRecordRepo.save(tradeOrder.buildStateChangeChatRecord());
	}

	@Transactional
	public void buyerCancelTrade(@Valid BuyerCancelTradeParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!param.getBuyerId().equals(tradeOrder.getBuyerId())) {
			throw new BizException("????????????");
		}
		if (!(Constant.??????????????????_?????????.equals(tradeOrder.getState()) || Constant.??????????????????_?????????.equals(tradeOrder.getState())
				|| Constant.??????????????????_?????????.equals(tradeOrder.getState()))) {
			throw new BizException("??????????????????");
		}
		cancel(tradeOrder.getId(), Constant.??????????????????_?????????, Constant.?????????_??????);
	}

	@Transactional
	public void sellerRejectOrder(@Valid SellerRejectOrderParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!param.getSellerId().equals(tradeOrder.getSellerId())) {
			throw new BizException("????????????");
		}
		if (!Constant.??????????????????_?????????.equals(tradeOrder.getState())) {
			throw new BizException("??????????????????");
		}
		cancel(tradeOrder.getId(), Constant.??????????????????_???????????????, Constant.?????????_??????);
	}

	public void cancel(String tradeOrderId, String state, String endOperator) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(tradeOrderId);
		tradeOrder.setState(state);
		tradeOrder.setFinishTime(new Date());
		tradeOrder.setFinishOperator(endOperator);
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());

		tradeChatRecordRepo.save(tradeOrder.buildStateChangeChatRecord());

		PreTradeOrder preTradeOrder = tradeOrder.getPreTradeOrder();
		if (preTradeOrder != null) {
			double availableAmount = NumberUtil.round(preTradeOrder.getAvailableAmount() + tradeOrder.getAmount(), 2)
					.doubleValue();
			preTradeOrder.setAvailableAmount(availableAmount);
			preTradeOrderRepo.save(preTradeOrder);
		}

		if (preTradeOrder != null && Constant.??????????????????_??????.equals(preTradeOrder.getTradeType())) {
			Member seller = tradeOrder.getSeller();
			double sellerBalance = NumberUtil.round(seller.getBalance() + tradeOrder.getAmount(), 2).doubleValue();
			seller.setBalance(sellerBalance);
			memberRepo.save(seller);

			memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithCancelTrade(seller, tradeOrder));
		}

		if (Constant.??????????????????_?????????.equals(tradeOrder.getState())) {
			publishRiskMq(Constant.????????????_??????????????????, tradeOrder.getBuyerId());
		}
		if (Constant.??????????????????_???????????????.equals(tradeOrder.getState())) {
			publishRiskMq(Constant.????????????_??????????????????, tradeOrder.getSellerId());
		}
	}

	@Transactional
	public void sellerConfirmOrder(@Valid SellerConfirmOrderParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!param.getSellerId().equals(tradeOrder.getSellerId())) {
			throw new BizException("????????????");
		}
		if (!Constant.??????????????????_?????????.equals(tradeOrder.getState())) {
			throw new BizException("??????????????????");
		}
		tradeOrder.setState(Constant.??????????????????_?????????);
		tradeOrder.setTradeDeadline(DateUtil.offset(new Date(), DateField.MINUTE, 10));
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());

		tradeChatRecordRepo.save(tradeOrder.buildStateChangeChatRecord());
	}

	@Transactional
	public String createBuyOrder(@Valid BuyParam param) {
		Member buyer = memberRepo.getOne(param.getBuyerId());
		buyer.validBuyRisk();
		PreTradeOrder preTradeOrder = preTradeOrderRepo.getOne(param.getPreTradeOrderId());
		if (!Constant.?????????????????????_?????????.equals(preTradeOrder.getState())) {
			throw new BizException("????????????");
		}
		if (!Constant.??????????????????_??????.equals(preTradeOrder.getTradeType())) {
			throw new BizException("???????????????????????????");
		}
		double availableAmount = NumberUtil.round(preTradeOrder.getAvailableAmount() - param.getAmount(), 2)
				.doubleValue();
		if (availableAmount < 0) {
			throw new BizException("???????????????");
		}
		if (preTradeOrder.getMemberId().equals(param.getBuyerId())) {
			throw new BizException("????????????????????????");
		}
		ReceiptPaymentInfo paymentInfo = receiptPaymentInfoRepo.getOne(param.getPaymentInfoId());
		if (!paymentInfo.getMemberId().equals(param.getBuyerId())) {
			throw new BizException("????????????");
		}
		if (!preTradeOrder.getReceiptPaymentType().contains(paymentInfo.getType())) {
			throw new BizException("?????????????????????");
		}
		String receiptPaymentType = paymentInfo.getType();
		ReceiptPaymentInfo receiptInfo = receiptPaymentInfoRepo
				.findTopByMemberIdAndTypeAndActivatedTrueAndDeletedFlagIsFalseOrderByActivatedTimeDesc(
						preTradeOrder.getMemberId(), receiptPaymentType);
		if (receiptInfo == null) {
			throw new BizException("???????????????????????????");
		}

		preTradeOrder.setAvailableAmount(availableAmount);
		preTradeOrderRepo.save(preTradeOrder);

		TradeOrder order = param.convertToPo();
		order.setOrderDeadline(DateUtil.offset(order.getCreateTime(), DateField.MINUTE, 10));
		order.setReceiptPaymentType(receiptPaymentType);
		order.setSellerId(preTradeOrder.getMemberId());
		order.setReceiptInfoId(receiptInfo.getId());
		tradeOrderRepo.save(order);

		tradeOrderStateLogRepo.save(order.buildStateLog());

		tradeChatRecordRepo.save(order.buildStateChangeChatRecord());

		return order.getId();
	}

	@Transactional
	public String createSellOrder(@Valid SellParam param) {
		Member seller = memberRepo.getOne(param.getSellerId());
		seller.validSellRisk();
		double sellerBalance = NumberUtil.round(seller.getBalance() - param.getAmount(), 2).doubleValue();
		if (sellerBalance < 0) {
			throw new BizException("????????????");
		}
		PreTradeOrder preTradeOrder = preTradeOrderRepo.getOne(param.getPreTradeOrderId());
		if (!Constant.?????????????????????_?????????.equals(preTradeOrder.getState())) {
			throw new BizException("????????????");
		}
		if (!Constant.??????????????????_??????.equals(preTradeOrder.getTradeType())) {
			throw new BizException("???????????????????????????");
		}
		double availableAmount = NumberUtil.round(preTradeOrder.getAvailableAmount() - param.getAmount(), 2)
				.doubleValue();
		if (availableAmount < 0) {
			throw new BizException("???????????????");
		}
		if (preTradeOrder.getMemberId().equals(param.getSellerId())) {
			throw new BizException("????????????????????????");
		}
		ReceiptPaymentInfo receiptInfo = receiptPaymentInfoRepo.getOne(param.getReceiptInfoId());
		if (!receiptInfo.getMemberId().equals(param.getSellerId())) {
			throw new BizException("????????????");
		}
		if (!preTradeOrder.getReceiptPaymentType().contains(receiptInfo.getType())) {
			throw new BizException("?????????????????????");
		}
		String receiptPaymentType = receiptInfo.getType();
		ReceiptPaymentInfo paymentInfo = receiptPaymentInfoRepo
				.findTopByMemberIdAndTypeAndActivatedTrueAndDeletedFlagIsFalseOrderByActivatedTimeDesc(
						preTradeOrder.getMemberId(), receiptPaymentType);
		if (paymentInfo == null) {
			throw new BizException("???????????????????????????");
		}

		preTradeOrder.setAvailableAmount(availableAmount);
		preTradeOrderRepo.save(preTradeOrder);

		TradeOrder order = param.convertToPo();
		order.setOrderDeadline(DateUtil.offset(order.getCreateTime(), DateField.MINUTE, 10));
		order.setReceiptPaymentType(receiptPaymentType);
		order.setBuyerId(preTradeOrder.getMemberId());
		order.setPaymentInfoId(paymentInfo.getId());
		tradeOrderRepo.save(order);

		tradeOrderStateLogRepo.save(order.buildStateLog());

		seller.setBalance(sellerBalance);
		memberRepo.save(seller);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithSell(seller, order));

		tradeChatRecordRepo.save(order.buildStateChangeChatRecord());

		return order.getId();
	}

}
