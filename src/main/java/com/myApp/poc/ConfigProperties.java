package com.myApp.poc;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import com.myApp.poc.accounts.service.model.AccountType;
import com.myApp.poc.accounts.service.model.TransactionType;

@Data
@Configuration
@ConfigurationProperties("app.config")
public class ConfigProperties {

	private List<AccountConfig> accountsConfig;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class AccountConfig {

		private AccountType accountType;
		private List<TransactionType> allowedTransactions;

	}
}
