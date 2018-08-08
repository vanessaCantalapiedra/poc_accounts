package com.myApp.poc.accounts.service;

import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACCOUNT_ID;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACCOUNT_ID_2;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACCOUNT_ID_3;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACC_AMOUNT;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_TX_AMOUNT;
import static com.myApp.poc.MockAccountBuilderHelper.ERROR_ACCOUNT_ID;
import static com.myApp.poc.MockAccountBuilderHelper.ERROR_TX_AMOUNT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import com.myApp.poc.ConfigProperties;
import com.myApp.poc.ConfigProperties.AccountConfig;
import com.myApp.poc.MockAccountBuilderHelper;
import com.myApp.poc.accounts.service.converters.AccountToAccountDecoratorConverter;
import com.myApp.poc.accounts.service.dao.Account;
import com.myApp.poc.accounts.service.dao.AccountRepository;
import com.myApp.poc.accounts.service.exceptions.AccountIdMissingException;
import com.myApp.poc.accounts.service.exceptions.AccountIdNotFoundException;
import com.myApp.poc.accounts.service.exceptions.AccountIdsMatchException;
import com.myApp.poc.accounts.service.exceptions.TransactionNotAllowedException;
import com.myApp.poc.accounts.service.exceptions.WithdrawalLimitException;
import com.myApp.poc.accounts.service.model.AccountDecorator;
import com.myApp.poc.accounts.service.model.AccountQueryFilter;
import com.myApp.poc.accounts.service.model.AccountType;
import com.myApp.poc.accounts.service.model.TransactionRequest;
import com.myApp.poc.accounts.service.model.TransactionType;
import com.myApp.poc.accounts.service.resolvers.metadata.CheckingMetadataResolver;
import com.myApp.poc.accounts.service.resolvers.metadata.DefaultMetadataResolver;
import com.myApp.poc.accounts.service.resolvers.metadata.SavingsMetadataResolver;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

	@Mock
	private AccountRepository accountRepository;
	@Mock
	private ConfigProperties configProperties;
	@Mock
	private AccountToAccountDecoratorConverter accountConverter;
	@Mock
	private SavingsMetadataResolver savingsMetadataResolver;
	@Mock
	private CheckingMetadataResolver checkingMetadataResolver;
	@Mock
	private DefaultMetadataResolver defaultMetadataResolver;
	@Captor
	private ArgumentCaptor<List<Account>> accountsCaptor;

	private AccountsServiceImpl service;
	private List<Account> mockAccounts;

	@Before
	public void setup() {
		// load mocks of the accounts
		mockAccounts = new MockAccountBuilderHelper().loadAccounts();

		when(accountRepository.findByAccountId(eq(MockAccountBuilderHelper.ERROR_ACCOUNT_ID))).thenReturn(null);

		when(accountRepository.findByAccountId(eq(DEFAULT_ACCOUNT_ID)))
				.thenReturn(mockAccounts.get(DEFAULT_ACCOUNT_ID));
		when(accountRepository.findByAccountId(eq(DEFAULT_ACCOUNT_ID_2)))
				.thenReturn(mockAccounts.get(DEFAULT_ACCOUNT_ID_2));
		when(accountRepository.findByAccountId(eq(DEFAULT_ACCOUNT_ID_3)))
				.thenReturn(mockAccounts.get(DEFAULT_ACCOUNT_ID_3));

		when(accountRepository.saveAll(Matchers.anyListOf(Account.class))).thenAnswer(new Answer<List<Account>>() {
			@Override
			public List<Account> answer(InvocationOnMock invocation) throws Throwable {
				return (List<Account>) invocation.getArguments()[0];
			}
		});

		when(accountConverter.convert(Matchers.any())).thenAnswer(new Answer<AccountDecorator>() {
			@Override
			public AccountDecorator answer(InvocationOnMock invocation) throws Throwable {
				return AccountDecorator.builder().source((Account) invocation.getArguments()[0]).build();
			}
		});

		when(configProperties
				.getAccountsConfig())
						.thenReturn(
								Arrays.asList(
										AccountConfig.builder().accountType(AccountType.CHECKING)
												.allowedTransactions(Arrays.asList(TransactionType.DEPOSIT,
														TransactionType.WITHDRAWAL, TransactionType.CASH_TRANSFER))
												.build(),
										AccountConfig.builder().accountType(AccountType.SAVINGS)
												.allowedTransactions(Arrays.asList(TransactionType.DEPOSIT,
														TransactionType.WITHDRAWAL, TransactionType.PAY_INTEREST))
												.build()));

		this.service = new AccountsServiceImpl(accountRepository, accountConverter, configProperties);
	}

	@Test
	public void getAccounts_OK() {
		when(accountRepository.findAll()).thenReturn(mockAccounts);
		List<AccountDecorator> result = service
				.getAccountsDecorator(AccountQueryFilter.builder().accountType(Optional.empty()).build());
		List<AccountDecorator> expected = Arrays.asList(
				AccountDecorator.builder().source(mockAccounts.get(DEFAULT_ACCOUNT_ID))
						.metadataResolver(
								() -> savingsMetadataResolver.getMetadata(mockAccounts.get(DEFAULT_ACCOUNT_ID)))
						.build(),
				AccountDecorator.builder().source(mockAccounts.get(DEFAULT_ACCOUNT_ID_2))
						.metadataResolver(
								() -> checkingMetadataResolver.getMetadata(mockAccounts.get(DEFAULT_ACCOUNT_ID_2)))
						.build(),
				AccountDecorator.builder().source(mockAccounts.get(DEFAULT_ACCOUNT_ID_3))
						.metadataResolver(
								() -> checkingMetadataResolver.getMetadata(mockAccounts.get(DEFAULT_ACCOUNT_ID_3)))
						.build());

		assertEquals(expected.size(), result.size());
		assertEquals(result.get(0).getAccountType(), "SAVINGS");
		assertEquals(expected.get(0).getSource(), result.get(0).getSource());
		assertEquals(result.get(1).getAccountType(), "CHECKING");
		assertEquals(expected.get(1).getSource(), result.get(1).getSource());
	}

	@Test
	public void getAccounts_Filtered_BySavings_OK() {
		when(accountRepository.findAll()).thenReturn(mockAccounts);

		List<AccountDecorator> result = service.getAccountsDecorator(
				AccountQueryFilter.builder().accountType(Optional.of(AccountType.SAVINGS.toString())).build());

		List<AccountDecorator> expected = Arrays
				.asList(AccountDecorator.builder().source(mockAccounts.get(DEFAULT_ACCOUNT_ID))
						.metadataResolver(
								() -> savingsMetadataResolver.getMetadata(mockAccounts.get(DEFAULT_ACCOUNT_ID)))
						.build());

		assertEquals(expected.size(), result.size());
		assertEquals(result.get(0).getAccountType(), "SAVINGS");
		assertEquals(expected.get(0).getSource(), result.get(0).getSource());

	}

	@Test
	public void getAccounts_Empty() {
		when(accountRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

		List<AccountDecorator> result = service.getAccountsDecorator(
				AccountQueryFilter.builder().accountType(Optional.of(AccountType.SAVINGS.toString())).build());

		assertEquals(Collections.EMPTY_LIST, result);
	}

	@Test
	public void getAccount_OK() {
		AccountDecorator result = service.getAccountDecorator(DEFAULT_ACCOUNT_ID);
		AccountDecorator expected = AccountDecorator.builder().source(mockAccounts.get(DEFAULT_ACCOUNT_ID))
				.metadataResolver(() -> savingsMetadataResolver.getMetadata(mockAccounts.get(DEFAULT_ACCOUNT_ID)))
				.build();

		assertEquals(expected.getSource(), result.getSource());
	}

	@Test(expected = AccountIdNotFoundException.class)
	public void getAccount_ERROR() {

		service.getAccountDecorator(ERROR_ACCOUNT_ID);
		verifyZeroInteractions(accountConverter);
	}

	@Test
	public void performTransaction_Deposit_OK() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.DEPOSIT,
				TransactionRequest.builder().accountSrcId(DEFAULT_ACCOUNT_ID).amount(DEFAULT_TX_AMOUNT).build());

		verify(accountRepository).saveAll(accountsCaptor.capture());
		List<Account> capturedAccounts = accountsCaptor.<List<Account>>getValue();

		assertEquals(capturedAccounts.size(), result.size());
		assertEquals(capturedAccounts.get(0).getAccountId(), result.get(0).getSource().getAccountId());
		assertEquals(DEFAULT_ACC_AMOUNT.add(DEFAULT_TX_AMOUNT), result.get(0).getSource().getAmount());

	}

	@Test
	public void performTransaction_Withdrawal_OK() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.WITHDRAWAL,
				TransactionRequest.builder().accountSrcId(DEFAULT_ACCOUNT_ID).amount(DEFAULT_TX_AMOUNT).build());

		verify(accountRepository).saveAll(accountsCaptor.capture());

		List<Account> capturedAccounts = accountsCaptor.<List<Account>>getValue();

		assertEquals(capturedAccounts.size(), result.size());
		assertEquals(capturedAccounts.get(0).getAccountId(), result.get(0).getSource().getAccountId());
		assertEquals(DEFAULT_ACC_AMOUNT.subtract(DEFAULT_TX_AMOUNT), result.get(0).getSource().getAmount());

	}

	@Test(expected = WithdrawalLimitException.class)
	public void performTransaction_Withdrawal_ERR() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.WITHDRAWAL,
				TransactionRequest.builder().accountSrcId(DEFAULT_ACCOUNT_ID_2).amount(ERROR_TX_AMOUNT).build());

		verify(accountRepository, never()).saveAll(Matchers.anyList());
	}

	@Test
	public void performTransaction_PayInterest_OK() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.PAY_INTEREST,
				TransactionRequest.builder().accountSrcId(DEFAULT_ACCOUNT_ID).amount(DEFAULT_TX_AMOUNT).build());

		verify(accountRepository).saveAll(accountsCaptor.capture());
		List<Account> capturedAccounts = accountsCaptor.<List<Account>>getValue();

		assertEquals(capturedAccounts.size(), result.size());
		assertEquals(capturedAccounts.get(0).getAccountId(), result.get(0).getSource().getAccountId());
		assertEquals(
				DEFAULT_ACC_AMOUNT.add(DEFAULT_ACC_AMOUNT
						.multiply(result.get(0).getSource().getInterest().divide(BigDecimal.valueOf(100)))),
				result.get(0).getSource().getAmount());
	}

	@SuppressWarnings("deprecation")
	@Test(expected = TransactionNotAllowedException.class)
	public void performTransaction_PayInterest_ERROR() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.PAY_INTEREST,
				TransactionRequest.builder().accountSrcId(DEFAULT_ACCOUNT_ID_2).amount(DEFAULT_TX_AMOUNT).build());

		verifyZeroInteractions(accountRepository.saveAll(Matchers.anyList()));
	}

	@Test(expected = TransactionNotAllowedException.class)
	public void performTransaction_CashTransfer_SrcAccId_ERROR() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.CASH_TRANSFER,
				TransactionRequest.builder().accountDst(DEFAULT_ACCOUNT_ID_2).accountSrcId(DEFAULT_ACCOUNT_ID)
						.amount(DEFAULT_TX_AMOUNT).build());

		verifyZeroInteractions(accountRepository.saveAll(Matchers.anyList()));
	}

	@Test(expected = AccountIdMissingException.class)
	public void performTransaction_CashTransfer_MissingId_ERROR() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.CASH_TRANSFER,
				TransactionRequest.builder().accountSrcId(DEFAULT_ACCOUNT_ID_2).amount(DEFAULT_TX_AMOUNT).build());

		verifyZeroInteractions(accountRepository.saveAll(Matchers.anyList()));
	}

	@Test(expected = AccountIdsMatchException.class)
	public void performTransaction_CashTransfer_EqualsId_ERROR() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.CASH_TRANSFER,
				TransactionRequest.builder().accountDst(DEFAULT_ACCOUNT_ID_2).accountSrcId(DEFAULT_ACCOUNT_ID_2)
						.amount(DEFAULT_TX_AMOUNT).build());

		verifyZeroInteractions(accountRepository.saveAll(Matchers.anyList()));
	}

	@Test(expected = TransactionNotAllowedException.class)
	public void performTransaction_CashTransfer_DstAccId_ERROR() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.CASH_TRANSFER,
				TransactionRequest.builder().accountDst(DEFAULT_ACCOUNT_ID).accountSrcId(DEFAULT_ACCOUNT_ID_2)
						.amount(DEFAULT_TX_AMOUNT).build());

		verifyZeroInteractions(accountRepository.saveAll(Matchers.anyList()));
	}

	@Test
	public void performTransaction_CashTransfer_OK() {

		List<AccountDecorator> result = service.performTransaction(TransactionType.CASH_TRANSFER,
				TransactionRequest.builder().accountDst(DEFAULT_ACCOUNT_ID_3).accountSrcId(DEFAULT_ACCOUNT_ID_2)
						.amount(DEFAULT_TX_AMOUNT).build());

		verify(accountRepository).saveAll(accountsCaptor.capture());
		List<Account> capturedAccounts = accountsCaptor.<List<Account>>getValue();

		assertEquals(capturedAccounts.size(), result.size());
		assertEquals(capturedAccounts.get(0).getAccountId(), result.get(0).getSource().getAccountId());
		assertEquals(capturedAccounts.get(1).getAccountId(), result.get(1).getSource().getAccountId());
		assertEquals(result.get(0).getSource().getAmount(), DEFAULT_ACC_AMOUNT.subtract(DEFAULT_TX_AMOUNT));
		assertEquals(result.get(1).getSource().getAmount(), DEFAULT_ACC_AMOUNT.add(DEFAULT_TX_AMOUNT));

	}

}
