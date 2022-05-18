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
		List<TradeOrder> tradeOrders = tradeOrderRepo.findByStateAndOrderDeadlineLessThan(Constant.交易订单状态_待接单, now);
		for (TradeOrder tradeOrder : tradeOrders) {
			cancel(tradeOrder.getId(), Constant.交易订单状态_接单已取消, Constant.操作方_系统);
		}
	}

	@Transactional
	public void tradeDeadline() {
		Date now = new Date();
		List<TradeOrder> tradeOrders = tradeOrderRepo.findByStateAndTradeDeadlineLessThan(Constant.交易订单状态_未付款, now);
		for (TradeOrder tradeOrder : tradeOrders) {
			cancel(tradeOrder.getId(), Constant.交易订单状态_已取消, Constant.操作方_系统);
		}
	}

	@Transactional
	public void appealProcess(@Valid AppealProcessParam param) {
		updateAppealProcessWay(param.getTradeOrderId(), param.getProcessWay(), param.getApproveAppeal());
		if (Constant.申诉处理方式_已完成交易.equals(param.getProcessWay())) {
			confirmTradeCompleted(param.getTradeOrderId(), Constant.操作方_客服);
		} else if (Constant.申诉处理方式_取消交易.equals(param.getProcessWay())) {
			cancel(param.getTradeOrderId(), Constant.交易订单状态_已取消, Constant.操作方_客服);
		}
	}

	@Transactional
	public void initiatorCancelAppeal(@Valid InitiatorCancelAppealParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		TradeAppealRecord appealRecord = tradeOrder.getAppealRecord();
		if (!param.getInitiatorId().equals(appealRecord.getInitiatorId())) {
			throw new BizException("不是发起人无权撤销申诉");
		}
		updateAppealProcessWay(tradeOrder.getId(), Constant.申诉处理方式_撤销申诉, false);
	}

	@Lock(keys = "'updateAppealProcessWay' + #tradeOrderId")
	@Transactional
	public void updateAppealProcessWay(String tradeOrderId, String processWay, Boolean approveAppeal) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(tradeOrderId);
		TradeAppealRecord appealRecord = tradeOrder.getAppealRecord();
		if (!Constant.交易订单状态_申诉中.equals(tradeOrder.getState())) {
			throw new BizException("该订单没有在申诉中");
		}
		if (!Constant.申诉状态_待处理.equals(appealRecord.getState())) {
			throw new BizException("申诉状态异常");
		}
		appealRecord.setState(Constant.申诉状态_已完成);
		appealRecord.setProcessWay(processWay);
		appealRecord.setProcessTime(new Date());
		appealRecord.setApproveAppeal(approveAppeal);
		tradeAppealRecordRepo.save(appealRecord);

		tradeOrder.setState(Constant.交易订单状态_已付款);
		tradeOrder.setAppealRecordId(null);
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());

		if (approveAppeal) {
			if (Constant.交易申诉类型_买方未付款.equals(appealRecord.getAppealType())) {
				publishRiskMq(Constant.风控原因_买方未付款, appealRecord.getDefendantId());
			}
			if (Constant.交易申诉类型_使用非本人支付账户付款.equals(appealRecord.getAppealType())) {
				publishRiskMq(Constant.风控原因_使用非本人支付账户付款, appealRecord.getDefendantId());
			}
			if (Constant.交易申诉类型_已付款卖方未放行.equals(appealRecord.getAppealType())) {
				publishRiskMq(Constant.风控原因_已付款卖方未放行, appealRecord.getDefendantId());
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
			throw new BizException("操作异常");
		}
		if (!Constant.交易订单状态_已付款.equals(tradeOrder.getState())) {
			throw new BizException("非进行中的订单不能发起申诉");
		}
		if (Constant.交易订单状态_申诉中.equals(tradeOrder.getState())) {
			throw new BizException("该订单已在申诉中");
		}
		String initiator = param.getInitiatorId().equals(tradeOrder.getSellerId()) ? Constant.操作方_卖方 : Constant.操作方_买方;
		if (Constant.操作方_卖方.equals(initiator) && !Constant.卖方申诉类型.contains(param.getAppealType())) {
			throw new BizException("未支持该申诉");
		}
		if (Constant.操作方_买方.equals(initiator) && !Constant.买方申诉类型.contains(param.getAppealType())) {
			throw new BizException("未支持该申诉");
		}
		TradeAppealRecord lastAppealRecord = tradeAppealRecordRepo
				.findTopByTradeOrderIdOrderByProcessTimeDesc(tradeOrder.getId());
		if (lastAppealRecord != null
				&& DateUtil.between(new Date(), lastAppealRecord.getProcessTime(), DateUnit.SECOND) < 300) {
			throw new BizException("需要等待5分钟才能再次提交申诉");
		}

		TradeAppealRecord appealRecord = param
				.convertToPo(Constant.操作方_卖方.equals(initiator) ? tradeOrder.getBuyerId() : tradeOrder.getSellerId());
		tradeAppealRecordRepo.save(appealRecord);

		tradeOrder.setState(Constant.交易订单状态_申诉中);
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
			throw new BizException("操作异常");
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
		tradeChatRecord.setSenderId(Constant.操作方_客服);
		tradeChatRecordRepo.save(tradeChatRecord);

		tradeChatUnreadRepo.save(TradeChatUnread.build(tradeChatRecord.getId(), order.getSellerId(), order.getId()));
		tradeChatUnreadRepo.save(TradeChatUnread.build(tradeChatRecord.getId(), order.getBuyerId(), order.getId()));
	}

	@Transactional
	public void sendMsg(@Valid SendMsgParam param) {
		TradeOrder order = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!(param.getSenderId().equals(order.getBuyerId()) || param.getSenderId().equals(order.getSellerId()))) {
			throw new BizException("操作异常");
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
		param.setState(Constant.预交易订单状态_进行中);
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
		if (!Constant.预交易订单状态_进行中.equals(preTradeOrder.getState())) {
			throw new BizException("订单无效");
		}
		List<TradeOrder> tradeOrders = tradeOrderRepo.findByPreTradeOrderId(param.getPreTradeOrderId());
		for (TradeOrder tradeOrder : tradeOrders) {
			if (!(Constant.交易订单状态_已完成.equals(tradeOrder.getState())
					|| Constant.交易订单状态_已取消.equals(tradeOrder.getState()))) {
				throw new BizException("该挂单存在交易中的订单,请先处理完再进行撤单操作");
			}
		}
		preTradeOrder.setState(Constant.预交易订单状态_已撤单);
		preTradeOrder.setCancelTime(new Date());
		preTradeOrderRepo.save(preTradeOrder);

		if (Constant.订单交易类型_出售.equals(preTradeOrder.getTradeType())) {
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
		if (Constant.订单交易类型_购买.equals(param.getTradeType())) {
			member.validBuyRisk();
		}
		if (Constant.订单交易类型_出售.equals(param.getTradeType())) {
			member.validSellRisk();
		}
		if (param.getMinAmount() != null) {
			if (NumberUtil.round(param.getAmount() - param.getMinAmount(), 2).doubleValue() < 0) {
				throw new BizException("操作异常");
			}
		} else {
			param.setMinAmount(param.getAmount());
		}
		if (Constant.订单交易类型_出售.equals(param.getTradeType())
				&& NumberUtil.round(member.getBalance() - param.getAmount(), 2).doubleValue() < 0) {
			throw new BizException("余额不足");
		}
		PreTradeOrder preTradeOrder = param.convertToPo();
		preTradeOrderRepo.save(preTradeOrder);

		if (Constant.订单交易类型_出售.equals(param.getTradeType())) {
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
			throw new BizException("操作异常");
		}
		if (!Constant.交易订单状态_已付款.equals(tradeOrder.getState())) {
			throw new BizException("订单状态异常");
		}
		Member seller = tradeOrder.getSeller();
		if (!SaSecureUtil.sha256(param.getPayPwd()).equals(seller.getPayPwd())) {
			throw new BizException("支付密码不正确");
		}
		confirmTradeCompleted(tradeOrder.getId(), Constant.操作方_卖方);
	}

	public void confirmTradeCompleted(String tradeOrderId, String endOperator) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(tradeOrderId);
		tradeOrder.setState(Constant.交易订单状态_已完成);
		tradeOrder.setFinishTime(new Date());
		tradeOrder.setFinishOperator(endOperator);
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());

		tradeChatRecordRepo.save(tradeOrder.buildStateChangeChatRecord());

		PreTradeOrder preTradeOrder = tradeOrder.getPreTradeOrder();
		if (preTradeOrder != null && preTradeOrder.getAvailableAmount() == 0) {
			preTradeOrder.setState(Constant.预交易订单状态_已完成);
			preTradeOrder.setCompletedTime(new Date());
			preTradeOrderRepo.save(preTradeOrder);
		}
		if (preTradeOrder != null && Constant.订单交易类型_出售.equals(preTradeOrder.getTradeType())) {
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
			throw new BizException("操作异常");
		}
		if (!Constant.交易订单状态_未付款.equals(tradeOrder.getState())) {
			throw new BizException("订单状态异常");
		}
		if (StrUtil.isNotBlank(param.getPaymentCertificate())) {
			tradeOrder.setPaymentCertificate(param.getPaymentCertificate());
		}
		tradeOrder.setState(Constant.交易订单状态_已付款);
		tradeOrderRepo.save(tradeOrder);

		tradeOrderStateLogRepo.save(tradeOrder.buildStateLog());

		tradeChatRecordRepo.save(tradeOrder.buildStateChangeChatRecord());
	}

	@Transactional
	public void buyerCancelTrade(@Valid BuyerCancelTradeParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!param.getBuyerId().equals(tradeOrder.getBuyerId())) {
			throw new BizException("操作异常");
		}
		if (!(Constant.交易订单状态_待接单.equals(tradeOrder.getState()) || Constant.交易订单状态_未付款.equals(tradeOrder.getState())
				|| Constant.交易订单状态_已付款.equals(tradeOrder.getState()))) {
			throw new BizException("订单状态异常");
		}
		cancel(tradeOrder.getId(), Constant.交易订单状态_已取消, Constant.操作方_买方);
	}

	@Transactional
	public void sellerRejectOrder(@Valid SellerRejectOrderParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!param.getSellerId().equals(tradeOrder.getSellerId())) {
			throw new BizException("操作异常");
		}
		if (!Constant.交易订单状态_待接单.equals(tradeOrder.getState())) {
			throw new BizException("订单状态异常");
		}
		cancel(tradeOrder.getId(), Constant.交易订单状态_接单已拒绝, Constant.操作方_卖方);
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

		if (preTradeOrder != null && Constant.订单交易类型_购买.equals(preTradeOrder.getTradeType())) {
			Member seller = tradeOrder.getSeller();
			double sellerBalance = NumberUtil.round(seller.getBalance() + tradeOrder.getAmount(), 2).doubleValue();
			seller.setBalance(sellerBalance);
			memberRepo.save(seller);

			memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithCancelTrade(seller, tradeOrder));
		}

		if (Constant.交易订单状态_已取消.equals(tradeOrder.getState())) {
			publishRiskMq(Constant.风控原因_买方取消交易, tradeOrder.getBuyerId());
		}
		if (Constant.交易订单状态_接单已拒绝.equals(tradeOrder.getState())) {
			publishRiskMq(Constant.风控原因_卖方拒绝接单, tradeOrder.getSellerId());
		}
	}

	@Transactional
	public void sellerConfirmOrder(@Valid SellerConfirmOrderParam param) {
		TradeOrder tradeOrder = tradeOrderRepo.getOne(param.getTradeOrderId());
		if (!param.getSellerId().equals(tradeOrder.getSellerId())) {
			throw new BizException("操作异常");
		}
		if (!Constant.交易订单状态_待接单.equals(tradeOrder.getState())) {
			throw new BizException("订单状态异常");
		}
		tradeOrder.setState(Constant.交易订单状态_未付款);
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
		if (!Constant.预交易订单状态_进行中.equals(preTradeOrder.getState())) {
			throw new BizException("订单无效");
		}
		if (!Constant.订单交易类型_出售.equals(preTradeOrder.getTradeType())) {
			throw new BizException("订单交易类型不正确");
		}
		double availableAmount = NumberUtil.round(preTradeOrder.getAvailableAmount() - param.getAmount(), 2)
				.doubleValue();
		if (availableAmount < 0) {
			throw new BizException("金额过大了");
		}
		if (preTradeOrder.getMemberId().equals(param.getBuyerId())) {
			throw new BizException("不能交易自己的单");
		}
		ReceiptPaymentInfo paymentInfo = receiptPaymentInfoRepo.getOne(param.getPaymentInfoId());
		if (!paymentInfo.getMemberId().equals(param.getBuyerId())) {
			throw new BizException("操作异常");
		}
		if (!preTradeOrder.getReceiptPaymentType().contains(paymentInfo.getType())) {
			throw new BizException("付款方式不正确");
		}
		String receiptPaymentType = paymentInfo.getType();
		ReceiptPaymentInfo receiptInfo = receiptPaymentInfoRepo
				.findTopByMemberIdAndTypeAndActivatedTrueAndDeletedFlagIsFalseOrderByActivatedTimeDesc(
						preTradeOrder.getMemberId(), receiptPaymentType);
		if (receiptInfo == null) {
			throw new BizException("卖方未激活收款方式");
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
			throw new BizException("余额不足");
		}
		PreTradeOrder preTradeOrder = preTradeOrderRepo.getOne(param.getPreTradeOrderId());
		if (!Constant.预交易订单状态_进行中.equals(preTradeOrder.getState())) {
			throw new BizException("订单无效");
		}
		if (!Constant.订单交易类型_购买.equals(preTradeOrder.getTradeType())) {
			throw new BizException("订单交易类型不正确");
		}
		double availableAmount = NumberUtil.round(preTradeOrder.getAvailableAmount() - param.getAmount(), 2)
				.doubleValue();
		if (availableAmount < 0) {
			throw new BizException("金额过大了");
		}
		if (preTradeOrder.getMemberId().equals(param.getSellerId())) {
			throw new BizException("不能交易自己的单");
		}
		ReceiptPaymentInfo receiptInfo = receiptPaymentInfoRepo.getOne(param.getReceiptInfoId());
		if (!receiptInfo.getMemberId().equals(param.getSellerId())) {
			throw new BizException("操作异常");
		}
		if (!preTradeOrder.getReceiptPaymentType().contains(receiptInfo.getType())) {
			throw new BizException("收款方式不正确");
		}
		String receiptPaymentType = receiptInfo.getType();
		ReceiptPaymentInfo paymentInfo = receiptPaymentInfoRepo
				.findTopByMemberIdAndTypeAndActivatedTrueAndDeletedFlagIsFalseOrderByActivatedTimeDesc(
						preTradeOrder.getMemberId(), receiptPaymentType);
		if (paymentInfo == null) {
			throw new BizException("买方未激活付款方式");
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
