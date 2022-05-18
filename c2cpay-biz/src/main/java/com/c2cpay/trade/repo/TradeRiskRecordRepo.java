package com.c2cpay.trade.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.trade.domain.TradeRiskRecord;

public interface TradeRiskRecordRepo
		extends JpaRepository<TradeRiskRecord, String>, JpaSpecificationExecutor<TradeRiskRecord> {

	@Modifying
	@Query(nativeQuery = true, value = "delete from trade_risk_record where create_time >= ?1 and create_time <= ?2")
	Integer dataClean(Date startTime, Date endTime);

}
