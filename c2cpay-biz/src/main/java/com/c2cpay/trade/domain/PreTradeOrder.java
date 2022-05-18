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

import com.c2cpay.member.domain.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pre_trade_order")
@DynamicInsert(true)
@DynamicUpdate(true)
public class PreTradeOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;
	
	private String orderNo;

	private String tradeType;
	
	private String receiptPaymentType;
	
	private Date createTime;
	
	private Date completedTime;
	
	private Date cancelTime;
	
	private String state;
	
	private Double amount;
	
	private Double minAmount;
	
	private Double maxAmount;
	
	private Double availableAmount;
	
	@Version
	private Long version;

	@Column(name = "member_id", length = 32)
	private String memberId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

}
