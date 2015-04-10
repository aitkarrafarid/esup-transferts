-- Executer ce script de migration uniquement si vous souhaitez passer de la version v2.1.0 vers la version 2.1.1 de l'application esup-transferts

SET DEFINE OFF; 

Insert into ACCUEIL_ANNEE
   (ID_ACCUEIL_ANNEE, LIBELLE)
 Values
   (13, '2014/2015');   

update wspub set mail_technique='a.definir@monmail.fr';   
   
COMMIT;
