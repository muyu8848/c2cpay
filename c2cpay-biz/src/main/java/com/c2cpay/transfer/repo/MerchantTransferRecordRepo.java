package com.c2cpay.transfer.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.transfer.domain.MerchantTransferRecord;

public interface MerchantTransferRecordRepo
		extends JpaRepository<MerchantTransferRecord, String>, JpaSpecificationExecutor<MerchantTransferRecord> {

	@Modifying
	@Query(nativeQuery = true, value = "delete from merchant_transfer_record where create_time >= ?1 and create_time <= ?2")
	Integer dataClean(Date startTime, Date endTime);

}
