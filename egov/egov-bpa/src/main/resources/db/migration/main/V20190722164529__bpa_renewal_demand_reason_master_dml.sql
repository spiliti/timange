INSERT INTO EG_DEMAND_REASON_MASTER ( ID, REASONMASTER, "category", ISDEBIT, module, CODE, "order", create_date, modified_date,isdemand) VALUES(nextval('seq_eg_demand_reason_master'), 'Permit Extension Fee', (select id from eg_reason_category where code='Fee'), 'N', (select id from eg_module where name='BPA' and parentmodule is null), 'PEF', 2, current_timestamp, current_timestamp,'t');

INSERT into EG_DEMAND_REASON (ID,ID_DEMAND_REASON_MASTER,ID_INSTALLMENT,PERCENTAGE_BASIS,ID_BASE_REASON,create_date,modified_date,GLCODEID) (select (nextval('seq_eg_demand_reason')), (select id from eg_demand_reason_master where code='PEF' and module=(select id from eg_module where name='BPA' and parentmodule is null)), (select inst.id from eg_installment_master inst where inst.id_module=(select id from eg_module where name='BPA' and parentmodule is null) and inst.installment_year='2019-04-01 00:00:00'), null, null, current_timestamp, current_timestamp, (select ID from CHARTOFACCOUNTS where GLCODE = '1401202'));

INSERT INTO EG_DEMAND_REASON_MASTER ( ID, REASONMASTER, "category", ISDEBIT, module, CODE, "order", create_date, modified_date,isdemand) VALUES(nextval('seq_eg_demand_reason_master'), 'Permit Renewal Fee', (select id from eg_reason_category where code='Fee'), 'N', (select id from eg_module where name='BPA' and parentmodule is null), 'PRF', 2, current_timestamp, current_timestamp,'t');

INSERT into EG_DEMAND_REASON (ID,ID_DEMAND_REASON_MASTER,ID_INSTALLMENT,PERCENTAGE_BASIS,ID_BASE_REASON,create_date,modified_date,GLCODEID) (select (nextval('seq_eg_demand_reason')), (select id from eg_demand_reason_master where code='PRF' and module=(select id from eg_module where name='BPA' and parentmodule is null)), (select inst.id from eg_installment_master inst where inst.id_module=(select id from eg_module where name='BPA' and parentmodule is null) and inst.installment_year='2019-04-01 00:00:00'), null, null, current_timestamp, current_timestamp, (select ID from CHARTOFACCOUNTS where GLCODE = '1401202'));