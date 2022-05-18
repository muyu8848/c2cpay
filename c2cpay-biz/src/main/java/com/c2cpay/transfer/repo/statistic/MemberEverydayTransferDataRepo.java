package com.c2cpay.transfer.repo.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.transfer.domain.statistic.MemberEverydayTransferData;

public interface MemberEverydayTransferDataRepo extends JpaRepository<MemberEverydayTransferData, String>,
		JpaSpecificationExecutor<MemberEverydayTransferData> {

}
