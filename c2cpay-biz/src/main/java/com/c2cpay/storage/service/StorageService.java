package com.c2cpay.storage.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.c2cpay.common.exception.BizException;
import com.c2cpay.common.utils.IdUtils;
import com.c2cpay.setting.domain.SystemSetting;
import com.c2cpay.setting.repo.SystemSettingRepo;
import com.c2cpay.storage.domain.Storage;
import com.c2cpay.storage.repo.StorageRepo;
import com.c2cpay.storage.vo.StorageVO;

@Service
public class StorageService {

	@Autowired
	private StorageRepo storageRepo;

	@Autowired
	private SystemSettingRepo systemSettingRepo;

	public StorageVO findById(String id) {
		return StorageVO.convertFor(storageRepo.getOne(id));
	}

	public Resource loadAsResource(String id) {
		try {
			SystemSetting systemSetting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
			String localStoragePath = systemSetting.getLocalStoragePath();
			Path file = Paths.get(localStoragePath).resolve(id);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String upload(InputStream inputStream, Long fileSize, String fileType, String fileName) {
		if (!fileType.startsWith("image/")) {
			throw new BizException("只能上传图片");
		}
		String id = IdUtils.getId();
		SystemSetting systemSetting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		String localStoragePath = systemSetting.getLocalStoragePath();
		try {
			Files.copy(inputStream, Paths.get(localStoragePath).resolve(id), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file " + id, e);
		}

		Storage storage = new Storage();
		storage.setId(id);
		storage.setFileName(fileName);
		storage.setFileType(fileType);
		storage.setFileSize(fileSize);
		storage.setUploadTime(new Date());
		storageRepo.save(storage);
		return id;
	}

}
