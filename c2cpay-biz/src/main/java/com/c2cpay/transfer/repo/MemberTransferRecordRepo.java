package com.c2cpay.transfer.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.c2cpay.transfer.domain.MemberTransferRecord;

public interface MemberTransferRecordRepo
		extends JpaRepository<MemberTransferRecord, String>, JpaSpecificationExecutor<MemberTransferRecord> {

	@Modifying
	@Query(nativeQuery = true, value = "delete from member_transfer_record where create_time >= ?1 and create_time <= ?2")
	Integer dataClean(Date startTime, Date endTime);

}
