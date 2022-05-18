package com.c2cpay.log.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.constants.Constant;
import com.c2cpay.member.domain.Member;
import com.c2cpay.trade.domain.PreTradeOrder;
import com.c2cpay.trade.domain.TradeOrder;
import com.c2cpay.transfer.domain.MemberReceiptRecord;
import com.c2cpay.transfer.domain.MemberTransferRecord;

import cn.hutool.core.util.NumberUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member_balance_change_log")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MemberBalanceChangeLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String bizOrderNo;

	private Date changeTime;

	private String changeType;

	private Double balanceChange;

	private Double balanceBefore;

	private Double balanceAfter;

	private String note;

	@Version
	private Long version;

	@Column(name = "member_id", length = 32)
	private String memberId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

	public static MemberBalanceChangeLog buildWithSystem(Member member, Double changeAmount) {
		MemberBalanceChangeLog log = new MemberBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.会员余额变动日志类型_系统);
		log.setBalanceChange(changeAmount);
		log.setBalanceBefore(NumberUtil.round(member.getBalance() - changeAmount, 2).doubleValue());
		log.setBalanceAfter(member.getBalance());
		log.setMemberId(member.getId());
		return log;
	}

	public static MemberBalanceChangeLog buildWithReceipt(Member member, MemberReceiptRecord receiptRecord) {
		MemberBalanceChangeLog log = new MemberBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(receiptRecord.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.会员余额变动日志类型_收款);
		log.setBalanceChange(receiptRecord.getAmount());
		log.setBalanceBefore(NumberUtil.round(member.getBalance() - receiptRecord.getAmount(), 2).doubleValue());
		log.setBalanceAfter(member.getBalance());
		log.setMemberId(member.getId());
		return log;
	}

	public static MemberBalanceChangeLog buildWithTransfer(Member member, MemberTransferRecord transferRecord) {
		MemberBalanceChangeLog log = new MemberBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(transferRecord.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.会员余额变动日志类型_转账);
		log.setBalanceChange(-transferRecord.getAmount());
		log.setBalanceBefore(NumberUtil.round(member.getBalance() + transferRecord.getAmount(), 2).doubleValue());
		log.setBalanceAfter(member.getBalance());
		log.setMemberId(member.getId());
		return log;
	}

	public static MemberBalanceChangeLog buildWithCancelPreTradeOrderSell(Member member, PreTradeOrder preTradeOrder) {
		MemberBalanceChangeLog log = new MemberBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(preTradeOrder.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.会员余额变动日志类型_挂单撤销);
		log.setBalanceChange(preTradeOrder.getAvailableAmount());
		log.setBalanceBefore(
				NumberUtil.round(member.getBalance() - preTradeOrder.getAvailableAmount(), 2).doubleValue());
		log.setBalanceAfter(member.getBalance());
		log.setMemberId(member.getId());
		return log;
	}

	public static MemberBalanceChangeLog buildWithCancelTrade(Member member, TradeOrder tradeOrder) {
		MemberBalanceChangeLog log = new MemberBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(tradeOrder.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.会员余额变动日志类型_取消交易);
		log.setBalanceChange(tradeOrder.getAmount());
		log.setBalanceBefore(NumberUtil.round(member.getBalance() - tradeOrder.getAmount(), 2).doubleValue());
		log.setBalanceAfter(member.getBalance());
		log.setMemberId(member.getId());
		return log;
	}

	public static MemberBalanceChangeLog buildWithPreTradeOrderSell(Member member, PreTradeOrder preTradeOrder) {
		MemberBalanceChangeLog log = new MemberBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(preTradeOrder.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.会员余额变动日志类型_挂单卖出);
		log.setBalanceChange(-preTradeOrder.getAmount());
		log.setBalanceBefore(NumberUtil.round(member.getBalance() + preTradeOrder.getAmount(), 2).doubleValue());
		log.setBalanceAfter(member.getBalance());
		log.setMemberId(member.getId());
		return log;
	}

	public static MemberBalanceChangeLog buildWithSell(Member member, TradeOrder tradeOrder) {
		MemberBalanceChangeLog log = new MemberBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(tradeOrder.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.会员余额变动日志类型_卖出);
		log.setBalanceChange(-tradeOrder.getAmount());
		log.setBalanceBefore(NumberUtil.round(member.getBalance() + tradeOrder.getAmount(), 2).doubleValue());
		log.setBalanceAfter(member.getBalance());
		log.setMemberId(member.getId());
		return log;
	}

	public static MemberBalanceChangeLog buildWithBuy(Member member, TradeOrder tradeOrder) {
		MemberBalanceChangeLog log = new MemberBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(tradeOrder.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.会员余额变动日志类型_买入);
		log.setBalanceChange(tradeOrder.getAmount());
		log.setBalanceBefore(NumberUtil.round(member.getBalance() - tradeOrder.getAmount(), 2).doubleValue());
		log.setBalanceAfter(member.getBalance());
		log.setMemberId(member.getId());
		return log;
	}

}
