
DELETE from ACCOUNTS;

insert into accounts
values(0,'SAVINGS',1000,2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(),0,null);

insert into accounts
values(1,'CHECKING',1000,null,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP(),0,100);

insert into accounts
values(2
,'CHECKING',1000,null,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP(),0,100);