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

import com.c2cpay.transfer.domain.statistic.MemberEverydayReceiptData;
import com.c2cpay.transfer.domain.statistic.MemberEverydayTransferData;
import com.c2cpay.transfer.param.statistic.MemberReceiptDataQueryCondParam;
import com.c2cpay.transfer.param.statistic.MemberTransferDataQueryCondParam;
import com.c2cpay.transfer.repo.statistic.MemberEverydayReceiptDataRepo;
import com.c2cpay.transfer.repo.statistic.MemberEverydayTransferDataRepo;
import com.c2cpay.transfer.vo.statistic.MemberEverydayReceiptDataVO;
import com.c2cpay.transfer.vo.statistic.MemberEverydayTransferDataVO;
import com.c2cpay.transfer.vo.statistic.MemberReceiptStatisticDataVO;
import com.c2cpay.transfer.vo.statistic.MemberTransferStatisticDataVO;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;

@Validated
@Service
public class MemberTransferStatisticService {

	@Autowired
	private MemberEverydayReceiptDataRepo memberEverydayReceiptDataRepo;

	@Autowired
	private MemberEverydayTransferDataRepo memberEverydayTransferDataRepo;

	public MemberReceiptStatisticDataVO getMemberReceiptStatisticData(MemberReceiptDataQueryCondParam param) {
		List<MemberEverydayReceiptData> result = memberEverydayReceiptDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("everyday")));
		MemberReceiptStatisticDataVO vo = new MemberReceiptStatisticDataVO();
		String today = DateUtil.today();
		String yesterday = DateUtil.format(DateUtil.yesterday(), DatePattern.NORM_DATE_PATTERN);
		for (MemberEverydayReceiptData data : result) {
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

	public MemberTransferStatisticDataVO getMemberTransferStatisticData(MemberTransferDataQueryCondParam param) {
		List<MemberEverydayTransferData> result = memberEverydayTransferDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("everyday")));
		MemberTransferStatisticDataVO vo = new MemberTransferStatisticDataVO();
		String today = DateUtil.today();
		String yesterday = DateUtil.format(DateUtil.yesterday(), DatePattern.NORM_DATE_PATTERN);
		for (MemberEverydayTransferData data : result) {
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
	public List<MemberEverydayReceiptDataVO> findMemberEverydayReceiptData(MemberReceiptDataQueryCondParam param) {
		List<MemberEverydayReceiptData> result = memberEverydayReceiptDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.asc("everyday")));
		return MemberEverydayReceiptDataVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public List<MemberEverydayReceiptDataVO> findMemberEverydayReceiptDataGroupBy(
			MemberReceiptDataQueryCondParam param) {
		List<MemberEverydayReceiptData> result = memberEverydayReceiptDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.asc("everyday")));
		Map<Date, List<MemberEverydayReceiptData>> groupByMap = new LinkedHashMap<>();
		for (MemberEverydayReceiptData data : result) {
			if (groupByMap.get(data.getEveryday()) == null) {
				groupByMap.put(data.getEveryday(), new ArrayList<>());
			}
			groupByMap.get(data.getEveryday()).add(data);
		}
		List<MemberEverydayReceiptDataVO> vos = new ArrayList<>();
		for (Entry<Date, List<MemberEverydayReceiptData>> entry : groupByMap.entrySet()) {
			MemberEverydayReceiptDataVO vo = new MemberEverydayReceiptDataVO();
			vo.setEveryday(entry.getKey());
			for (MemberEverydayReceiptData data : entry.getValue()) {
				vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + data.getSuccessAmount(), 2).doubleValue());
				vo.setSuccessCount(vo.getSuccessCount() + data.getSuccessCount());
			}
			vos.add(vo);
		}
		return vos;
	}

	@Transactional(readOnly = true)
	public List<MemberEverydayTransferDataVO> findMemberEverydayTransferData(MemberTransferDataQueryCondParam param) {
		List<MemberEverydayTransferData> result = memberEverydayTransferDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.asc("everyday")));
		return MemberEverydayTransferDataVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public List<MemberEverydayTransferDataVO> findMemberEverydayTransferDataGroupBy(
			MemberTransferDataQueryCondParam param) {
		List<MemberEverydayTransferData> result = memberEverydayTransferDataRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.asc("everyday")));
		Map<Date, List<MemberEverydayTransferData>> groupByMap = new LinkedHashMap<>();
		for (MemberEverydayTransferData data : result) {
			if (groupByMap.get(data.getEveryday()) == null) {
				groupByMap.put(data.getEveryday(), new ArrayList<>());
			}
			groupByMap.get(data.getEveryday()).add(data);
		}
		List<MemberEverydayTransferDataVO> vos = new ArrayList<>();
		for (Entry<Date, List<MemberEverydayTransferData>> entry : groupByMap.entrySet()) {
			MemberEverydayTransferDataVO vo = new MemberEverydayTransferDataVO();
			vo.setEveryday(entry.getKey());
			for (MemberEverydayTransferData data : entry.getValue()) {
				vo.setSuccessAmount(NumberUtil.round(vo.getSuccessAmount() + data.getSuccessAmount(), 2).doubleValue());
				vo.setSuccessCount(vo.getSuccessCount() + data.getSuccessCount());
			}
			vos.add(vo);
		}
		return vos;
	}

}
