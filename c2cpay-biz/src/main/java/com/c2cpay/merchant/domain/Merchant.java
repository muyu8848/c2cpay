package com.c2cpay.merchant.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant")
@DynamicInsert(true)
@DynamicUpdate(true)
public class Merchant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String userName;

	private String merchantName;
	
	private Double balance;
	
	private String walletAddr;
	
	private String ipWhiteList;

	private String loginPwd;
	
	private String payPwd;
	
	private String apiSecretKey;

	private String googleSecretKey;

	private Date googleAuthBindTime;

	private String state;
	
	private Date createTime;

	private Date latelyLoginTime;

	private Boolean deletedFlag;

	private Date deletedTime;

	@Version
	private Long version;
	
	public void deleted() {
		this.setDeletedFlag(true);
		this.setDeletedTime(new Date());
	}
	
	public void unBindGoogleAuth() {
		this.setGoogleSecretKey(null);
		this.setGoogleAuthBindTime(null);
	}
	
	public void bindGoogleAuth(String googleSecretKey) {
		this.setGoogleSecretKey(googleSecretKey);
		this.setGoogleAuthBindTime(new Date());
	}
}
