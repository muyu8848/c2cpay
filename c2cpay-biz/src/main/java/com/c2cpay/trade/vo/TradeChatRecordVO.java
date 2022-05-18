package com.c2cpay.trade.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.c2cpay.constants.Constant;
import com.c2cpay.trade.domain.TradeChatRecord;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

@Data
public class TradeChatRecordVO {

	private String id;

	private String msgType;

	private String content;

	private Long timestamp;

	private String formatTime;

	private String senderId;

	private String senderNickName;

	private String receiverId;

	private String receiverNickName;

	public static List<TradeChatRecordVO> convertFor(List<TradeChatRecord> pos, Date currentDate) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<TradeChatRecordVO> vos = new ArrayList<>();
		for (TradeChatRecord po : pos) {
			vos.add(convertFor(po, currentDate));
		}
		return vos;
	}

	public static TradeChatRecordVO convertFor(TradeChatRecord po, Date currentDate) {
		if (po == null) {
			return null;
		}
		TradeChatRecordVO vo = new TradeChatRecordVO();
		BeanUtils.copyProperties(po, vo);

		vo.setTimestamp(po.getCreateTime().getTime());
		vo.setFormatTime(timePrettify(currentDate, po.getCreateTime()));
		if (!Constant.操作方_客服.equals(po.getSenderId())) {
			if (po.getSender() != null) {
				vo.setSenderNickName(po.getSender().getNickName());
			}
			if (po.getReceiver() != null) {
				vo.setReceiverNickName(po.getReceiver().getNickName());
			}
		}
		return vo;
	}

	public static String timePrettify(Date currentDate, Date createTime) {
		String format = "";
		if (DateUtil.year(currentDate) != DateUtil.year(createTime)) {
			format = format + "yyyy年";
		}
		if (DateUtil.isSameDay(currentDate, createTime)) {

		} else if (DateUtil.isSameDay(currentDate, DateUtil.offset(createTime, DateField.DAY_OF_YEAR, 1))) {
			format = format + "昨天 ";
		} else {
			format = format + "MM月dd日 ";
		}
		int hour = DateUtil.hour(createTime, true);
		if (hour >= 0 && hour <= 6) {
			format = format + "凌晨";
		} else if (hour >= 7 && hour < 12) {
			format = format + "上午";
		} else if (hour >= 12 && hour < 13) {
			format = format + "中午";
		} else if (hour >= 13 && hour < 18) {
			format = format + "下午";
		} else if (hour >= 18 && hour <= 23) {
			format = format + "晚上";
		}
		format = format + "HH:mm";
		String formatTime = DateUtil.format(createTime, format);
		return formatTime;
	}

}
