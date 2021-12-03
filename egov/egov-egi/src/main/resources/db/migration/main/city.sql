---city preferences

update eg_citypreferences set municipalityname = "Mapepe City Council" where municipalitynam = 'Ranchi Municipal Corporation');
-- update eg_city set preferences  =(select id from eg_citypreferences where municipalityname= 'Ranchi Municipal Corporation');

update eg_city set districtname='Mapepe', districtcode='001', grade='Corp' where code='0001';
