# Anforderungsverzeichnis — vojj

Dieses Verzeichnis enthält alle Anforderungen des Projekts **vojj**.
Jede Anforderung wird in einer eigenen Datei nach dem Schema `REQ-NNN-<kurzname>.md` gepflegt.

## Übersicht

| ID        | Titel                  | Status      | Branch                              |
|-----------|------------------------|-------------|-------------------------------------|
| [REQ-001](REQ-001-basis-projekt.md) | Basis-Projekt          | Umgesetzt   | `claude/setup-base-project-8DLXN`  |

## Statusdefinitionen

| Status      | Bedeutung                                              |
|-------------|--------------------------------------------------------|
| Entwurf     | Anforderung beschrieben, noch nicht begonnen           |
| In Arbeit   | Implementierung läuft                                  |
| Umgesetzt   | Implementierung abgeschlossen, auf Branch committed    |
| Abgenommen  | Code in `main` gemergt, vom Auftraggeber freigegeben  |

## Konventionen

- Anforderungs-IDs sind aufsteigend und unveränderlich.
- Jede Anforderung referenziert den zugehörigen Git-Branch und die betroffenen Artefakte.
- Versionsnummern der Abhängigkeiten (Java, Spring Boot, Vaadin, MySQL) gelten projektweit
  und sind in [REQ-001](REQ-001-basis-projekt.md) festgelegt. Folgeaufgaben übernehmen diese
  Versionen, sofern nicht explizit abweichend angegeben.
