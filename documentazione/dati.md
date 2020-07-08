# Struttura dati

Come prima cosa si va ad analizzare il formato dei dati in ingresso. In questo progetto si è deciso di gestire i dati open sugli spostamenti dell'area urbana di Tokyo presenti in [questo](https://github.com/sekilab/OpenPFLOW) repository. Si può vedere che i dati, presenti sotto forma di grossi file csv, hanno la seguente struttura:
```
id : unique agent id (int)
time : YYYY/MM/DD HH:mm:ss (char[])
longitude : SRID-4326 (double)
latitude: SRID-4326 (double)
transport: STAY-99, WALK-1, VEHICLE-2, TRAIN-3, BICYCLE-4 (int)
magnification factor: (double)
```

Si è deciso che all'interno del topic MQTT la struttura sarà la medesima ma rappresentata mediante un json con il seguente schema:
```json
{
  "type": "object",
  "properties": {
    "id": {
      "type": "integer"
    },
    "time": {
      "type": "string"
    },
    "longitude": {
      "type": "number"
    },
    "latitude": {
      "type": "number"
    },
    "transport": {
      "type": "integer"
    },
    "magnification": {
      "type": "number"
    }
  },
  "required": [
    "id",
    "time",
    "longitude",
    "latitude",
    "transport",
    "magnification"
  ]
}
```
la cui struttura del campo `time` è quella definita nel repository di riferimento, ovvero `YYYY/MM/DD HH:mm:ss`. Un json d'esempio ha la seguente forma:
````json
{
  "id": 1660,
  "time": "2020/06/27 15:10:30",
  "longitude": 139.60923,
  "latitude": 35.641745,
  "transport": 99,
  "magnification": 55.2 
}
````

Questo messaggio viene gestito in tre modi diversi a seconda dell'architettura.

### Service architecture
Nel caso della service architecture, il servizio custom è in grado di leggere il dato come stringa, usare una libreria di conversione ([jackson](https://github.com/FasterXML/jackson) in questo caso) per convertirla in un oggetto Java che abbia la struttura del json, ed effettuare un ulteriore mapping prima di andare a scrivere il dato su Kafka. Il dato viene scritto in formato Avro e la struttura può essere consultata nel file di definizione in formato IDL [open-pflow.avdl](../avro/src/main/avro/open-pflow.avdl) presente nel modulo `avro`. Come si può vedere, il dato presente nel campo `time` viene convertito da stringa a timestamp (supponendo che il dato abbia il fuso orario del Giappone) e latitudine e longitudine vengono inseriti nell'array `coordinates` dell'oggetto `location` insieme al campo `type` (nel nostro caso sempre di valore `Point`). Il razionale di questa conversione è quello di preparare il dato per le [Geospatial queries](https://docs.mongodb.com/manual/geospatial-queries/) di MongoDB come descritto nella documentazione.

Il connettore verso MongoDB legge il dato in formato Avro e lo scrive così com'è in una collection. Non avendo il topic Kafka una chiava impostata, viene automaticamente generata una chiave (UUID) nella collection sul database.

### Connector architecture
In questo caso, per semplicità, non viene effettuato il mapping della service architecture, ma viene eseguita solo una conversione di formato: il messaggio è infatti salvato in Avro in Kafka e letto così com'è dal connettore verso MongoDB. Il formato Avro ha la seguente struttura all'interno di Kafka:
```json
{
  "type": "record",
  "name": "pflow",
  "fields": [
    {
      "name": "id",
      "type": "long"
    },
    {
      "name": "time",
      "type": "string"
    },
    {
      "name": "longitude",
      "type": "double"
    },
    {
      "name": "latitude",
      "type": "double"
    },
    {
      "name": "transport",
      "type": "long"
    },
    {
      "name": "magnification",
      "type": "double"
    }
  ],
  "connect.name": "pflow"
}
```

### Proxy architecture
Nel caso dell'architettura che utilizza il Kafka MQTT Proxy, il messaggio viene salvato come stringa direttamente su un topic Kafka (che si sostituisce al broker MQTT). Da quel topic, viene letto da una applicazione Kafka Streams che ne effettua il mapping e la conversione di formato per riscriverlo su un altro topic ed essere riversato nel database. Il formato di destinazione è lo stesso già descritto nella service architecture e definito [qui](../avro/src/main/avro/open-pflow.avdl).

