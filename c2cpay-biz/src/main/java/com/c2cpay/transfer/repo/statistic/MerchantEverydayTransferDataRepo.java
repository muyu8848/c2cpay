package com.c2cpay.transfer.repo.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.transfer.domain.statistic.MerchantEverydayTransferData;

public interface MerchantEverydayTransferDataRepo extends JpaRepository<MerchantEverydayTransferData, String>,
		JpaSpecificationExecutor<MerchantEverydayTransferData> {

}
