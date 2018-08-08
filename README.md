[![Build Status](https://travis-ci.org/vanessaCantalapiedra/shopretailer_v2.0.svg?branch=master)](https://travis-ci.org/vanessaCantalapiedra/shopretailer_v2.0)

ACCOUNT MANAGER  - REST API 
====================================

## Description

Microservice for manage the accounts of our clients.
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
