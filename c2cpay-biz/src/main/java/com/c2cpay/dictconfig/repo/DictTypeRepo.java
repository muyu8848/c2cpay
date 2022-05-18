package com.c2cpay.dictconfig.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.dictconfig.domain.DictType;

public interface DictTypeRepo extends JpaRepository<DictType, String>, JpaSpecificationExecutor<DictType> {

	DictType findByDictTypeCode(String dictTypeCode);

}
