package com.myApp.poc.accounts;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.myApp.poc.accounts.service.AccountsService;
import com.myApp.poc.accounts.service.model.AccountDecorator;
import com.myApp.poc.accounts.service.model.AccountQueryFilter;
import com.myApp.poc.accounts.service.model.TransactionRequest;
import com.myApp.poc.accounts.service.model.TransactionType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "${service.context}/accounts", produces = APPLICATION_JSON_VALUE)
public class AccountsController {

	private final AccountsService accountsService;

	@ApiOperation(value = "Retrieves all available accounts.")
	@ApiResponses({
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Returns data for  " + " all investment accounts"),
			@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Whenever an error happen in the server") })
	@CrossOrigin
	@RequestMapping(value = "", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountDecorator>> getAccounts(@RequestParam("type") Optional<String> accountType) {
		return ResponseEntity.status(HttpStatus.OK).body(
				accountsService.getAccountsDecorator(AccountQueryFilter.builder().accountType(accountType).build()));
	}

	@ApiOperation(value = "Retrieves data for a given account.")
	@ApiResponses({
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Returns data for a "
					+ " given investment account"),
			@ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Whenever the accountId doesn't "
					+ "exist or doesn't belong to a real account."),
			@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Whenever an error happen in the server") })
	@CrossOrigin
	@RequestMapping(value = "/{accountId}", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountDecorator> getAccount(
			@ApiParam(value = "account id", required = true) @PathVariable("accountId") final Integer accountId) {

		return ResponseEntity.status(HttpStatus.OK).body(accountsService.getAccountDecorator(accountId));
	}

	@ApiOperation(value = "Performs an operation/transaction on the specific account.")
	@ApiResponses({
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Returns the data of the newly updated account"),
			@ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Whenever the accountId doesn't "
					+ "exist or doesn't belong to a real account."),
			@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Whenever an error happen in the server") })
	@CrossOrigin
	@RequestMapping(value = "/{accountId}", method = PUT, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AccountDecorator>> performTransaction(
			@ApiParam(value = "account id", required = true) @PathVariable("accountId") final Integer accountId,
			@ApiParam(value = "type of transaction", required = true) @RequestParam("transaction") TransactionType transactionType,
			@Valid @NotNull @RequestBody TransactionRequest request) {

		// fill request
		request.setAccountSrcId(accountId);
		return ResponseEntity.status(HttpStatus.OK).body(accountsService.performTransaction(transactionType, request));
	}

	// TODO DELETE OPERATION TO COMPLETE CRUD service
}
