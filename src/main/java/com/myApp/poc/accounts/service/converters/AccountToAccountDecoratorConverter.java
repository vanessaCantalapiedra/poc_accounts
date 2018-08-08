package com.myApp.poc.accounts.service.converters;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import com.myApp.poc.accounts.service.dao.Account;
import com.myApp.poc.accounts.service.model.AccountDecorator;
import com.myApp.poc.accounts.service.model.AccountType;
import com.myApp.poc.accounts.service.resolvers.metadata.CheckingMetadataResolver;
import com.myApp.poc.accounts.service.resolvers.metadata.DefaultMetadataResolver;
import com.myApp.poc.accounts.service.resolvers.metadata.MetadataResolver;
import com.myApp.poc.accounts.service.resolvers.metadata.SavingsMetadataResolver;

@Component
public class AccountToAccountDecoratorConverter implements Converter<Account, AccountDecorator> {

	private final SavingsMetadataResolver savingsMetadataResolver;
	private final CheckingMetadataResolver checkingMetadataResolver;
	private final DefaultMetadataResolver defaultMetadataResolver;
	private final static HashMap<AccountType, MetadataResolver> resolvers = new HashMap();

	@Autowired
	public AccountToAccountDecoratorConverter(final SavingsMetadataResolver savingsMetadataResolver,
			final CheckingMetadataResolver checkingMetadataResolver,
			final DefaultMetadataResolver defaultMetadataResolver) {
		this.savingsMetadataResolver = savingsMetadataResolver;
		this.checkingMetadataResolver = checkingMetadataResolver;
		this.defaultMetadataResolver = defaultMetadataResolver;

		// initialize resolvers
		resolvers.put(AccountType.CHECKING, checkingMetadataResolver);
		resolvers.put(AccountType.SAVINGS, savingsMetadataResolver);
		resolvers.put(AccountType.UNKNOWN, defaultMetadataResolver);
	}

	@Override
	public AccountDecorator convert(Account source) {
		return AccountDecorator.builder().source(source)
				.metadataResolver(() -> resolvers.get(AccountType.getEnum(source.getAccountType())).getMetadata(source))
				.build();
	}

}
