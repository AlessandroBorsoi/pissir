# MQTT Proxy Service

In questa sezione viene descritta l'implementazione del servizio, contenuto nel modulo `mqtt-proxy-service`, che si occupa di convertire i dati depositati su un topic dal Kafka MQTT Proxy.

Nella radice del modulo sono contenuti il `Dockerfile` necessario al plugin gradle (`com.palantir.docker`) per poter creare una immagine docker del servizio, e il file di build di gradle. L'altro plugin importato (`com.github.johnrengelman.shadow`) serve per generare comodamente l'uber-jar eseguibile.

Le dipendenze, a parte l'apposita libreria di Kafka Streams, sono quelle necessarie a lavorare con Avro, a maneggiare json, al logging e alla gestione delle configurazioni. Si nota che il modulo `avro` è una dipendenza avendo necessità di produrre i dati nella struttura definita in quel modulo.

Il servizio è molto semplice ed è quasi interamente contenuto nella classe `MqttProxyService`, al netto di una classe di utilità e a due classi POJO. Per lavorare con Kafka Streams occore fondamentalmente definire un oggetto `KafkaStreams` a cui vengono passate le proprietà di configurazione e la topologia della trasformazione da effettuare. Una volta creato, è sufficiente eseguirne lo start.

Per definire la topologia si utilizza un builder. In questo caso la trasformazione è abbastanza semplice e viene riportata qui per semplicità:

```
StreamsBuilder builder = new StreamsBuilder();
builder
        .stream(appConfig.getSourceTopicName(), Consumed.with(stringSerde, stringSerde))
        .mapValues(parseMessage())
        .filter((k, v) -> Objects.nonNull(v))
        .mapValues(OpenPflowConverter::from)
        .filter((k, v) -> v.isPresent())
        .mapValues(Optional::get)
        .map((k, v) -> KeyValue.pair(null, v))
        .to(appConfig.getDestinationTopicName(), Produced.with(null, openPflowSpecificAvroSerde));
``` 

Si inizia definendo lo stream sorgente (in questo caso è il topic MQTT) e il formato che ci si aspetta dal topic sia per la chiave che per il valore (stringhe in entrambi i casi). La funzione privata `parseMessage()` legge la stringa e la converte nell'oggetto `it.uniupo.disit.pissir.mqtt.proxy.service.OpenPflowRaw` aspettandosi che il messaggio sia il json descritto in [questa sezione](dati.md). Si scartano eventuali valori nulli, si esegue la conversione nell'oggetto `it.uniupo.disit.pissir.avro.OpenPflow` definito nel modulo `avro` (il codice Java viene automaticamente generato dal descrittore). Dopo aver scartato elementi non validi, si elimina la chiave del topic sorgente (che è automaticamente inserita dal proxy) e si scrive il messaggio nel topic di destinazione in formato Avro. La chiave si elimina perchè non rilevante e per lasciare che sia MongoDB a generarne automaticamente una in fase di inserimento. Si noti che il serde (serializzatore/deserializzatore) Avro necessita di essere configurato con l'indirizzo dello schema registry per poterne pubblicare lo schema.
