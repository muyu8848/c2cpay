package com.c2cpay.backgroundaccount.param;

import com.c2cpay.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BackgroundAccountQueryCondParam extends PageParam {
	
	private String userName;

}
