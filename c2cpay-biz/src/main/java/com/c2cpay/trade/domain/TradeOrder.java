package com.c2cpay.trade.domain;

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
import com.c2cpay.receiptpaymentinfo.domain.ReceiptPaymentInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "trade_order")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TradeOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Date createTime;

	private Date finishTime;

	private String finishOperator;

	private String state;

	private String receiptPaymentType;

	private Double amount;

	private Date orderDeadline;

	private Date tradeDeadline;

	private String paymentCertificate;

	@Version
	private Long version;

	@Column(name = "buyer_id", length = 32)
	private String buyerId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buyer_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member buyer;

	@Column(name = "payment_info_id", length = 32)
	private String paymentInfoId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_info_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private ReceiptPaymentInfo paymentInfo;

	@Column(name = "seller_id", length = 32)
	private String sellerId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member seller;

	@Column(name = "receipt_info_id", length = 32)
	private String receiptInfoId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receipt_info_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private ReceiptPaymentInfo receiptInfo;

	@Column(name = "pre_trade_order_id", length = 32)
	private String preTradeOrderId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pre_trade_order_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private PreTradeOrder preTradeOrder;

	@Column(name = "appeal_record_id", length = 32)
	private String appealRecordId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "appeal_record_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private TradeAppealRecord appealRecord;

	public TradeOrderStateLog buildStateLog() {
		TradeOrderStateLog po = new TradeOrderStateLog();
		po.setId(IdUtils.getId());
		po.setTradeOrderId(this.getId());
		po.setLogTime(new Date());
		po.setState(this.getState());
		return po;
	}

	public TradeChatRecord buildStateChangeChatRecord() {
		String content = "";
		if (Constant.交易订单状态_待接单.equals(this.getState())) {
			content = Constant.订单状态变动消息_待接单;
		} else if (Constant.交易订单状态_接单已取消.equals(this.getState())) {
			content = Constant.订单状态变动消息_接单已取消;
		} else if (Constant.交易订单状态_接单已拒绝.equals(this.getState())) {
			content = Constant.订单状态变动消息_接单已拒绝;
		} else if (Constant.交易订单状态_未付款.equals(this.getState())) {
			content = Constant.订单状态变动消息_未付款;
		} else if (Constant.交易订单状态_已付款.equals(this.getState())) {
			content = Constant.订单状态变动消息_已付款;
		} else if (Constant.交易订单状态_已完成.equals(this.getState())) {
			content = Constant.订单状态变动消息_已完成;
		} else if (Constant.交易订单状态_已取消.equals(this.getState())) {
			content = Constant.订单状态变动消息_已取消;
		} else if (Constant.交易订单状态_申诉中.equals(this.getState())) {
			content = Constant.订单状态变动消息_申诉中;
		}
		TradeChatRecord po = new TradeChatRecord();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setDeletedFlag(false);
		po.setSenderId(Constant.操作方_系统);
		po.setMsgType(Constant.聊天消息类型_文字);
		po.setTradeOrderId(this.getId());
		po.setContent(content);
		return po;
	}

}
