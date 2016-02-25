-- Executer ce script de migration uniquement si vous souhaitez passer de la version v2.2.0 vers la version 2.3.0 de l'application esup-transferts

SET DEFINE OFF; 

DROP SEQUENCE AVIS_SEQ; 

DROP SEQUENCE DECISION_SEQ;

DROP SEQUENCE OPI_SEQ;


alter table 
   OFFRE_FORMATIONS
modify 
( 
   ACTIF    integer
);


alter table 
   IND_OPI
modify 
( 
   SYNCHRO    integer
);

COMMIT;