# Struttura del progetto

La struttura è impostata come un progetto multi modulo Gradle. I moduli sono i seguenti:
- avro
- mqtt-ingestion-service
- mqtt-proxy-service
- integration-tests

oltre alla directory `docker` e alla (autoesplicativa) `documentazione`.

## avro
In `avro` è contenuta la definizione dello schema [Avro](https://avro.apache.org/) usato da entrambi gli `mqtt-*-service`. È stato separato in questo modulo proprio per evitare duplicazioni e per poterlo importare come dipendenza da chi ne necessita. Lo schema è definito in [IDL](https://avro.apache.org/docs/current/idl.html) (contenuto in un file `.avdl`) da cui viene automaticamente generato tutto il codice Java necessario usando il [`gradle-avro-plugin`](https://github.com/davidmc24/gradle-avro-plugin).

## mqtt-ingestion-service
In questo modulo è contenuto il servizio custom di ingestione dei dati inseriti tramite broker MQTT. Il servizio utilizza la libreria [Eclipse Paho](https://www.eclipse.org/paho/clients/java/) per leggere i dati dal broker MQTT e un [producer](https://kafka.apache.org/documentation/#producerapi) per scriverli sul broker Kafka.

## mqtt-proxy-service
Questo modulo contiene il servizio, basato su [Kafka Streams](https://kafka.apache.org/documentation/streams/) che trasforma i dati ingestionati tramite [MQTT Proxy](https://docs.confluent.io/current/kafka-mqtt/index.html) nel formato definito dal modulo `avro`.

## integration-tests
In `integration-tests` sono contenuti i test end-to-end applicabili indifferentemente su tutte e tre le architetture realizzate in questo progetto.

## docker
La directory `docker` contiene tutti i file necessari per avviare in automatico le tre architetture usando `docker-compose`. Oltre ai tre `docker-compose.yml`, sono presenti anche i jar dei connettori usati e i file json necessari per configurarli.

# Compilazione ed esecuzione

Per automatizzare il processo di esecuzione delle tre architetture realizzate, sono stati creati altrettanti file di script bash eseguibili dalla radice del progetto con
```bash
sh start-<nome architettura>-arch.sh
```
ed è possibile terminarli indefferentemente eseguendo
```bash
sh stop.sh
```

Al loro interno, i file di start eseguono le seguenti operazioni:
- compilano, se necessario, il servizio associato a quella architettura;
- dopo aver eseguito la build, generano l'immagine docker del servizio stesso usando il `Dockerfile` presente nei singoli servizi;
- viene eseguito il comando `docker-compose up` sullo specifico descrittore `docker-compose.yml` nella cartella `docker`;
- si attende che il o i connettori siano running per eseguire una `curl` di configurazione con il file json appropriato.

A quel punto tutti i servizi necessari dovrebbero essere attivi ed è possibile eseguire i test.

Da notare che la prima volta che viene eseguito uno start, l'operazione potrebbe richiedere diversi minuti dovuti allo scaricamento sia delle dipendenze dei servizi che delle immagini docker necessarie.

## Test
I test possono essere eseguiti con il comando `gradle test` nel singolo modulo oppure tramite IDE. C'è però un test nel modulo `integration-tests` che richiede come configurazione tramite variabile d'ambiente una directory contenente uno o più csv presi dal dataset di riferimento. Per agevolare le procedure, questo test può essere eseguito tramite lo script `test.sh` avendo cura di configurare la giusta cartella.
