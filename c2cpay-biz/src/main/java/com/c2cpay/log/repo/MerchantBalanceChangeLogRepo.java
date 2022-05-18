package com.c2cpay.log.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.log.domain.MerchantBalanceChangeLog;

public interface MerchantBalanceChangeLogRepo
		extends JpaRepository<MerchantBalanceChangeLog, String>, JpaSpecificationExecutor<MerchantBalanceChangeLog> {

	@Modifying
	@Query(nativeQuery = true, value = "delete from merchant_balance_change_log where change_time >= ?1 and change_time <= ?2")
	Integer dataClean(Date startTime, Date endTime);

}
