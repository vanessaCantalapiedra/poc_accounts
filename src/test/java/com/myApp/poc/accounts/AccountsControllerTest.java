package com.myApp.poc.accounts;

import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACCOUNT_ID;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACCOUNT_ID_2;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACCOUNT_ID_3;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACC_AMOUNT;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_INTEREST;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_TX_AMOUNT;
import static com.myApp.poc.MockAccountBuilderHelper.ERROR_ACCOUNT_ID;
import static com.myApp.poc.MockAccountBuilderHelper.ERROR_TX_AMOUNT;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myApp.poc.AbstractIntegratedTest;
import com.myApp.poc.accounts.service.dao.AccountRepository;
import com.myApp.poc.accounts.service.model.TransactionRequest;
import com.myApp.poc.accounts.service.model.TransactionType;

@EnableWebMvc
@SqlGroup({ @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:data.sql") })
public class AccountsControllerTest extends AbstractIntegratedTest {
	private static final String URL_ACCOUNTS = "/v1/accounts";
	private static final String URL_ACCOUNT = "/v1/accounts/" + DEFAULT_ACCOUNT_ID;
	private static final String URL_TRANSACTIONS = "/v1/accounts/" + DEFAULT_ACCOUNT_ID + "?transaction=";
	private static final String URL_TRANSACTIONS_2 = "/v1/accounts/" + DEFAULT_ACCOUNT_ID_2 + "?transaction=";

	@Mock
	private AccountRepository accountRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	private static TransactionRequest DEFAULT_TX_REQUEST = TransactionRequest.builder().amount(DEFAULT_TX_AMOUNT)
			.build();

	@Override
	public void doSetup() {
		// load mocks of the accounts
	}

	@Test
	public void getAccounts_OK() throws Exception {

		this.mockMvc.perform(get(URL_ACCOUNTS)).andExpect(status().isOk()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty()).andDo(print()).andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$.*", hasSize(3))).andExpect(jsonPath("$[0].accountId").value(DEFAULT_ACCOUNT_ID))
		.andExpect(jsonPath("$[0].amount").value(1000)).andExpect(jsonPath("$[0].accountType").value("SAVINGS"))
		.andExpect(jsonPath("$[0].interestRate").value(2))
		.andExpect(jsonPath("$[1].accountId").value(DEFAULT_ACCOUNT_ID_2))
		.andExpect(jsonPath("$[1].amount").value(1000))
		.andExpect(jsonPath("$[1].accountType").value("CHECKING"))
		.andExpect(jsonPath("$[1].withdrawalLimit").value(100));
	}

	@Test
	public void getAccounts_Filter_OK() throws Exception {

		this.mockMvc.perform(get(URL_ACCOUNTS + "?type=SAVINGS")).andExpect(status().isOk())
		.andExpect(jsonPath("$").exists()).andExpect(jsonPath("$").isNotEmpty())
		.andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(1)))
		.andExpect((jsonPath("$[0].accountId").value(DEFAULT_ACCOUNT_ID)))
		.andExpect((jsonPath("$[0].amount").value(1000)))
		.andExpect((jsonPath("$[0].accountType").value("SAVINGS")))
		.andExpect((jsonPath("$[0].interestRate").value(2)));
	}

	@Test
	public void getAccount_OK() throws Exception {

		this.mockMvc.perform(get(URL_ACCOUNT)).andExpect(status().isOk()).andDo(print())
		.andExpect(jsonPath("$").exists()).andExpect(jsonPath("$").isNotEmpty())
		.andExpect((jsonPath("$.accountId").value(DEFAULT_ACCOUNT_ID)))
		.andExpect((jsonPath("$.amount").value(1000))).andExpect((jsonPath("$.accountType").value("SAVINGS")))
		.andExpect((jsonPath("$.interestRate").value(2)));
	}

	@Test
	public void getAccount_NotFound_ERR() throws Exception {

		this.mockMvc.perform(get(URL_ACCOUNTS + "/" + ERROR_ACCOUNT_ID)).andDo(print()).andExpect(status().isNotFound())
		.andExpect(jsonPath("$").exists()).andExpect(jsonPath("$").isNotEmpty())
		.andExpect((jsonPath("$.message").value("Account id not found.")));
	}

	@Test
	public void deposit_OK() throws Exception {
		String jsonRequestStr = objectMapper.writeValueAsString(DEFAULT_TX_REQUEST);

		this.mockMvc
		.perform(put(URL_TRANSACTIONS + TransactionType.DEPOSIT).contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequestStr))
		.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect((jsonPath("$[0].amount").value(DEFAULT_TX_AMOUNT.add(DEFAULT_ACC_AMOUNT).setScale(1))));
	}

	@Test
	public void withdrawal_OK() throws Exception {
		String jsonRequestStr = objectMapper.writeValueAsString(DEFAULT_TX_REQUEST);

		this.mockMvc
		.perform(put(URL_TRANSACTIONS + TransactionType.WITHDRAWAL).contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequestStr))
		.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasSize(1))).andExpect(
				(jsonPath("$[0].amount").value(DEFAULT_ACC_AMOUNT.subtract(DEFAULT_TX_AMOUNT).setScale(1))));
	}

	@Test
	public void withdrawal_ERR() throws Exception {
		String jsonRequestStr = objectMapper
				.writeValueAsString(TransactionRequest.builder().amount(ERROR_TX_AMOUNT).build());

		this.mockMvc
		.perform(put(URL_TRANSACTIONS_2 + TransactionType.WITHDRAWAL).contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequestStr))
		.andDo(print()).andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty())
		.andExpect((jsonPath("$.message").value("The withdrawal limit has been reached")));
	}

	@Test
	public void payInterest_OK() throws Exception {
		String jsonRequestStr = objectMapper.writeValueAsString(DEFAULT_TX_REQUEST);
		BigDecimal expectedAmount = DEFAULT_ACC_AMOUNT
				.add(DEFAULT_ACC_AMOUNT.multiply(DEFAULT_INTEREST.divide(BigDecimal.valueOf(100))));

		this.mockMvc
		.perform(put(URL_TRANSACTIONS + TransactionType.PAY_INTEREST).contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequestStr))
		.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasSize(1)))
		.andExpect((jsonPath("$[0].amount").value(expectedAmount.setScale(1))));
	}

	@Test
	public void payInterest_ERR() throws Exception {
		String jsonRequestStr = objectMapper.writeValueAsString(DEFAULT_TX_REQUEST);

		this.mockMvc
		.perform(put(URL_TRANSACTIONS_2 + TransactionType.PAY_INTEREST).contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequestStr))
		.andDo(print()).andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty())
		.andExpect((jsonPath("$.message").value("Selected transaction not allowed for the account Type.")));
	}

	@Test
	public void cashTransfer_OK() throws Exception {
		String jsonRequestStr = objectMapper.writeValueAsString(
				TransactionRequest.builder().accountDst(DEFAULT_ACCOUNT_ID_3).amount(DEFAULT_TX_AMOUNT).build());

		this.mockMvc
		.perform(put(URL_TRANSACTIONS_2 + TransactionType.CASH_TRANSFER).contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequestStr))
		.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect((jsonPath("$[0].accountId").value(DEFAULT_ACCOUNT_ID_2)))
		.andExpect((jsonPath("$[0].amount").value(DEFAULT_ACC_AMOUNT.subtract(DEFAULT_TX_AMOUNT).setScale(1))))
		.andExpect((jsonPath("$[1].accountId").value(DEFAULT_ACCOUNT_ID_3)))
		.andExpect((jsonPath("$[1].amount").value(DEFAULT_TX_AMOUNT.add(DEFAULT_ACC_AMOUNT).setScale(1))));
	}

	@Test
	public void cashTransfer_SameAccId_ERROR() throws Exception {
		String jsonRequestStr = objectMapper.writeValueAsString(
				TransactionRequest.builder().accountDst(DEFAULT_ACCOUNT_ID_2).amount(ERROR_TX_AMOUNT).build());

		this.mockMvc
		.perform(put(URL_TRANSACTIONS_2 + TransactionType.PAY_INTEREST).contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequestStr))
		.andDo(print()).andExpect(status().is4xxClientError()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty())
		.andExpect((jsonPath("$.message").value("Selected transaction not allowed for the account Type.")));
	}

	@Test
	public void cashTransfer_notAllowedAccId_ERROR() throws Exception {
		String jsonRequestStr = objectMapper.writeValueAsString(
				TransactionRequest.builder().accountDst(DEFAULT_ACCOUNT_ID).amount(ERROR_TX_AMOUNT).build());

		this.mockMvc
		.perform(put(URL_TRANSACTIONS_2 + TransactionType.PAY_INTEREST).contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequestStr))
		.andDo(print()).andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$").exists())
		.andExpect(jsonPath("$").isNotEmpty())
		.andExpect((jsonPath("$.message").value("Selected transaction not allowed for the account Type.")));
	}

}