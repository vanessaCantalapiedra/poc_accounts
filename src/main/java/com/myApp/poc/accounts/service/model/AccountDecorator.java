package com.myApp.poc.accounts.service.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.function.Supplier;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myApp.poc.accounts.service.dao.Account;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDecorator {

	@JsonIgnore
	private Account source;

	@ApiModelProperty(value = "Identifier of the account", position = 1)
	@JsonProperty(index = 1)
	public Integer getAccountId() {
		return source.getAccountId();
	}

	@ApiModelProperty(value = "Type of the account", position = 2)
	@JsonProperty
	public String getAccountType() {
		return source.getAccountType();
	}

	@ApiModelProperty(value = "Available amount in the account", position = 3)
	@JsonProperty
	private BigDecimal getAmount() {
		return source.getAmount().setScale(2).stripTrailingZeros();
	}

	@JsonIgnore
	private Supplier<HashMap<String, Object>> metadataResolver;

	@JsonAnyGetter
	@JsonInclude(Include.NON_NULL)
	private HashMap<String, Object> getMetadata() {
		return metadataResolver.get();
	}
}
