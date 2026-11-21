# API ARCADE Milano

API REST in Spring Boot. Il contratto si trova in `openapi.yaml`.

## Prerequisiti
- JDK 25
- Maven non serve: si usa il wrapper incluso (`mvnw`)

## Configurazione
La connessione al database si legge da variabili d'ambiente. Nel repository non ci sono credenziali.

| Variabile | Significato | Valore predefinito |
|---|---|---|
| `DB_URL` | indirizzo JDBC del database | `jdbc:postgresql://localhost:5432/arcade` |
| `DB_USER` | utente del database | `arcade` |
| `DB_PASSWORD` | password | vuota |

Per usare il database vero, impostate le tre variabili nel vostro terminale prima di avviare.

## Comandi (da questa cartella)

Windows:
```
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Linux e macOS:
```
./mvnw test
./mvnw spring-boot:run
```

L'applicazione parte sulla porta 8080.