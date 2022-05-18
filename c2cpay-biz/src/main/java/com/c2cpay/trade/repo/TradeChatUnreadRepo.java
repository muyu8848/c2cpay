package com.c2cpay.trade.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.trade.domain.TradeChatUnread;

public interface TradeChatUnreadRepo
		extends JpaRepository<TradeChatUnread, String>, JpaSpecificationExecutor<TradeChatUnread> {
	
	@Modifying
	@Query(nativeQuery = true, value = "delete from trade_chat_unread where trade_order_id in (select id from trade_order where create_time >= ?1 and create_time <= ?2)")
	Integer dataClean(Date startTime, Date endTime);

	List<TradeChatUnread> findByTradeOrderIdAndReceiverIdAndUnreadFlagIsTrue(String tradeOrderId, String receiverId);

	Long countByTradeOrderIdAndReceiverIdAndUnreadFlagIsTrue(String tradeOrderId, String receiverId);
	
	List<TradeChatUnread> findByTradeChatRecordIdAndUnreadFlagIsTrue(String tradeChatRecordId);

}
