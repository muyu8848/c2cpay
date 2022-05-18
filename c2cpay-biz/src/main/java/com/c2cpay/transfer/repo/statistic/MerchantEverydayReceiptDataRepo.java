package com.c2cpay.transfer.repo.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.transfer.domain.statistic.MerchantEverydayReceiptData;

public interface MerchantEverydayReceiptDataRepo extends JpaRepository<MerchantEverydayReceiptData, String>,
		JpaSpecificationExecutor<MerchantEverydayReceiptData> {

}
