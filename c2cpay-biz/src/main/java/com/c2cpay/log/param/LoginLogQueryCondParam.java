package com.c2cpay.log.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.c2cpay.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LoginLogQueryCondParam extends PageParam {

	private String ipAddr;

	private String userName;
	
	private String subSystem;

	private String state;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endTime;

}
