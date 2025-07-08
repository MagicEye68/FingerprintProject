# 🔬 FingerPrint Project

**FingerPrint Project** è un progetto accademico sviluppato in **Java** con l’obiettivo di analizzare e confrontare **impronte digitali** attraverso tecniche di **matching strutturale** basate su **grafi**. Il sistema implementa un framework modulare per la trasformazione di impronte in strutture grafiche e il confronto tramite algoritmi di **sotto-isomorfismo** e **scoring topologico**.

## 📚 Descrizione del progetto

Il progetto affronta il problema del **confronto di impronte digitali** modellando ciascuna impronta come un **grafo non orientato**, in cui i **minuziae points** (terminazioni, biforcazioni, ecc.) sono rappresentati come nodi, e le relazioni spaziali tra essi come archi.

Il sistema è suddiviso nei seguenti componenti principali:

- 🧩 **Parser interno**: analizza i dati biometrici raw (formato testuale o strutturato) e li traduce in un modello intermedio adatto alla generazione di grafi.
- 🌐 **Generatore di grafi**: fornisce più strategie di costruzione del grafo, tra cui:
  - **BFS** (Breadth-First Search)
  - **DFS** (Depth-First Search)
  - Metodi alternativi basati su distanze euclidee, soglie angolari, o aggregazioni locali.
- 🔎 **Motore di matching**: ricerca sotto-isomorfismi tra grafi con varie euristiche e ottimizzazioni computazionali.
- 📊 **Algoritmi di scoring**: valutano la **similarità strutturale** tra due grafi, producendo un punteggio normalizzato che quantifica la corrispondenza tra due impronte.


## 🧠 Obiettivi e risultati

L’obiettivo principale è stato quello di **sperimentare diversi approcci di rappresentazione e confronto** di impronte digitali come grafi, valutandone l’efficacia in termini di accuratezza e prestazioni. Il progetto è stato utilizzato per condurre **test sperimentali** su un dataset biometrico simulato, analizzando:

- L’impatto del metodo di generazione grafi sulla qualità del matching.
- Le prestazioni computazionali dei diversi approcci di sotto-isomorfismo.
- L’efficacia dei metodi di scoring nella discriminazione tra impronte simili e diverse.

## 📄 Contenuti principali

- `parser`: parser biometrici personalizzati.
- `graph`: moduli per la costruzione e rappresentazione dei grafi.
- `matcher`: implementazioni di confronto e sotto-isomorfismo.
- `scoring`: metriche di similarità e valutazione.
- `tests`: script di test e benchmark sperimentali.

---

*Progetto sviluppato come parte della tesi triennale in Informatica, [consultabile al seguente link.](https://github.com/MagicEye68/Thesis)*
