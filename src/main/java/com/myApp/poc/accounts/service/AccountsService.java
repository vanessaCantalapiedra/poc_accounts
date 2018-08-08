package com.myApp.poc.accounts.service;

import java.util.List;
import com.myApp.poc.accounts.service.model.AccountDecorator;
import com.myApp.poc.accounts.service.model.AccountQueryFilter;
import com.myApp.poc.accounts.service.model.TransactionRequest;
import com.myApp.poc.accounts.service.model.TransactionType;

public interface AccountsService {

	List<AccountDecorator> getAccountsDecorator(AccountQueryFilter filter);

	AccountDecorator getAccountDecorator(Integer accountId);

	List<AccountDecorator> performTransaction(TransactionType transactionType, TransactionRequest request);
}
