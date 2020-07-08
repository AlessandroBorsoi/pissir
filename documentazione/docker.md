# Docker compose

In questo progetto sono definiti tre file `docker-compose.yml` che includono tutti i servizi necessari per avere un ambiente in cui testare end-to-end le architetture. La maggior parte dei servizi sono comuni a tutti e tre i sistemi e sono i seguenti:

- `kafka`, singolo broker per questo ambiente di test;
- `zookeeper`, necessario a Kafka stesso per poter operare;
- `kafka-schema-registry`, servizio necessario per lavorare con il formato Avro in cui vengono registrati gli schema dei modelli usati. In questo modo è possibile inserire nel payload solo l'id dello schema usato, ed ogni servizi che legge o scrive può usare lo schema registry per recuperarne, appunto, lo schema;
- `kafka-connect`, è il servizio che gestisce i connettori. Nel caso dei connettori usati (Sink per MongoDB e Source per MQTT) è necessario includere anche i jar specifici (presenti nel repository in `docker/connectors`) e caricare le configurazioni necessarie (file json dentro la directory `docker`);
- `mongo-db`, database in cui riversare i dati;
- `kafka-rest-proxy`, servizio che espone una interfaccia REST sopra Kafka e necessario ai servizi di monitoring inclusi ed elencati di seguito;
- `schema-registry-ui`, servizio che espone una interfaccia web (disponibile in localhost sulla porta 8001) per monitorare gli schema Avro registrati e la loro struttura;
- `kafka-topics-ui`, servizio che espone una interfaccia web (disponibile in localhost sulla porta 8000) per monitorare i topic presenti su Kafka e i dati inseriti;
- `kafka-connect-ui`, servizio che espone una interfaccia web (disponibile in localhost sulla porta 8003) per monitorare lo stato e le configurazioni dei connettori abilitati.

Oltre a questi servizi, quelli specifici per le varie architetture sono:
- `mosquitto`, broker MQTT usato da due delle tre architetture;
- `mqtt-proxy`, servizio che sostituisce il broker MQTT usato solo nella proxy architecture;
- `kafka-setup`, servizio che implementa un workaround per creare i topic allo startup. Necessario nella proxy architecture in quanto il servizio Kafka Streams andrebbe in errore non trovando il topic da cui deve leggere all'avvio;
- `mqtt-ingestion-service`, servizio di ingestione custom (il cui codice si trova in questo repository), opportunamente dockerizzato nello script di startup;   
- `mqtt-proxy-service`, servizio Kafka Streams (il cui codice si trova in questo repository), opportunamente dockerizzato nello script di startup.
