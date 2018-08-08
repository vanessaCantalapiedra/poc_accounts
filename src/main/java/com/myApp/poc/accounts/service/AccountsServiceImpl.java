package com.myApp.poc.accounts.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import com.myApp.poc.ConfigProperties;
import com.myApp.poc.ConfigProperties.AccountConfig;
import com.myApp.poc.accounts.service.converters.AccountToAccountDecoratorConverter;
import com.myApp.poc.accounts.service.dao.Account;
import com.myApp.poc.accounts.service.dao.AccountRepository;
import com.myApp.poc.accounts.service.exceptions.AccountIdMissingException;
import com.myApp.poc.accounts.service.exceptions.AccountIdNotFoundException;
import com.myApp.poc.accounts.service.exceptions.AccountIdsMatchException;
import com.myApp.poc.accounts.service.exceptions.AccountServiceInternalErrorException;
import com.myApp.poc.accounts.service.exceptions.CommitTransactionException;
import com.myApp.poc.accounts.service.exceptions.TransactionNotAllowedException;
import com.myApp.poc.accounts.service.exceptions.TransactionNotFoundException;
import com.myApp.poc.accounts.service.exceptions.WithdrawalLimitException;
import com.myApp.poc.accounts.service.model.AccountDecorator;
import com.myApp.poc.accounts.service.model.AccountQueryFilter;
import com.myApp.poc.accounts.service.model.AccountType;
import com.myApp.poc.accounts.service.model.TransactionRequest;
import com.myApp.poc.accounts.service.model.TransactionType;

@Service
public class AccountsServiceImpl implements AccountsService {

	private final AccountRepository accountRepository;
	private final AccountToAccountDecoratorConverter accountToAccountDecoratorConverter;
	private final ConfigProperties configProperties;
	private final List<AccountConfig> configList;

	@Autowired
	public AccountsServiceImpl(AccountRepository accountRepoository,
			AccountToAccountDecoratorConverter accountToAccountDecoratorConverter, ConfigProperties configProperties) {
		this.accountRepository = accountRepoository;
		this.accountToAccountDecoratorConverter = accountToAccountDecoratorConverter;
		this.configProperties = configProperties;
		this.configList = configProperties.getAccountsConfig();
	}

	@Override
	public List<AccountDecorator> getAccountsDecorator(AccountQueryFilter filter) {
		return tryRunAndRethrow(() -> accountRepository.findAll()).stream().filter(filter::applyAccountTypeFilter)
				.map(accountToAccountDecoratorConverter::convert).collect(Collectors.toList());
	}

	@Override
	public AccountDecorator getAccountDecorator(Integer accountId) {
		Account acc = accountRepository.findByAccountId(accountId);
		return getAccount(accountId).map(accountToAccountDecoratorConverter::convert)
				.orElseThrow(AccountIdNotFoundException::new);
	}

	@Override
	public List<AccountDecorator> performTransaction(TransactionType transactionType, TransactionRequest request) {
		Account account = new Account(
				getAccount(request.getAccountSrcId()).orElseThrow(AccountIdNotFoundException::new));

		checkAllowedTransactions(AccountType.getEnum(account.getAccountType()), transactionType);
		validateRequest(transactionType, request);

		List<Account> accounts = new ArrayList();
		accounts.add(account);
		resolveTransaction(transactionType, request).accept(accounts);

		// i have decided to update a list, instead of a single object in order to
		// support several operations
		// in different accounts on the same transaction
		try {
			// used for concurrencyTest
			/*
			 * try { Thread.sleep(4000); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
			return accountRepository.saveAll(accounts).stream().map(accountToAccountDecoratorConverter::convert)
					.collect(Collectors.toList());

		} catch (IllegalArgumentException e) {
			throw new CommitTransactionException(e);
		} catch (ObjectOptimisticLockingFailureException e) {
			throw new CommitTransactionException(e);
		}
	}

	private <T> T tryRunAndRethrow(Supplier<T> supplier) {
		try {
			return supplier.get();
		} catch (RuntimeException e) {
			throw new AccountServiceInternalErrorException();
		}
	}

	private void checkAllowedTransactions(AccountType accountType, TransactionType transactionType) {
		configList.stream().filter((config) -> config.getAccountType().equals(accountType)).findFirst()
				.orElseThrow(TransactionNotFoundException::new).getAllowedTransactions().stream()
				.filter((tranType) -> tranType.equals(transactionType)).findFirst()
				.orElseThrow(TransactionNotAllowedException::new);
	}

	private Optional<Account> getAccount(Integer accountId) {
		return Optional.ofNullable(tryRunAndRethrow(() -> accountRepository.findByAccountId(accountId)));
	}

	private void validateRequest(TransactionType transactionType, TransactionRequest request) {
		switch (transactionType) {
		case CASH_TRANSFER:
			Integer validId = Optional.ofNullable(request.getAccountDst()).orElseThrow(AccountIdMissingException::new);
			if (validId.equals(request.getAccountSrcId()))
				throw new AccountIdsMatchException();

		default:
			break;
		}
	}

	private Consumer<List<Account>> resolveTransaction(TransactionType transactionType, TransactionRequest request) {
		switch (transactionType) {
		case DEPOSIT:
			return (accounts) -> accounts.stream()
					.forEach((account) -> account.setAmount(account.getAmount().add(request.getAmount())));

		case WITHDRAWAL:
			return (accounts) -> accounts.stream().forEach((account) -> {
				BigDecimal newAmount = account.getAmount().subtract(request.getAmount());
				// we consider that if the withdrawal limit is not relevant
				// for that particular account type, it should be set to null
				if (account.getWithdrawalLimit() != null && newAmount.compareTo(account.getWithdrawalLimit()) < 0)
					throw new WithdrawalLimitException();
				account.setAmount(account.getAmount().subtract(request.getAmount()));
			});

		case CASH_TRANSFER:
			return (accounts) -> {
				Account accountDst = new Account(
						getAccount(request.getAccountDst()).orElseThrow(AccountIdNotFoundException::new));
				checkAllowedTransactions(AccountType.getEnum(accountDst.getAccountType()),
						transactionType.CASH_TRANSFER);

				resolveTransaction(transactionType.WITHDRAWAL, request).accept(accounts);

				List<Account> accountsDst = (Arrays.asList(accountDst));
				resolveTransaction(transactionType.DEPOSIT, request).accept(accountsDst);
				accounts.addAll(accountsDst);
			};
		case PAY_INTEREST:
			return (accounts) -> accounts.stream().forEach((account) -> account.setAmount(account.getAmount()
					.add(account.getAmount().multiply(account.getInterest().divide(BigDecimal.valueOf(100))))));
		default:
			return (accounts) -> {
				return;
			}; // this case should never happen
		}
	}
}
