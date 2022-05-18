package com.c2cpay.notice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2cpay.common.vo.PageResult;
import com.c2cpay.common.vo.Result;
import com.c2cpay.notice.param.NoticeQueryCondParam;
import com.c2cpay.notice.service.NoticeService;
import com.c2cpay.notice.vo.NoticeAbstractVO;
import com.c2cpay.notice.vo.NoticeVO;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/notice")
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	@PostMapping("/allMarkRead")
	public Result<String> allMarkRead() {
		noticeService.allMarkRead(StpUtil.getLoginIdAsString());
		return Result.success();
	}

	@PostMapping("/markRead")
	public Result<String> markRead(String id) {
		noticeService.markRead(id, StpUtil.getLoginIdAsString());
		return Result.success();
	}

	@GetMapping("/findUnreadNoticeId")
	public Result<List<String>> findUnreadNoticeId() {
		return Result.success(noticeService.findUnreadNoticeId(StpUtil.getLoginIdAsString()));
	}

	@GetMapping("/getNoticeDetail")
	public Result<NoticeVO> getNoticeDetail(String id) {
		return Result.success(noticeService.findById(id));
	}

	@GetMapping("/findNoticeAbstractByPage")
	public Result<PageResult<NoticeAbstractVO>> findNoticeAbstractByPage(NoticeQueryCondParam param) {
		return Result.success(noticeService.findNoticeAbstractByPage(param));
	}

}
