package com.c2cpay.receiptpaymentinfo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.receiptpaymentinfo.domain.ReceiptPaymentInfo;

public interface ReceiptPaymentInfoRepo
		extends JpaRepository<ReceiptPaymentInfo, String>, JpaSpecificationExecutor<ReceiptPaymentInfo> {

	List<ReceiptPaymentInfo> findByMemberIdAndDeletedFlagFalseOrderByActivatedTimeDesc(String memberId);

	List<ReceiptPaymentInfo> findByMemberIdAndActivatedTrueAndDeletedFlagFalseOrderByActivatedTimeDesc(
			String memberId);

	ReceiptPaymentInfo findTopByMemberIdAndTypeAndActivatedTrueAndDeletedFlagIsFalseOrderByActivatedTimeDesc(
			String memberId, String type);

}
