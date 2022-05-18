package com.c2cpay.trade.repo.statistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.trade.domain.statistic.EverydayTradeData;

public interface EverydayTradeDataRepo
		extends JpaRepository<EverydayTradeData, String>, JpaSpecificationExecutor<EverydayTradeData> {

}
