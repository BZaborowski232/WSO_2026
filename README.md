# Projekt WSO: Alokacja zadań w chmurze (CloudSim)

Projekt realizowany w ramach przedmiotu Wirtualne Środowiska Obliczeniowe. Celem jest porównanie algorytmów alokacji (FCFS, Min-Min, Max-Min, EAMM).

## Wymagania
- Java 17
- Maven 3.x
- Python 3.x (do analizy wyników)

## Instalacja biblioteki CloudSim
Jeśli Maven nie widzi biblioteki, zainstaluj ją lokalnie komendą:
```bash
mvn install:install-file -Dfile=lib/cloudsim-3.0.3.jar -DgroupId=org.cloudbus.cloudsim -DartifactId=cloudsim -Dversion=3.0.3 -Dpackaging=jar -DgeneratePom=true
```

## Potencjalny Plan Eksperymentów propozycja
Celem jest przeprowadzenie serii testów dla wszystkich kombinacji algorytmów, wariantów infrastruktury i obciążeń:

1. Eksperyment 1 (Porównanie podstawowe): Uruchomienie wszystkich algorytmów dla 500 zadań (Wariant A).
2. Eksperyment 2 (Skalowalność): Porównanie wydajności przy 100, 500 oraz 1000 zadań.
3. Eksperyment 3 (Skalowanie infrastruktury): Porównanie Wariantu A (5 hostów) vs Wariant B (10 hostów).
4. Eksperyment 4 (Test EAMM): Analiza efektywności energetycznej autorskiego algorytmu EAMM w porównaniu do Min-Min.

## Jak uruchomić symulację?
Trzeba zapuscic komende dla wybranego eksperymentu, wariantu infrastruktury i liczby taskow według schemtu: (pamietaj o spacji miedzy litera wariantu a liczba taskow)

```bash
mvn clean compile exec:java -Dexec.mainClass="pl.edu.pw.wso.experiments.[NazwaEksperymentu]" -Dexec.args="[Wariant A/B] [LiczbaZadań]"
```

### Przykłady uruchomienia:
- EAMM (Wariant A, 500 zadań):
```bash
  mvn clean compile exec:java -Dexec.mainClass="pl.edu.pw.wso.experiments.EammExperiment" -Dexec.args="A 500"
```
- Min-Min (Wariant B, 1000 zadań):
```bash
  mvn clean compile exec:java -Dexec.mainClass="pl.edu.pw.wso.experiments.MinMinExperiment" -Dexec.args="B 1000"
```
- Max-Min (Wariant B, 100 zadań):
```bash  
  mvn clean compile exec:java -Dexec.mainClass="pl.edu.pw.wso.experiments.MaxMinExperiment" -Dexec.args="B 100"
```

itp itd 

## Wyniki
Wyniki (pliki .csv) są zapisywane automatycznie w folderze `DATA/` (np. `wyniki_EAMM_A_500.csv`). Na koniec pewnie trzeba bedzie zrobic jakas ladna analize pewnie w pythonie itp, mozna tez zrobic ladny dashboard zeby sie w www wyswietlal z biblio:

`import streamlit as st`
