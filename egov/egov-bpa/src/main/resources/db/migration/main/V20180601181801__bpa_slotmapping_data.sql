delete from egbpa_mstr_slotmapping where id =(select id from egbpa_mstr_slotmapping where applicationtype = 'ALL_OTHER_SERVICES' and zone = (select id from eg_boundary where name = 'ZONE-1 (MAIN OFFICE)') limit 1);