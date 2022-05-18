package com.c2cpay.member.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.constants.Constant;
import com.c2cpay.dictconfig.DictHolder;
import com.c2cpay.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class MemberVO {

	private String id;

	private String realName;

	private String mobile;

	private String nickName;

	private String walletAddr;

	private String state;

	private String stateName;

	private String buyState;

	private String buyStateName;

	private Boolean buyRiskLimit;

	private String sellState;

	private String sellStateName;

	private Boolean sellRiskLimit;

	private Double balance;

	private Double freezeFund;

	private Integer keepLoginDuration;

	private Boolean notSetPayPwd;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	public static List<MemberVO> convertFor(List<Member> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MemberVO> vos = new ArrayList<>();
		for (Member po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MemberVO convertFor(Member po) {
		if (po == null) {
			return null;
		}
		MemberVO vo = new MemberVO();
		BeanUtils.copyProperties(po, vo);
		vo.setNotSetPayPwd(StrUtil.isBlank(po.getPayPwd()));
		vo.setBuyRiskLimit(po.checkRiskLimit(Constant.风控处罚_限制买入));
		vo.setSellRiskLimit(po.checkRiskLimit(Constant.风控处罚_限制卖出));
		vo.setStateName(DictHolder.getDictItemName("functionState", vo.getState()));
		vo.setBuyStateName(DictHolder.getDictItemName("functionState", vo.getBuyState()));
		vo.setSellStateName(DictHolder.getDictItemName("functionState", vo.getSellState()));
		return vo;
	}

}
