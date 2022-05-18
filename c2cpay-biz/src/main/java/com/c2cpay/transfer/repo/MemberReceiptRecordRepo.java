package com.c2cpay.transfer.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.transfer.domain.MemberReceiptRecord;

public interface MemberReceiptRecordRepo
		extends JpaRepository<MemberReceiptRecord, String>, JpaSpecificationExecutor<MemberReceiptRecord> {
	
	@Modifying
	@Query(nativeQuery = true, value = "delete from member_receipt_record where create_time >= ?1 and create_time <= ?2")
	Integer dataClean(Date startTime, Date endTime);

	List<MemberReceiptRecord> findByStateAndReceiptFundSyncFalse(String state);

}
