package com.myApp.poc.accounts.service.resolvers.metadata;

import java.util.Collections;
import java.util.HashMap;
import org.springframework.stereotype.Component;
import com.myApp.poc.accounts.service.dao.Account;

@Component
public class DefaultMetadataResolver implements MetadataResolver {

	@Override
	public HashMap<String, Object> getMetadata(Account source) {
		return (HashMap<String, Object>) Collections.EMPTY_MAP;
	}
}
