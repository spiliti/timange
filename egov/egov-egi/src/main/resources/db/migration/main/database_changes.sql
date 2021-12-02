
---database_changes
-- boundaries and heirachies
-- user changes 
-- 
ALTER TABLE eg_user DROP COLUMN aadhaarnumber;
ALTER TABLE eg_user ADD COLUMN firsName character varying(40);
ALTER TABLE eg_user ADD COLUMN middleName character varying(20);
ALTER TABLE eg_user ADD COLUMN surname character varying(40);
ALTER TABLE eg_user ADD COLUMN nrcnumber character varying(11);
# split name into three
# delete from eg_action where name = 'AadhaarInfo',  or url ='/aadhaar', or omit from menu
# 
# -----------------START superadmin-------------------
INSERT INTO eg_user (id, title, salutation, dob, locale, username, password, pwdexpirydate, mobilenumber, altcontactnumber, emailid, createddate, lastmodifieddate, createdby, lastmodifiedby, active, name, gender, pan, aadhaarnumber, type, version, guardian, guardianrelation) VALUES (1, NULL, NULL, NULL, 'en_IN', 'superadmin', '$2a$10$uheIOutTnD33x7CDqac1zOL8DMiuz7mWplToPgcf7oxAI9OzRKxmK', '2020-12-31 00:00:00', NULL, NULL, NULL, '2010-01-01 00:00:00', '2015-01-01 00:00:00', 1, 1, true, 'Admin', NULL, NULL, NULL, 'EMPLOYEE', 0, NULL, NULL);
# ------------------END---------------------

#-----------------START--------------------
INSERT into eg_userrole values((select id from eg_role  where name  = 'Super User'),(select id from eg_user where username ='superadmin'));

#-----------------START--------------------

Insert into EG_ROLEACTION (roleid, actionid) values ((select id from eg_role where name='Super User'),1049),((select id from eg_role where name='Super User'),33),((select id from eg_role where name='Super User'),46),((select id from eg_role where name='Super User'),54),((select id from eg_role where name='Super User'),55),((select id from eg_role where name='Super User'),34),((select id from eg_role where name='Super User'),35),((select id from eg_role where name='Super User'),39),((select id from eg_role where name='Super User'),30),((select id from eg_role where name='Super User'),32),((select id from eg_role where name='Super User'),31),((select id from eg_role where name='Super User'),44),((select id from eg_role where name='Super User'),69),((select id from eg_role where name='Super User'),1015),((select id from eg_role where name='Super User'),1036),((select id from eg_role where name='Super User'),1014),((select id from eg_role where name='Super User'),1016),((select id from eg_role where name='Super User'),1035),((select id from eg_role where name='Super User'),41),((select id from eg_role where name='Super User'),42),((select id from eg_role where name='Super User'),43),((select id from eg_role where name='Super User'),62),((select id from eg_role where name='Super User'),63),((select id from eg_role where name='Super User'),64),((select id from eg_role where name='Super User'),65),((select id from eg_role where name='Super User'),40),((select id from eg_role where name='Super User'),47),((select id from eg_role where name='Super User'),50),((select id from eg_role where name='Super User'),25),((select id from eg_role where name='Super User'),26),((select id from eg_role where name='Super User'),27),((select id from eg_role where name='Super User'),70),((select id from eg_role where name='Super User'),71),((select id from eg_role where name='Super User'),72),((select id from eg_role where name='Super User'),73),((select id from eg_role where name='Super User'),1041),((select id from eg_role where name='Super User'),1044),((select id from eg_role where name='Super User'),66),((select id from eg_role where name='Super User'),67),((select id from eg_role where name='Super User'),68),((select id from eg_role where name='Super User'),59),((select id from eg_role where name='Super User'),58),((select id from eg_role where name='Super User'),1181),((select id from eg_role where name='Super User'),1182),((select id from eg_role where name='Super User'),1192),((select id from eg_role where name='Super User'),1224),((select id from eg_role where name='Super User'),37),((select id from eg_role where name='Super User'),56);

UPDATE EG_USER SET "type" = 'SYSTEM' WHERE username='superadmin';

INSERT INTO EG_SYSTEMUSER VALUES ((select id from eg_user where username='superadmin')); 

delete from eg_userrole where userid = (select id from eg_user where username='superadmin');
insert into eg_userrole(userid,roleid) values ((select id from eg_user where username='superadmin'),(select id from eg_role where name='SYSTEM'));

	-------------------inser into state.eg_user----------------
INSERT INTO state.eg_user (id, title, salutation, dob, locale, username, password, pwdexpirydate, mobilenumber, altcontactnumber, emailid, createddate, lastmodifieddate, createdby, lastmodifiedby, active, name, gender, pan, aadhaarnumber, type, version, guardian, guardianrelation,tenantId) 
select 1, NULL, NULL, NULL, 'en_IN', 'superadmin', '$2a$10$uheIOutTnD33x7CDqac1zOL8DMiuz7mWplToPgcf7oxAI9OzRKxmK', '2020-12-31 00:00:00', NULL, NULL, NULL, '2010-01-01 00:00:00', '2015-01-01 00:00:00', 1, 1, true, 'Admin', NULL, NULL, NULL, 'SYSTEM', 0, NULL, NULL,'state'  where not exists(SELECT * FROM state.eg_user WHERE username='egovernments');

INSERT into state.eg_userrole select (select id from eg_role  where name  = 'SYSTEM'),(select id from state.eg_user where username ='superadmin') where not exists(SELECT * FROM state.eg_userrole WHERE roleid in (select id from eg_role  where name  = 'SYSTEM'));