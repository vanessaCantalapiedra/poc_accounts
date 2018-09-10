[![Build Status](https://travis-ci.org/vanessaCantalapiedra/poc_accounts.svg?branch=master)](https://travis-ci.org/vanessaCantalapiedra/poc_accounts)

POC - ACCOUNT MANAGER ( REST API )
===========================================

## Description

POC for Microservice for manage the accounts of our clients. The purpose is to play around with JPA.
Currently we only have 2 types of accounts, savings and checkin. Each one has its allowed transactions, and it is supposed that this operations
can be changed along the time; so for that reason, this operations can be set in the properties file.

The allowed Transactions are the following:
 - DEPOSIT
 - WITHDRAWAL:
   - All accounts could have a limit. If this limit does not need to be taken into account , the account is saved with this field to 
  null
 - PAY INTEREST
 - CASH TRANSFERS
   - If the id of the destiny account is equal to the source account, the transaction can not be executed.
   - For the concurrency problems that could happen, the rule that the microservice follows is the Optimistic Lock, that is, if after
    the transaction, there is a collision with the account versions, meaning that the data is inconsistent (another transaction has         modified it), the whole transaction is rolled-back, so the user can choose what he want to do, because there is a problem of       
    inconsistency in the data of the current account.

## Requirements

 - Java 1.8
 - Lombok
 - Swagger
 - Gradle
 
## Usage 

#### Running the Service

```sh
./gradlew bootRun local # use 'gradle bootRun local' on Windows
```

#### Distributing the Service

```sh
./gradlew build # use 'gradlew.bat build' on Windows
```
generates the binary `./build/libs/poc-accounts-microservice-0.0.1-SNAPSHOT`

#### Testing the Service

##### Java
Some Java unit-tests and integration tests are included. To execute them, run:  
```sh
./gradlew test # use 'gradlew.bat test' on windows
```
#### CI
The project has been configured to perform Continuous Integration using Travis CI. In the top of this document the status of the build can be checked, also it has the direct link to the travis build page.

#### TESTING
To test/try the service a postman collection has been committed with the source. It can be imported in postman.
Also once the service is running, a swagger-ui is also available at this url:
```
https://localhost:10101/swagger-ui.html
```

#### DEVELOPMENT COMMENTS
Depending on the performance , if this one is bad due to the number of concurrent requests, can try 2 different approachs:

1 - increase the number of allowed threads in the tomcat threadpool ( one thread is used for each request).
2 - re-implement the service and do it some factoring:
	- we can try to identify the methods that consume most of the time and make their calls in an async way.
	- we can re-design the whole service and make it non-blocking rest service, each call in the controller can be done in an async way,
	using callbacks and deferred results.
	
In order to improve the responsiveness of our services , an extra layer can be introduced, the cache , managed by Hazelcast or similar
products. So the data, once loaded from our database,  would be in a in memory-grid, much faster. Also the second layer of JPA cache
can be managed by Hazelcast.

For the JPA lockin strategy I´ve choosen OPTIMISTIC LOCK, because the locking mechanism is a way to ensure that all changes are taken into consideration when changing a record in the database.
So, if it  fails the same person who run the request, should review the new version of the record and make a decision.
This approach could be enhance by controlling also the ETag , so unnecessary exceptions caused by the optimistic lock can be avoided. 
The version is checked in the Etag, so you can avoid to begin the transaction.

#### NICE TO HAVE
Nice to have but not specified in the challenge:
 - HATEOAS support
 - aunthentication with jwt token for example
 - Etag checking to enhance the optimistic lock approach
 - delete / create / update accounts
 - New resource named Transaction, to track history of performed operations :delete / list all transaction, and transactions by transactionId, to complete the API for Transaction resource
 - improvements in the testing.
 
