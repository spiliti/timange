--- from egov-collection
Insert into egcl_servicecategory (id,name,code,isactive,version,createdby,createddate,lastmodifiedby,lastmodifieddate) 
values (nextval('SEQ_EG_MODULE'),'Axis Payment Gateway','AXIS',true,0,1,to_timestamp('2015-09-15 10:44:20.062408','null'),1,to_timestamp('2015-09-15 10:44:20.062408','null'));

Insert into egcl_servicedetails (id,name,serviceurl,isenabled,callbackurl,servicetype,code,fund,fundsource,functionary,vouchercreation,scheme,subscheme,servicecategory,isvoucherapproved,vouchercutoffdate,created_by,created_date,modified_by,modified_date,ordernumber) 
values (nextval('seq_egcl_servicedetails'),'Axis Payment Gateway','https://migs.mastercard.com.au/vpcpay',true,'https://dev4.governation.com/collection/citizen/onlineReceipt-acceptMessageFromPaymentGateway.action','P','AXIS',1,null,null,true,null,null,(select id from egcl_servicecategory where code='AXIS'),true,to_timestamp('0001-07-11 00:00:00.0','null'),1,to_timestamp('2015-09-15 10:44:20.062408','null'),1,to_timestamp('2015-09-15 10:44:20.062408','null'),null);


INSERT INTO egcl_servicecategory(id, name, code, isactive, version, createdby, createddate, lastmodifiedby, lastmodifieddate)
VALUES(nextval('seq_egcl_servicecategory'), 'SBIMOPS Payment Gateway', 'SBIMOPS', false, 0, 1, current_timestamp, 1, current_timestamp);

Insert into egcl_servicedetails (ID,NAME,SERVICEURL,ISENABLED,CALLBACKURL,SERVICETYPE,CODE,FUND,FUNDSOURCE,FUNCTIONARY,
VOUCHERCREATION,SCHEME,SUBSCHEME,SERVICECATEGORY,ISVOUCHERAPPROVED,VOUCHERCUTOFFDATE,CREATED_BY,created_date,
modified_by,modified_date) values 
(nextval('seq_egcl_servicedetails'),'SBI Bank Payment Gateway','https://treasury.ap.gov.in/cybertry/deptrequest_mops.php',true,null,
'P','SBIMOPS',null,null,null,true,null,null,(select id from egcl_servicecategory where code='SBIMOPS'),true,null,1,current_timestamp,1,current_timestamp);
