INSERT into EG_ROLEACTION(ROLEID,ACTIONID) select (select id from eg_role where name='Council Management Admin'), id from eg_action where name in ('Result-CouncilDesignation','Search and View Result-CouncilDesignation');