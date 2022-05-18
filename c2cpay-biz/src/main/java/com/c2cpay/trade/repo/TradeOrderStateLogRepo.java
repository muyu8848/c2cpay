package com.c2cpay.trade.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.trade.domain.TradeOrderStateLog;

public interface TradeOrderStateLogRepo
		extends JpaRepository<TradeOrderStateLog, String>, JpaSpecificationExecutor<TradeOrderStateLog> {

	@Modifying
	@Query(nativeQuery = true, value = "delete from trade_order_state_log where trade_order_id in (select id from trade_order where create_time >= ?1 and create_time <= ?2)")
	Integer dataClean(Date startTime, Date endTime);

	List<TradeOrderStateLog> findByTradeOrderIdOrderByLogTimeAsc(String tradeOrderId);

}
