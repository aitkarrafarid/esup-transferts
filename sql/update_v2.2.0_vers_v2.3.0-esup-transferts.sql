-- Executer ce script de migration uniquement si vous souhaitez passer de la version v2.2.0 vers la version 2.3.0 de l'application esup-transferts

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

Insert into PARAMETRES
   (CODE, ETAT, COMMENTAIRE)
 Values
   ('planning_fermetures', 0, 'Choix de l''affichage et gestion manuelle ou automatique des périodes de fermeture');

Insert into VERSIONS (NUMERO, COMMENTAIRE, ETAT)
 Values
   ('2.3.0', '', 1);

COMMIT;