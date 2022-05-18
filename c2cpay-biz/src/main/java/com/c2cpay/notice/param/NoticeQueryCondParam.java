package com.c2cpay.notice.param;

import com.c2cpay.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeQueryCondParam extends PageParam {
	
	private String title;

}
