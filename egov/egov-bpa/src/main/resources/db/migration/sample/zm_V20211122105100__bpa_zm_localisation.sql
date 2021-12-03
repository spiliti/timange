--- egov-bpa

--sort out Gandhi Rd in Boundary
 update eg_boundary set name = 'Libala Road' where name='Gandhi Road';

-----------------state.eg_user-----------------------------
 update state.eg_user set locale = 'en_ZM';
 update state.eg_address set country = 'Zambia', streetroadline = 'Mazuba Rd', citytownvillage = 'Mapepe', arealocalitysector = 'Mapepe', district ='Mapepe', state = 'Lusaka';


-- Master schemes
UPDATE EGBPA_MSTR_SCHEME SET description = 'Water Scheme for Ward No 1' WHERE code = 1;
UPDATE EGBPA_MSTR_SCHEME SET description = 'Sanitation Scheme for Ward No 2' WHERE code = 2;


update egbpa_mstr_scheme set description ='Master Plan for Mapepe Area 2035' where code ='18';

UPDATE egeis_assignment SET todate = '2023-12-31';

--- default configuration -----Tenant list

--insert into state.eg_tenant (id, name, code, createdby, createddate, lastmodifieddate, lastmodifiedby, url, version) select nextval('seq_eg_tenant'), 'Mapepe', 'generic', 1, now(), now(), 1, 'http://mapepe.egovzam.org:8481/', 0 where not exists (select id from state.eg_tenant where code='generic');
--update state.eg_tenant set url='http://mapepe.egovzam.org:8481/' where code='generic';
