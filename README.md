# DietiDeals24

# About
DietiDeals24 è una piattaforma dedicata alla gestione di aste online. Questa soluzione è fornita attraverso un'applicazione mobile che offre agli utenti una gamma completa di funzionalità, compresa la creazione di aste all'inglese e al ribasso, consentendo loro di partecipare attivamente all'esperienza di shopping e di fare offerte.

# Features
- Creazione aste all'inglese
- Creazione aste al ribasso
- Fare offerte
- Personalizzare profilo
- Ricevere notifiche

# Build instructions
Per lanciare in esecuzione il Backend di DietiDeals24:
- Specificare una password per il database postgres all'interno di "compose.yaml".
- Riportare la stessa password in `Server/src/main/resources/application.properties`.
- Impostare una porta (di default è 9090) in `Server/src/main/resources/application.properties`:
```yaml
server.port=9090
```
- Impostare una secret key jwt e una expiration in `Server/src/main/resources/application.properties`:
```yaml
security.jwt.secret-key=<your_secret_key>
security.jwt.expiration-time=<your_expiration>
```
Alla fine di questi passaggi, eseguire il seguente comando nella root directory della repository.
```shell
docker compose up
```

Per buildare l'apk del client:
- Specificare l'indirizzo ip su cui viene eseguito il backend in `Client/app/src/main/java/it/unina/dietideals24/retrofit/RetrofitService.java` (usare `http://10.0.2.2:<port>/` se si vuole usare l'emulatore di android studio con il server in locale) e la porta impostata in precedenza.
- Se si vuole tracciare le statistiche di utilizzo, sostituire il google-services.json in `Client/app`.
