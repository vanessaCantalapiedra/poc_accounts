package com.myApp.poc.accounts.service.model;

import java.util.Optional;
import com.myApp.poc.accounts.service.dao.Account;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AccountQueryFilter {

	private Optional<String> accountType;
	// TODO more filters can be added

	public boolean applyAccountTypeFilter(Account account) {
		return !this.getAccountType().isPresent() || this.getAccountType().get().equals(account.getAccountType());
	}

}
