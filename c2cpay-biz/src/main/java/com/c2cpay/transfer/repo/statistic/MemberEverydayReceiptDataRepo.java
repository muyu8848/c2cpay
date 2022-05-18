package com.c2cpay.transfer.repo.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.transfer.domain.statistic.MemberEverydayReceiptData;

public interface MemberEverydayReceiptDataRepo
		extends JpaRepository<MemberEverydayReceiptData, String>, JpaSpecificationExecutor<MemberEverydayReceiptData> {

}
