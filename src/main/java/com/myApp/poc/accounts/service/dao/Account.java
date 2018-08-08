package com.myApp.poc.accounts.service.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "ACCOUNTS")
public class Account implements Serializable {

	// copy constructor
	public Account(Account account) {
		this.accountId = account.getAccountId();
		this.version = account.getVersion();
		this.accountType = account.getAccountType();
		this.amount = account.getAmount();
		this.interest = account.getInterest();
		this.resgistrationDate = account.getResgistrationDate();
		this.updateDate = account.updateDate;
		this.withdrawalLimit = account.getWithdrawalLimit();
	}

	@Id
	@Column(name = "account_id", nullable = false)
	private Integer accountId;

	@Version
	@Column(name = "version", nullable = false)
	private Integer version;

	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "withdrawal_limit")
	private BigDecimal withdrawalLimit;

	@Column(name = "interest_rate")
	private BigDecimal interest;

	@Column(name = "account_type")
	private String accountType;

	@Column(name = "registration_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date resgistrationDate;

	@Column(name = "update_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@LastModifiedDate
	private Date updateDate;

}