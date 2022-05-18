package com.c2cpay.storage.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.c2cpay.storage.domain.Storage;

public interface StorageRepo extends JpaRepository<Storage, String>, JpaSpecificationExecutor<Storage> {

}
