package com.c2cpay.transfer.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.transfer.domain.MerchantReceiptRecord;

public interface MerchantReceiptRecordRepo
		extends JpaRepository<MerchantReceiptRecord, String>, JpaSpecificationExecutor<MerchantReceiptRecord> {

	@Modifying
	@Query(nativeQuery = true, value = "delete from merchant_receipt_record where create_time >= ?1 and create_time <= ?2")
	Integer dataClean(Date startTime, Date endTime);

	MerchantReceiptRecord findTopByOrderNo(String orderNo);

	List<MerchantReceiptRecord> findByStateAndReceiptFundSyncFalse(String state);

	List<MerchantReceiptRecord> findByStateAndLockDeadlineLessThan(String state, Date lockDeadline);

	List<MerchantReceiptRecord> findByStateAndTransferDeadlineLessThan(String state, Date transferDeadline);

	@Modifying
	@Query("update MerchantReceiptRecord m set m.noticeState=?2 where  m.id=?1")
	void updateNoticeState(String id, String state);

}
