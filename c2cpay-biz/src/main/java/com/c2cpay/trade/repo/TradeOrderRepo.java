package com.c2cpay.trade.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.trade.domain.TradeOrder;

public interface TradeOrderRepo extends JpaRepository<TradeOrder, String>, JpaSpecificationExecutor<TradeOrder> {
	
	@Modifying
	@Query(nativeQuery = true, value = "delete from trade_order where create_time >= ?1 and create_time <= ?2")
	Integer dataClean(Date startTime, Date endTime);

	List<TradeOrder> findByPreTradeOrderId(String preTradeOrderId);
	
	List<TradeOrder> findByStateAndOrderDeadlineLessThan(String state, Date orderDeadline);
	
	List<TradeOrder> findByStateAndTradeDeadlineLessThan(String state, Date tradeDeadline);

}
