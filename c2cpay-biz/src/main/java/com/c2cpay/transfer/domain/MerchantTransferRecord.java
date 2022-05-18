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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant_transfer_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantTransferRecord implements Serializable {

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

	private String receiptAddr;

	private String state;

	private Date createTime;

	@Version
	private Long version;

	@Column(name = "transfer_account_id", length = 32)
	private String transferAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transfer_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant transferAccount;

	public static MerchantTransferRecord build(String transferAccountId, Double amount, String receiptAddr,
			String merchantOrderNo) {
		MerchantTransferRecord po = new MerchantTransferRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setState(Constant.转账记录状态_已完成);
		po.setTransferAccountId(transferAccountId);
		po.setAmount(amount);
		po.setReceiptAddr(receiptAddr);
		po.setMerchantOrderNo(merchantOrderNo);
		return po;
	}

}
