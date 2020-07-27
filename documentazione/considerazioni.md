# Considerazioni finali
In questa sezione vengono riportate le considerazioni sui vari elementi del progetto in seguito al loro utilizzo con un focus soprattutto verso le problematiche riscontrate.

## Kafka Connector
Uno degli elementi più problematici è stato sicuramente il corretto setup ed utilizzo dei connettori utilizzati. 

Una prima problematica è stata in generale la scarsa documentazione riscontrata. I connettori ad hoc per i vari sistemi sono scritti e pubblicati da entità diverse che curano in maniera differente (a seconda anche degli interessi che hanno) la relativa documentazione. Se per il connettore verso MongoDB non ho riscontrato particolari problematiche, non si può dire lo stesso per quello da MQTT. In una prima istanza si è provato ad utilizzare ilo connettore scritto da confluent senza troppa fortuna. Dopo vari tentativi si è deciso di cambiare connettore che ha dato risultati migliori a fronte anche di una migliore documentazione.

Un'altra considerazione da fare sui connettori è che non consentono grandi elaborazioni del dato in ingresso. Infatti il grado di controllo che si ha, è di fatto relegato ad una serie di configurazioni che fornisce chi ha scritto il connettore stesso. Se si vuole un controllo più granulare, è necessario applicarlo in altro modo. Da qui la necessità di aggiungere un servizio Kafka Streams nella proxy architecture per elaborare il dato sul topic MQTT.

## Kafka MQTT Proxy
Anche questo servizio scritto da Confluent (disponibile su licenza) ha dato qualche problema. Come per il connettore (sempre di Confluent) la documentazione non eccelle. Inoltre il servizio andava sistematicamente in out-of-memory nell'ambiente di sviluppo su OSX non dando però nessun problema in ambiente Linux.

## Kafka Streams
Come detto precedentemente, è stato aggiunto un servizio Kafka Streams nella proxy architecture per poter manipolare i dati in ingresso. L'utilizzo di kafka Streams è invece molto agevole, ergonomico e potente. Con poche righe di codice si può mettere in piedi un servizio di manipolazione dati semplice e scalabile. Infatti basta istanziare più servizi dello stesso tipo per farli partecipare in automatico allo stesso consumer group e fargli così spartire le partizioni del topic. L'intero concetto di partizione non è stato affrontato in questo progetto avendo nell'istallazione locale tutto a uno come grado di ridondanza (broker, partizioni, nodi, ecc...), ma diventa fondamentale in una installazione reale.

## Istallazione reale 
Occorre fare alcune considerazioni anche per quanto riguarda una installazione simile su un ambiente reale (ad esempio su un cloud). Fare il deploy di tutti gli elementi presenti nelle tre architetture non è un lavoro banale. Istanziare direttamente il compose è fattibile, ma farebbe decadere completamente il senso di avere una architettura distribuita, ad iniziare da Kafka. Spesso i servizi cloud offrono questi servizi in maniera gestita, quindi scaricando l'utente dalla parte di istallazione, gestione, affidabilità dei dati, ecc. Questo ovviamente a fronte di un costo generalmente abbastanza alto comparato con un servizio analogo ma gestito dall'utente. Ad oggi non per niente il ruole di DevOps è sempre più richiesto (e pagato) proprio per far fronte ai problemi che comporta gestire architetture del genere.