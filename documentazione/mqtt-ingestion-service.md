# MQTT Ingestion Service

In questa sezione viene descritta l'implementazione del servizio che fa da collegamento tra il broker MQTT e Kafka contenuto nel modulo `mqtt-ingestion-service`.

Nella radice del modulo sono contenuti il `Dockerfile` necessario al plugin gradle (`com.palantir.docker`) per poter creare una immagine docker del servizio, e il file di build di gradle. L'altro plugin importato serve per generare comodamente l'uber-jar eseguibile.

Le dipendenze importate sono quelle relative alla lettura dei dati sul broker MQTT (usando Eclipse Paho), alla deserializzazione del json (usando jackson) e alla scrittura dei dati su Kafka (usando `kafka-client`). Le altre dipendenze sono relative alla gestione del logging, delle configurazioni e al testing. Si noti che il modulo `avro` è una dipendenza avendo necessità di produrre i dati nella struttura definita in quel modulo.

Senza entrare troppo nel dettaglio del codice, di fatto sono stati creati due servizi (`MqttService` e `KafkaService`): il primo, che ha come dipendenza il secondo, una volta eseguito passa il `KafkaService` alla callback di ricezione messaggi il quale si occupa di rimapparli, convertirli e inviarli a Kafka. Viene anche usato un `CountDownLatch` per bloccare l'esecuzione del main thread, ed eventualmente uscire in caso di errore facendolo decrementare dalla callback.
