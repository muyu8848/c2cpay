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
import com.c2cpay.member.domain.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member_receipt_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MemberReceiptRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Double amount;

	private String transferAddr;

	private String bizType;

	private String state;

	private Date createTime;

	private Boolean receiptFundSync;

	@Version
	private Long version;

	@Column(name = "receipt_account_id", length = 32)
	private String receiptAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receipt_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member receiptAccount;

	public static MemberReceiptRecord build(String receiptAccountId, Double amount, String transferAddr,
			String bizType) {
		MemberReceiptRecord po = new MemberReceiptRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setReceiptAccountId(receiptAccountId);
		po.setAmount(amount);
		po.setTransferAddr(transferAddr);
		po.setBizType(bizType);
		po.setReceiptFundSync(false);
		po.setState(Constant.收款记录状态_已完成);
		return po;
	}

}
