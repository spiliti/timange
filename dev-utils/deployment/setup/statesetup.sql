INSERT INTO state.eg_city (domainurl, name, localname, id, active, version, createdby, lastmodifiedby, createddate, lastmodifieddate, code, districtcode, districtname, longitude, latitude, preferences, regionname, grade) VALUES (:cityurl, :cityname, :citylocalname, nextval('seq_eg_city'), true, 0, 1, 1, '2010-01-01 00:00:00', '2015-01-01 00:00:00', :citycode, :citycode, :citylocalname, NULL, NULL, NULL, NULL, concat('0',:citycode)); 
