package com.c2cpay.merchant.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.merchant.domain.Merchant;

public interface MerchantRepo extends JpaRepository<Merchant, String>, JpaSpecificationExecutor<Merchant> {

	Merchant findByUserNameAndDeletedFlagIsFalse(String userName);

}
