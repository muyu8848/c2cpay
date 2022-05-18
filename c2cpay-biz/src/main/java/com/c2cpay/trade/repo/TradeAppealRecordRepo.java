package com.c2cpay.trade.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.trade.domain.TradeAppealRecord;

public interface TradeAppealRecordRepo
		extends JpaRepository<TradeAppealRecord, String>, JpaSpecificationExecutor<TradeAppealRecord> {
	
	@Modifying
	@Query(nativeQuery = true, value = "delete from trade_appeal_record where trade_order_id in (select id from trade_order where create_time >= ?1 and create_time <= ?2)")
	Integer dataClean(Date startTime, Date endTime);

	TradeAppealRecord findTopByTradeOrderIdOrderByProcessTimeDesc(String tradeOrderId);

}
