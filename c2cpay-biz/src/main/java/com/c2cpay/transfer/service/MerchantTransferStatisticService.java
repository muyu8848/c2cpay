package com.c2cpay.transfer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.c2cpay.transfer.domain.statistic.MerchantEverydayReceiptData;
import com.c2cpay.transfer.domain.statistic.MerchantEverydayTransferData;
import com.c2cpay.transfer.param.statistic.MerchantReceiptDataQueryCondParam;
import com.c2cpay.transfer.param.statistic.MerchantTransferDataQueryCondParam;
import com.c2cpay.transfer.repo.statistic.MerchantEverydayReceiptDataRepo;
import com.c2cpay.transfer.repo.statistic.MerchantEverydayTransferDataRepo;
import com.c2cpay.transfer.vo.statistic.MerchantEverydayReceiptDataVO;
import com.c2cpay.transfer.vo.statistic.MerchantEverydayTransferDataVO;
import com.c2cpay.transfer.vo.statistic.MerchantReceiptStatisticDataVO;
import com.c2cpay.transfer.vo.statistic.MerchantTransferStatisticDataVO;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;

@Validated
@Service
public class MerchantTransferStatisticService {

	@Autowired
	private MerchantEverydayReceiptDataRepo merchantEverydayReceiptDataRepo;

	@Autowired
	private MerchantEverydayTransferDataRepo merchantEverydayTransferDataRepo;

	public MerchantReceiptStatisticDataVO getMerchantReceiptStatisticData(MerchantReceiptDataQueryCondParam param) {
		List<MerchantEverydayReceiptData> result = merchantEverydayReceiptDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("everyday")));
		MerchantReceiptStatisticDataVO vo = new MerchantReceiptStatisticDataVO();
		String today = DateUtil.today();
		String yesterday = DateUtil.format(DateUtil.yesterday(), DatePattern.NORM_DATE_PATTERN);
		for (MerchantEverydayReceiptData data : result) {
			String everyday = DateUtil.format(data.getEveryday(), DatePattern.NORM_DATE_PATTERN);
			if (everyday.equals(today)) {
				vo.setTodayAmount(NumberUtil.round(vo.getTodayAmount() + data.getSuccessAmount(), 2).doubleValue());
				vo.setTodayCount(vo.getTodayCount() + data.getSuccessCount());
			}
			if (everyday.equals(yesterday)) {
				vo.setYesterdayAmount(
						NumberUtil.round(vo.getYesterdayAmount() + data.getSuccessAmount(), 2).doubleValue());
			}
			vo.setTotalAmount(NumberUtil.round(vo.getTotalAmount() + data.getSuccessAmount(), 2).doubleValue());
		}
		return vo;
	}

	public MerchantTransferStatisticDataVO getMerchantTransferStatisticData(MerchantTransferDataQueryCondParam param) {
		List<MerchantEverydayTransferData> result = merchantEverydayTransferDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("everyday")));
		MerchantTransferStatisticDataVO vo = new MerchantTransferStatisticDataVO();
		String today = DateUtil.today();
		String yesterday = DateUtil.format(DateUtil.yesterday(), DatePattern.NORM_DATE_PATTERN);
		for (MerchantEverydayTransferData data : result) {
			String everyday = DateUtil.format(data.getEveryday(), DatePattern.NORM_DATE_PATTERN);
			if (everyday.equals(today)) {
				vo.setTodayAmount(NumberUtil.round(vo.getTodayAmount() + data.getSuccessAmount(), 2).doubleValue());
				vo.setTodayCount(vo.getTodayCount() + data.getSuccessCount());
			}
			if (everyday.equals(yesterday)) {
				vo.setYesterdayAmount(
						NumberUtil.round(vo.getYesterdayAmount() + data.getSuccessAmount(), 2).doubleValue());
			}
			vo.setTotalAmount(NumberUtil.round(vo.getTotalAmount() + data.getSuccessAmount(), 2).doubleValue());
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public List<MerchantEverydayReceiptDataVO> findMerchantEverydayReceiptData(MerchantReceiptDataQueryCondParam param) {
		List<MerchantEverydayReceiptData> result = merchantEverydayReceiptDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.asc("everyday")));
		return MerchantEverydayReceiptDataVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public List<MerchantEverydayReceiptDataVO> findMerchantEverydayReceiptDataGroupBy(MerchantReceiptDataQueryCondParam param) {
		List<MerchantEverydayReceiptData> result = merchantEverydayReceiptDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.asc("everyday")));
		Map<Date, List<MerchantEverydayReceiptData>> groupByMap = new LinkedHashMap<>();
		for (MerchantEverydayReceiptData data : result) {
			if (groupByMap.get(data.getEveryday()) == null) {
				groupByMap.put(data.getEveryday(), new ArrayList<>());
			}
			groupByMap.get(data.getEveryday()).add(data);
		}
		List<MerchantEverydayReceiptDataVO> vos = new ArrayList<>();
		for (Entry<Date, List<MerchantEverydayReceiptData>> entry : groupByMap.entrySet()) {
			MerchantEverydayReceiptDataVO vo = new MerchantEverydayReceiptDataVO();
			vo.setEveryday(entry.getKey());
			for (MerchantEverydayReceiptData data : entry.getValue()) {
				vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + data.getSuccessAmount(), 2).doubleValue());
				vo.setSuccessCount(vo.getSuccessCount() + data.getSuccessCount());
			}
			vos.add(vo);
		}
		return vos;
	}

	@Transactional(readOnly = true)
	public List<MerchantEverydayTransferDataVO> findMerchantEverydayTransferData(MerchantTransferDataQueryCondParam param) {
		List<MerchantEverydayTransferData> result = merchantEverydayTransferDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.asc("everyday")));
		return MerchantEverydayTransferDataVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public List<MerchantEverydayTransferDataVO> findMerchantEverydayTransferDataGroupBy(
			MerchantTransferDataQueryCondParam param) {
		List<MerchantEverydayTransferData> result = merchantEverydayTransferDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.asc("everyday")));
		Map<Date, List<MerchantEverydayTransferData>> groupByMap = new LinkedHashMap<>();
		for (MerchantEverydayTransferData data : result) {
			if (groupByMap.get(data.getEveryday()) == null) {
				groupByMap.put(data.getEveryday(), new ArrayList<>());
			}
			groupByMap.get(data.getEveryday()).add(data);
		}
		List<MerchantEverydayTransferDataVO> vos = new ArrayList<>();
		for (Entry<Date, List<MerchantEverydayTransferData>> entry : groupByMap.entrySet()) {
			MerchantEverydayTransferDataVO vo = new MerchantEverydayTransferDataVO();
			vo.setEveryday(entry.getKey());
			for (MerchantEverydayTransferData data : entry.getValue()) {
				vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + data.getSuccessAmount(), 2).doubleValue());
				vo.setSuccessCount(vo.getSuccessCount() + data.getSuccessCount());
			}
			vos.add(vo);
		}
		return vos;
	}

}
