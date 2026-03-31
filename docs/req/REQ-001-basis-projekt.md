# REQ-001 — Basis-Projekt

| Attribut       | Wert                                                  |
|----------------|-------------------------------------------------------|
| ID             | REQ-001                                               |
| Titel          | Basis-Projekt: Vaadin + Spring Boot + MySQL + Docker  |
| Status         | Umgesetzt                                             |
| Branch         | `claude/setup-base-project-8DLXN`                     |
| Erstellt am    | 2026-03-31                                            |

---

## 1. Ziel

Aufbau eines lauffähigen Skelett-Projekts als gemeinsame Grundlage aller weiteren
Entwicklungsaufgaben. Das Projekt muss lokal (Entwicklungsumgebung) wie auch als
Docker-Compose-Stack vollständig startbar sein.

---

## 2. Technologie-Stack

Die folgenden Versionen sind verbindlich für das Gesamtprojekt (vgl. [index.md](index.md)).

| Komponente       | Version      | Artefakt / Image                              |
|------------------|--------------|-----------------------------------------------|
| Java             | 21 (LTS)     | `eclipse-temurin:21-jdk-jammy` (Build-Stage)  |
| Spring Boot      | 3.4.4        | `spring-boot-starter-parent`                  |
| Vaadin Flow      | 24.6.4       | `vaadin-spring-boot-starter` (via BOM)        |
| MySQL            | 8.4          | `mysql:8.4` (Docker Image)                    |
| Flyway           | (via BOM)    | `flyway-core` + `flyway-mysql`                |
| Spring Security  | (via BOM)    | `spring-boot-starter-security`                |
| Maven            | 3.9.x        | Maven Wrapper (`mvnw`)                        |

> Alle Spring- und Flyway-Versionen werden über `spring-boot-starter-parent:3.4.4`
> verwaltet und nicht manuell versioniert, ausgenommen Vaadin (eigenes BOM).

---

## 3. Anforderungen

### 3.1 Build & Projektstruktur

- **REQ-001-01** Das Projekt nutzt Maven als Build-Tool mit Maven Wrapper (`mvnw` / `mvnw.cmd`),
  sodass kein lokal installiertes Maven zwingend erforderlich ist.
- **REQ-001-02** Die Maven-Koordinaten lauten `com.vojj:app:0.0.1-SNAPSHOT`.
- **REQ-001-03** Das Maven-Profil `production` führt zusätzlich den Vaadin-Frontend-Build
  (`build-frontend`) aus und wird beim Docker-Image-Build aktiviert (`-Pproduction`).

### 3.2 Datenbankanbindung

- **REQ-001-04** Als relationale Datenbank wird MySQL 8.4 verwendet.
- **REQ-001-05** Verbindungsparameter (URL, Benutzer, Passwort) werden ausschließlich über
  Umgebungsvariablen (`DB_URL`, `DB_USER`, `DB_PASSWORD`) konfiguriert; Standardwerte
  für die lokale Entwicklung sind in `application.properties` hinterlegt.
- **REQ-001-06** Das Hibernate DDL-Management ist auf `validate` gesetzt; die Schemahoheit
  liegt bei Flyway (kein `create`, `update` oder `create-drop` im Produktiv- oder Dev-Betrieb).
- **REQ-001-07** Flyway liest Migrationsskripte aus `classpath:db/migration`.
  Die initiale Migration `V1__init_schema.sql` legt die Tabelle `users` an
  (Zeichensatz `utf8mb4`, Kollation `utf8mb4_unicode_ci`).

### 3.3 Vaadin & Spring Security

- **REQ-001-08** Die Vaadin-Webanwendung läuft auf Port `8080`.
- **REQ-001-09** Spring Security ist über `VaadinWebSecurity` integriert, das Vaadin-spezifische
  Anforderungen (CSRF-Token, statische Ressourcen, Push-Endpunkt) korrekt behandelt.
- **REQ-001-10** Die Startseite (`/`) ist als `MainView` mit `@AnonymousAllowed` erreichbar
  und zeigt eine einfache "Hello World"-Ansicht zur Verifikation des Gesamtstacks.

### 3.4 Schichtenarchitektur (Skeleton)

- **REQ-001-11** Das Projekt enthält eine exemplarische JPA-Entität `User` (Tabelle `users`)
  mit den Feldern `id`, `username`, `email`.
- **REQ-001-12** Für `User` existieren `UserRepository` (Spring Data JPA) und `UserService`
  als Basis für fachliche Erweiterungen in Folgeaufgaben.

### 3.5 Docker-Deployment

- **REQ-001-13** `docker-compose.yml` startet den vollständigen Stack (Anwendung + Datenbank)
  über ein Docker Bridge-Netzwerk (`vojj-net`). Die App-Konfiguration erfolgt über
  Umgebungsvariablen aus einer `.env`-Datei (Vorlage: `.env.example`).
- **REQ-001-14** Der MySQL-Container enthält einen Healthcheck (`mysqladmin ping`);
  der App-Container startet erst nach `service_healthy` der Datenbank
  (`depends_on: condition: service_healthy`), um Verbindungsfehler beim Start zu vermeiden.
- **REQ-001-15** Datenbankdaten werden in einem benannten Docker-Volume (`db_data`) persistiert.
- **REQ-001-16** `docker-compose.dev.yml` startet ausschließlich die Datenbank für die lokale
  Entwicklung; die Anwendung wird dabei direkt mit Maven gestartet:
  ```
  mvn spring-boot:run -Dspring-boot.run.profiles=dev
  ```

### 3.6 Docker-Image

- **REQ-001-17** Das `Dockerfile` verwendet einen zweistufigen Build (Multi-Stage):
  - **Stage 1 (`builder`):** `eclipse-temurin:21-jdk-jammy` — Maven-Build mit `-Pproduction`
  - **Stage 2 (`runtime`):** `eclipse-temurin:21-jre-jammy` — schlankes Laufzeit-Image
- **REQ-001-18** Im Laufzeit-Image wird die Anwendung unter einem dedizierten Nicht-Root-Benutzer
  (`appuser`) ausgeführt.
- **REQ-001-19** Die `pom.xml` wird vor dem Quellcode kopiert (`dependency:go-offline`),
  um Maven-Abhängigkeiten als separaten Docker-Layer zu cachen.

### 3.7 Testumgebung

- **REQ-001-20** Unit- und Integrationstests laufen gegen eine H2-In-Memory-Datenbank
  (Profil `test`), sodass keine externe Datenbank für den Testlauf benötigt wird.
- **REQ-001-21** Flyway ist im Testprofil deaktiviert; Hibernate übernimmt mit `create-drop`
  das Schemamanagement für den Testlauf.

### 3.8 Sicherheit & Konfiguration

- **REQ-001-22** Die Datei `.env` ist in `.gitignore` eingetragen und wird nie committed.
  Secrets werden ausschließlich über Umgebungsvariablen zur Laufzeit injiziert.

---

## 4. Betroffene Artefakte

| Artefakt                                                                 | Typ             | Beschreibung                                  |
|--------------------------------------------------------------------------|-----------------|-----------------------------------------------|
| `pom.xml`                                                                | Build           | Maven-Projektdeskriptor (→ REQ-001-01..03)    |
| `Dockerfile`                                                             | Deploy          | Multi-Stage Docker-Build (→ REQ-001-17..19)   |
| `docker-compose.yml`                                                     | Deploy          | Produktions-Stack (→ REQ-001-13..15)          |
| `docker-compose.dev.yml`                                                 | Deploy          | Entwicklungs-DB (→ REQ-001-16)                |
| `.env.example`                                                           | Konfiguration   | Vorlage für Secrets (→ REQ-001-22)            |
| `src/main/resources/application.properties`                              | Konfiguration   | Basis-Konfiguration (→ REQ-001-05..07)        |
| `src/main/resources/application-dev.properties`                          | Konfiguration   | Dev-Profil                                    |
| `src/main/resources/application-prod.properties`                         | Konfiguration   | Prod-Profil                                   |
| `src/main/resources/db/migration/V1__init_schema.sql`                    | Datenbank       | Initiales Schema (→ REQ-001-07)               |
| `src/main/java/com/vojj/app/Application.java`                            | Quellcode       | Spring-Boot-Einstiegspunkt                    |
| `src/main/java/com/vojj/app/config/SecurityConfig.java`                  | Quellcode       | VaadinWebSecurity-Konfiguration (→ REQ-001-09)|
| `src/main/java/com/vojj/app/views/MainView.java`                         | Quellcode       | Vaadin-Startseite (→ REQ-001-10)              |
| `src/main/java/com/vojj/app/entity/User.java`                            | Quellcode       | JPA-Entität (→ REQ-001-11)                    |
| `src/main/java/com/vojj/app/repository/UserRepository.java`              | Quellcode       | Spring-Data-Repository (→ REQ-001-12)         |
| `src/main/java/com/vojj/app/service/UserService.java`                    | Quellcode       | Service-Schicht (→ REQ-001-12)                |
| `src/test/java/com/vojj/app/ApplicationTests.java`                       | Test            | Spring-Context-Test (→ REQ-001-20)            |
| `src/test/resources/application-test.properties`                         | Test            | H2-Konfiguration (→ REQ-001-20..21)           |
