package com.myApp.poc.accounts.service.model;

import java.util.stream.Stream;

public enum AccountType {
	SAVINGS, CHECKING, UNKNOWN;

	public static AccountType getEnum(String enumType) {
		return Stream.of(AccountType.values()).filter(m -> m.name().equalsIgnoreCase(enumType)).findFirst()
				.orElse(UNKNOWN);
	}

}
