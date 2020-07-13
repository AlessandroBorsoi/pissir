# Descrizione del sistema da realizzare

Lo scopo di questo progetto è quello di realizzare un sistema che sia in grado di raccogliere (potenzialmente molti) dati da uno o più sistemi esterni, processarli velocemente e renderli disponibili per un'analisi dettagliata con tecniche opportune, raccogliendoli in un data store adeguato. In particolare si utilizzeranno come input i dati forniti dal dataset open sugli spostamenti urbani di Tokyo [OpenPFLOW](https://github.com/sekilab/OpenPFLOW), che verranno dapprima riversati su [Kafka](https://kafka.apache.org/) attraverso una interfaccia MQTT e poi riorganizzati all'interno di [MongoDB](https://www.mongodb.com/).

# Architettura

Lo scopo di questo progetto consiste nel realizzare un sistema in cui i dati possano essere inviati attraverso una interfaccia MQTT e che siano opportunamente depositati all'interno di un database MongoDB. Per fare questo sono state realizzate tre differenti architetture che, sempre avendo Kafka come elemento principale, sfruttino diverse tecnologie per analizzarne i vantaggi e gli svantaggi. Inoltre è da notare come gli elementi e le tecnologie utilizzate nelle varie soluzioni siano potenzialmente combinabili tra di loro. Le tre architetture sono illustrate nelle sezioni seguenti.

## Architettura con broker MQTT e servizio di ingestione dei dati custom (service architecture)
In questa prima architettura si utilizza un broker MQTT come interfaccia verso l'esterno. I dati che arrivano su un topic vengono letti da un servizio custom ed inviati a Kafka per mezzo di un [producer](https://docs.confluent.io/current/clients/producer.html). Il servizio in questo scenario si occupa anche di rimappare il messaggio letto dal topic MQTT sia in struttura che in formato per renderlo subito pronto ad assere depositato su MongoDB. Per far questo si usa l'apposito (Sink) [Connector](https://www.mongodb.com/kafka-connector) che legge dal topic Kafka e scrive in una apposita collezione sul database.

![Service architecture](service-architecture.png)

## Architettura con broker MQTT e utilizzo di Kafka Connect per l'ingestione dei dati (connector architecture)
Questa architettura è molto simile alla prima con la differenza che, invece di usare un servizio custom come ponte tra il broker MQTT e Kafka, si usa un (Source) [Connector](https://docs.lenses.io/connectors/source/mqtt.html). Il dato in ingresso è ancora convertito in formato ma la struttura rimarrà inalterata. Come per la prima architettura, sarà un connettore ad occuparsi di trasferire i dati dal topic Kafka a MongoDB. 

![Connector architecture](connector-architecture.png)

## Architettura con Kafka MQTT Proxy per l'ingestione dei dati (proxy architecture)
In questa architettura non si utilizza più un vero e proprio broker MQTT, ma si utilizzerà invece il [Kafka MQTT Proxy](https://docs.confluent.io/current/kafka-mqtt/index.html) il quale consente di esporre una interfaccia MQTT usando direttamente Kafka come broker. Questo fa si che il dato "grezzo" sul topic, prima di arrivare al database, debba essere ristrutturato e convertito. Per fare questo viene aggiunto un semplice servizio [Kafka Streams](https://kafka.apache.org/documentation/streams/) che legge i dati dal topic "MQTT" e li riscrive convertiti in un altro topic. Di nuovo, un connettore leggerà i dati da quest'ultimo topic e li depositerà in MongoDB.

![Proxy architecture](proxy-architecture.png)

## Integration test

A supporto del progetto, sono stati creati dei test di integrazione end-to-end che, leggendo i dati da dei csv disponibili del dataset di riferimento, li immettono nel sistema attraverso l'interfaccia MQTT per poi verificare che siano stati depositati all'interno di MongoDB. Essendo i sistemi composti da molte parti, sono stati creati degli script che avviano automaticamente un [docker-compose](https://docs.docker.com/compose/) che comprende tutti i servizi necessari opportunamente configurati. 
