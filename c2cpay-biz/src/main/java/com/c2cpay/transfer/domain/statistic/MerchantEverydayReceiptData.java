package com.c2cpay.transfer.domain.statistic;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.c2cpay.merchant.domain.Merchant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "v_merchant_everyday_receipt_data")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantEverydayReceiptData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column(name = "account_id", length = 32)
	private String accountId;

	private Date everyday;

	private Double successAmount;

	private Integer successCount;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant account;

}
