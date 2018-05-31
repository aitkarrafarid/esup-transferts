Update VERSIONS set ETAT=0 WHERE NUMERO = '2.4.0';

Insert into VERSIONS (NUMERO, COMMENTAIRE, ETAT)
 Values
   ('2.5.0', '', 1);

commit;