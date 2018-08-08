package com.myApp.poc.accounts.service.model;

import java.util.stream.Stream;

public enum TransactionType {
	DEPOSIT, WITHDRAWAL, PAY_INTEREST, CASH_TRANSFER, UNKNOWN;

	public static TransactionType getEnum(String enumType) {
		return Stream.of(TransactionType.values()).filter(m -> m.name().equalsIgnoreCase(enumType)).findFirst()
				.orElse(UNKNOWN);
	}

}
