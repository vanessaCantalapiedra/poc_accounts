package com.myApp.poc.accounts.service.resolvers.metadata;

import java.util.HashMap;
import com.myApp.poc.accounts.service.dao.Account;

public interface MetadataResolver {

	HashMap<String, Object> getMetadata(Account source);
}
