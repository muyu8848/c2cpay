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
import com.c2cpay.merchant.domain.Merchant;
import com.c2cpay.transfer.domain.MerchantReceiptRecord;
import com.c2cpay.transfer.domain.MerchantTransferRecord;

import cn.hutool.core.util.NumberUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant_balance_change_log")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantBalanceChangeLog implements Serializable {

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

	@Column(name = "merchant_id", length = 32)
	private String merchantId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant merchant;

	public static MerchantBalanceChangeLog buildWithReceipt(Merchant merchant, MerchantReceiptRecord receiptRecord) {
		MerchantBalanceChangeLog log = new MerchantBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(receiptRecord.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.商户余额变动日志类型_收款);
		log.setBalanceChange(receiptRecord.getAmount());
		log.setBalanceBefore(NumberUtil.round(merchant.getBalance() - receiptRecord.getAmount(), 2).doubleValue());
		log.setBalanceAfter(merchant.getBalance());
		log.setMerchantId(merchant.getId());
		return log;
	}

	public static MerchantBalanceChangeLog buildWithTransfer(Merchant merchant, MerchantTransferRecord transferRecord) {
		MerchantBalanceChangeLog log = new MerchantBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setBizOrderNo(transferRecord.getOrderNo());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.商户余额变动日志类型_转账);
		log.setBalanceChange(-transferRecord.getAmount());
		log.setBalanceBefore(NumberUtil.round(merchant.getBalance() + transferRecord.getAmount(), 2).doubleValue());
		log.setBalanceAfter(merchant.getBalance());
		log.setMerchantId(merchant.getId());
		return log;
	}

	public static MerchantBalanceChangeLog buildWithSystem(Merchant merchant, Double changeAmount) {
		MerchantBalanceChangeLog log = new MerchantBalanceChangeLog();
		log.setId(IdUtils.getId());
		log.setChangeTime(new Date());
		log.setChangeType(Constant.商户余额变动日志类型_系统);
		log.setBalanceChange(changeAmount);
		log.setBalanceBefore(NumberUtil.round(merchant.getBalance() - changeAmount, 2).doubleValue());
		log.setBalanceAfter(merchant.getBalance());
		log.setMerchantId(merchant.getId());
		return log;
	}
}
