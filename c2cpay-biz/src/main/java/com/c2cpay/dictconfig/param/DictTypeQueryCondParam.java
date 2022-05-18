package com.c2cpay.dictconfig.param;

import com.c2cpay.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DictTypeQueryCondParam extends PageParam {
	
	private String dictTypeCode;

	private String dictTypeName;

}
