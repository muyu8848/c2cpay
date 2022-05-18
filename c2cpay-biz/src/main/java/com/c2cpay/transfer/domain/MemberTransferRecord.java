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
@Table(name = "member_transfer_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MemberTransferRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Double amount;

	private String receiptAddr;

	private String bizType;

	private String state;

	private Date createTime;

	@Version
	private Long version;

	@Column(name = "transfer_account_id", length = 32)
	private String transferAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transfer_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member transferAccount;

	public static MemberTransferRecord build(String transferAccountId, Double amount, String receiptAddr,
			String bizType) {
		MemberTransferRecord po = new MemberTransferRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setTransferAccountId(transferAccountId);
		po.setAmount(amount);
		po.setReceiptAddr(receiptAddr);
		po.setBizType(bizType);
		po.setState(Constant.转账记录状态_已完成);
		return po;
	}

}
