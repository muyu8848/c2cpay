package com.c2cpay.notice.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.notice.domain.Notice;

public interface NoticeRepo extends JpaRepository<Notice, String>, JpaSpecificationExecutor<Notice> {

	List<Notice> findByIdInAndDeletedFlagIsFalse(List<String> ids);

}
