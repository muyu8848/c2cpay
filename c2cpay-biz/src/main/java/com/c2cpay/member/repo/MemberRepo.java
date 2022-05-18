package com.c2cpay.member.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.member.domain.Member;

public interface MemberRepo extends JpaRepository<Member, String>, JpaSpecificationExecutor<Member> {

	List<Member> findByDeletedFlagIsFalse();

	Member findByMobileAndDeletedFlagIsFalse(String mobile);

	Member findTopByWalletAddrAndDeletedFlagIsFalse(String walletAddr);

}
