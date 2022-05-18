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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.member.domain.Member;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "trade_risk_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TradeRiskRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Date createTime;

	private String riskCause;

	private Long hitCount;

	private String riskPunish;

	private Date riskFinishTime;

	@Column(name = "member_id", length = 32)
	private String memberId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

	public long getRiskSecond() {
		return DateUtil.between(this.getCreateTime(), this.getRiskFinishTime(), DateUnit.SECOND);
	}

	public static TradeRiskRecord buildRiskToday(String memberId, String riskCause, Long hitCount, String riskPunish) {
		TradeRiskRecord po = TradeRiskRecord.build(memberId, riskCause, hitCount, riskPunish);
		po.setRiskFinishTime(DateUtil.endOfDay(po.getCreateTime()).toJdkDate());
		return po;
	}

	public static TradeRiskRecord buildRisk48Hour(String memberId, String riskCause, Long hitCount, String riskPunish) {
		TradeRiskRecord po = TradeRiskRecord.build(memberId, riskCause, hitCount, riskPunish);
		po.setRiskFinishTime(DateUtil.offset(po.getCreateTime(), DateField.HOUR, 48).toJdkDate());
		return po;
	}

	public static TradeRiskRecord build(String memberId, String riskCause, Long hitCount, String riskPunish) {
		TradeRiskRecord po = new TradeRiskRecord();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setMemberId(memberId);
		po.setRiskCause(riskCause);
		po.setHitCount(hitCount);
		po.setRiskPunish(riskPunish);
		return po;
	}

}
