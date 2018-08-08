package com.myApp.poc.accounts.service.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	Account findByAccountId(final Integer accountId);

	List<Account> findAll();

}
