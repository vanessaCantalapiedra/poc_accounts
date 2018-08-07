# poc_accounts

[![Build Status](https://travis-ci.org/vanessaCantalapiedra/shopretailer_v2.0.svg?branch=master)](https://travis-ci.org/vanessaCantalapiedra/shopretailer_v2.0)

ACCOUNT MANAGER  - REST API 
====================================

## Description

Microservice for manage the accounts of our clients.
Currently we only have 2 types of accounts, savings and checkin. Each one has its allowed transactions, and i have supposed that this operations
can be changed along the time; so for that reason, this operations can be set in the properties file.

The allowed Transactions are the following:
 - DEPOSIT
  
 - WITHDRAWAL
 - PAY INTEREST
 - CASH TRANSFERS
    * For concurrent inserts, all the shops must be inserted/modified.
 - Modify an existing Shop: when a user POST an existing shop, the shop is updated. The old version of the shop is returned.
 - Get all Shops
 - Given latitude and longitude information in the url by a client , locate the shop closest to him and returning the address info of the shop.

## Requirements

 - Java 1.8
 - Lombok
 - Gradle
 
## Usage 

#### Running the Service

```sh
./gradlew bootRun # use 'gradlew.bat bootRun' on Windows
```

#### Distributing the Service

```sh
./gradlew build # use 'gradlew.bat build' on Windows
```
generates the binary `./build/libs/shopretailer-rest-service-2.1.0.jar`

#### Testing the Service

##### Java
Some Java unit-tests and integration tests are included. To execute them, run:  
```sh
./gradlew test # use 'gradlew.bat test' on windows
```
#### CI
The project has been configured to perform Continuous Integration using Travis CI. In the top of this document the status of the build can be checked, also it has the direct link to the travis build page.
