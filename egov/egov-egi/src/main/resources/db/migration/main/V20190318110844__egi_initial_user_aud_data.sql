insert into state.eg_user_aud (id,rev,name,mobilenumber,emailid,revtype) select egu.id,nextval('hibernate_sequence'),egu.name,egu.mobilenumber,egu.emailid,0 from state.eg_user egu where egu.id not in(select id from state.eg_user_aud);

update state.eg_user set usemultifa=false;
update state.eg_user_aud set usemultifa=false;