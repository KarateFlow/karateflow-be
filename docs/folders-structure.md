# Folders Structure

Ho ideato questo design delle cartelle del progetto per massimizzare la **testabilità** e la **scalabilità enterprise**, bilanciando la separazione delle responsabilità con la manutenibilità del codice. 

---

## Mappa della Struttura delle Cartelle (Package-by-Feature)

L'applicazione abbandona la classica suddivisione strutturale per layer (*Package-by-Layer*) a favore di una modularità verticale orientata alle funzionalità di business (*Package-by-Feature*). Di seguito viene mostrato l'albero del codice focalizzato su una delle feature centrali `testexecution`:

```text
com.example.app/
 │
 ├── athlete/                                    <-- Feature Modulo Atleti 
 ├── exercise/                                   <-- Feature Modulo Esercizi
 │
 ├── testexecution/                              <-- Feature Modulo Sessioni di Test (Core del Dominio)
 │    ├── controller/                            <-- Inbound Adapter (REST API)
 │    │    └── TestExecutionController.java
 │    │
 │    ├── dto/                                   <-- Contratti di Rete (Disaccoppiamento API)
 │    │    ├── request/
 │    │    │    └── RecordTestExecutionRequest.java
 │    │    └── response/
 │    │         └── TestExecutionResponse.java
 │    │
 │    ├── usecase/                               <-- Logica di Business pura (Single Responsibility)
 │    │    ├── RecordTestExecutionUseCase.java   
 │    │
 │    ├── domain/                                <-- Il Core dell'Applicazione (Isolato da Framework)
 │    │    ├── model/
 │    │    │    ├── TestExecution.java           <-- Entità di Dominio Pura (NO annotazioni DB)
 │    │    │    └── PerformedExercise.java       <-- Value Object (Dati storici dell'esercizio eseguito)
 │    │    ├── port/                             <-- Outbound Port
 │    │    │    └── TestExecutionRepository.java
 │    │    └── exception/
 │    │         └── AthleteNotFoundException.java
 │    │
 │    ├── persistence/                           <-- Outbound Adapter (Dettaglio Infrastrutturale)
 │    │    ├── document/
 │    │    │    └── TestExecutionDocument.java   <-- Mappatura specifica per MongoDB (@Document)
 │    │    ├── repository/
 │    │    │    └── TestExecutionMongoRepository.java <-- Interfaccia Spring Data Mongo
 │    │    └── adapter/
 │    │         └── TestExecutionRepositoryAdapter.java <-- Implementazione della Port del Dominio
 │    │
 │    └── mapper/                                <-- Traduttori tra gli strati protetti
 │         └── TestExecutionMapper.java
 │
 ├── config/                                     <-- Configurazioni globali cross-feature (es. MongoDB)
 │    └── MongoConfig.java
 │
 ├── exception/                                  <-- Gestione centralizzata errori HTTP
 │    └── GlobalExceptionHandler.java
 │
 └── Application.java