package com.myApp.poc;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import com.myApp.poc.accounts.service.dao.Account;
import lombok.Data;

@Data
//to be called in the setUp , that is before each test
public class MockAccountBuilderHelper {

	public static final Integer DEFAULT_ACCOUNT_ID = 0;
	public static final Integer DEFAULT_ACCOUNT_ID_2 = 1;
	public static final Integer DEFAULT_ACCOUNT_ID_3 = 2;
	public static final Integer ERROR_ACCOUNT_ID = 4;
	public static final BigDecimal DEFAULT_ACC_AMOUNT = BigDecimal.valueOf(1000).setScale(2);
	public static final BigDecimal DEFAULT_TX_AMOUNT = BigDecimal.valueOf(10).setScale(2);
	public static final BigDecimal ERROR_TX_AMOUNT = BigDecimal.valueOf(910).setScale(2);
	public static final BigDecimal DEFAULT_INTEREST = BigDecimal.valueOf(2).setScale(2);

	private Account account1;// TODO create a list with mock accounts
	private Account account2;
	private Account account3;

	public List<Account> loadAccounts() {
		account1 = new Account();
		account2 = new Account();
		account3 = new Account();

		account1.setAccountId(DEFAULT_ACCOUNT_ID);
		account1.setAccountType("SAVINGS");
		account1.setAmount(DEFAULT_ACC_AMOUNT);
		account1.setInterest(BigDecimal.valueOf(2));
		account1.setVersion(0);
		account1.setWithdrawalLimit(BigDecimal.ZERO);
		account1.setResgistrationDate(Date.valueOf(LocalDate.of(2018, 4, 3)));
		account1.setUpdateDate(Date.valueOf(LocalDate.of(2018, 4, 3)));

		account2.setAccountId(DEFAULT_ACCOUNT_ID_2);
		account2.setAccountType("CHECKING");
		account2.setAmount(DEFAULT_ACC_AMOUNT);
		account2.setInterest(BigDecimal.valueOf(0));
		account2.setVersion(0);
		account2.setWithdrawalLimit(BigDecimal.valueOf(100));
		account2.setResgistrationDate(Date.valueOf(LocalDate.of(2018, 4, 3)));
		account2.setUpdateDate(Date.valueOf(LocalDate.of(2018, 4, 3)));

		account3.setAccountId(DEFAULT_ACCOUNT_ID_3);
		account3.setAccountType("CHECKING");
		account3.setAmount(DEFAULT_ACC_AMOUNT);
		account3.setInterest(BigDecimal.valueOf(0));
		account3.setVersion(0);
		account3.setWithdrawalLimit(BigDecimal.valueOf(100));
		account3.setResgistrationDate(Date.valueOf(LocalDate.of(2018, 4, 3)));
		account3.setUpdateDate(Date.valueOf(LocalDate.of(2018, 4, 3)));

		return Arrays.asList(account1, account2, account3);

	}
}
