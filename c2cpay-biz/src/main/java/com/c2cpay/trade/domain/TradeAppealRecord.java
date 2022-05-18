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
@Table(name = "trade_appeal_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TradeAppealRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;
	
	private String orderNo;
	
	private String state;
	
	private String appealType;
	
	private String appealDesc;
	
	private Boolean approveAppeal;
	
	private Date createTime;
	
	private Date processTime;
	
	private String processWay;
	
	@Version
	private Long version;
	
	@Column(name = "initiator_id", length = 32)
	private String initiatorId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "initiator_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member initiator;
	
	@Column(name = "defendant_id", length = 32)
	private String defendantId;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "defendant_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member defendant;
	
	@Column(name = "trade_order_id", length = 32)
	private String tradeOrderId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trade_order_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private TradeOrder tradeOrder;
	

}
