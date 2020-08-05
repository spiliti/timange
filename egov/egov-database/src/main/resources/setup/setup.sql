SET search_path TO :schema;
INSERT INTO eg_city (domainurl, name, localname, id, active, version, createdby, lastmodifiedby, createddate, lastmodifieddate, code, districtcode, districtname, longitude, latitude, preferences, regionname, grade) VALUES (:cityurl, :cityname, :citylocalname, nextval('seq_eg_city'), true, 0, 1, 1, '2010-01-01 00:00:00', '2015-01-01 00:00:00', :citycode, :citycode, :localname, NULL, NULL, NULL, NULL, :gradecode); 
-----------------------START--------------------
INSERT INTO eg_citypreferences(id, municipalitylogo, createdby, createddate, lastmodifiedby, lastmodifieddate, version, municipalityname) values (nextval('seq_eg_citypreferences'), :citylogo, 1,now(),1,now(),0, :cityname);
update eg_city set preferences =(select id from eg_citypreferences, eg_city where eg_citypreferences.municipalityname = eg_city.name );
update eg_citypreferences set googleApiKey='AIzaSyA1otT5_xEGe0qMrh2lemKKYH7Vo-pGOlA';
