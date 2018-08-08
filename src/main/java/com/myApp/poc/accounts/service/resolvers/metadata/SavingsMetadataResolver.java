package com.myApp.poc.accounts.service.resolvers.metadata;

import java.util.HashMap;
import org.springframework.stereotype.Component;
import com.myApp.poc.accounts.service.dao.Account;

@Component
public class SavingsMetadataResolver implements MetadataResolver {

	@Override
	public HashMap<String, Object> getMetadata(Account source) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("interestRate", source.getInterest());
		return data;
	}
}
