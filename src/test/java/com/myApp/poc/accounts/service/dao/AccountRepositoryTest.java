package com.myApp.poc.accounts.service.dao;

import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACCOUNT_ID;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_ACC_AMOUNT;
import static com.myApp.poc.MockAccountBuilderHelper.DEFAULT_INTEREST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.myApp.poc.AbstractIntegratedTest;

@DataJpaTest
@DirtiesContext
public class AccountRepositoryTest extends AbstractIntegratedTest{

	@Autowired
	AccountRepository accountRepository;
		
	@Test
	public void getAllAccounts_Test(){
		List<Account> resultAccounts = accountRepository.findAll();
		assertTrue(resultAccounts.size()==3);
	}
	
	@Test
	public void getFindById_Test(){
		Account account = accountRepository.findByAccountId(DEFAULT_ACCOUNT_ID);
		Account expectedAccount = new Account();	
		expectedAccount.setAccountId (DEFAULT_ACCOUNT_ID);
		expectedAccount.setVersion (0);
		expectedAccount.setAccountType("SAVINGS");
		expectedAccount.setAmount (DEFAULT_ACC_AMOUNT);
		expectedAccount.setInterest(DEFAULT_INTEREST);
		expectedAccount.setWithdrawalLimit(null);
		
		assertEquals(account.getAccountId(), expectedAccount.getAccountId());
		assertEquals(account.getAmount(), expectedAccount.getAmount());
		assertEquals(account.getAccountType(), expectedAccount.getAccountType());
		assertEquals(account.getInterest(), expectedAccount.getInterest());
	}

	@Override
	public void doSetup() {
		// TODO Auto-generated method stub
		
	}


	
}
