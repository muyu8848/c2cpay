package com.c2cpay.transfer.domain;

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
import com.c2cpay.merchant.domain.Merchant;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant_receipt_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantReceiptRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private String merchantOrderNo;

	private Double amount;

	private String transferAddr;

	private String state;

	private Date createTime;

	private Date lockTime;

	private Date lockDeadline;

	private Date transferDeadline;

	private Date endTime;

	private String notifyUrl;

	private String noticeState;

	private Boolean receiptFundSync;

	@Version
	private Long version;

	@Column(name = "receipt_account_id", length = 32)
	private String receiptAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receipt_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant receiptAccount;

	public String formatPayUrl() {
		return "/pay/pay?orderNo=" + this.getOrderNo();
	}

	public void lockTimeOut() {
		this.setState(Constant.收款记录状态_超时未拉起);
		this.setEndTime(new Date());
	}

	public void transferTimeOut() {
		this.setState(Constant.收款记录状态_超时未付款);
		this.setEndTime(new Date());
	}

	public void receiptCompleted() {
		this.setState(Constant.收款记录状态_已完成);
		this.setEndTime(new Date());
		this.setReceiptFundSync(false);
	}

	public void lockOrder(String transferAddr) {
		this.setLockTime(new Date());
		this.setState(Constant.收款记录状态_未付款);
		this.setTransferAddr(transferAddr);
		this.setTransferDeadline(DateUtil.offset(this.getLockTime(), DateField.MINUTE, 5));
	}

	public static MerchantReceiptRecord build(String receiptAccountId, Double amount, String merchantOrderNo,
			String notifyUrl) {
		MerchantReceiptRecord po = new MerchantReceiptRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setLockDeadline(DateUtil.offset(po.getCreateTime(), DateField.MINUTE, 10));
		po.setState(Constant.收款记录状态_未拉起);
		po.setReceiptAccountId(receiptAccountId);
		po.setAmount(amount);
		po.setMerchantOrderNo(merchantOrderNo);
		po.setNotifyUrl(notifyUrl);
		po.setNoticeState(StrUtil.isBlank(po.getNotifyUrl()) ? Constant.通知状态_无需通知 : Constant.通知状态_未通知);
		return po;
	}

}
