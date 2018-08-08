package com.myApp.poc.accounts.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

	@JsonProperty()
	private Integer accountDst;

	@NotNull
	@JsonProperty()
	private BigDecimal amount;

	@JsonIgnore
	private Integer accountSrcId;
}
