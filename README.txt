version 2.0.0 --> version 2.1.0
- Ajouter la propriété "validation.automatique" du fichier config-exemple.properties vdans le fichier config.properties
- Configurer le fichier ehcache.xml notemment le <diskStore path="/tmp/" />
- Executer le script SQL : update_v2.0_vers_v2.1-esup-transferts.sql dans le répertoire SQL 

- Changement de version 2.1.0 --> version 2.2.0
- Migration des composants primeFaces 4.0 --> 5.1
- Changement de version de JAVA 6 --> JAVA 7
- Changement du drivers JDBC Oracle pour la version 10G
- Changement du client JAR du WS Apogée pour la version "apo-webservicesclient460la.jar"