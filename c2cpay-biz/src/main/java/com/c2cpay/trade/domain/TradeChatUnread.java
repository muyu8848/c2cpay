package com.c2cpay.trade.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.c2cpay.common.utils.IdUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "trade_chat_unread")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TradeChatUnread implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Boolean unreadFlag;

	private Date readTime;

	private String tradeChatRecordId;

	private String receiverId;

	private String tradeOrderId;

	public static TradeChatUnread build(String tradeChatRecordId, String receiverId, String tradeOrderId) {
		TradeChatUnread po = new TradeChatUnread();
		po.setId(IdUtils.getId());
		po.setUnreadFlag(true);
		po.setTradeChatRecordId(tradeChatRecordId);
		po.setReceiverId(receiverId);
		po.setTradeOrderId(tradeOrderId);
		return po;
	}

}
