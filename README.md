# Projekt WSO: Alokacja zadań w chmurze (CloudSim)

# 0. Wstęp

#### 0.1. Kontekst i background biznesowy
Współczesne platformy streamingowe, takie jak np. YouTube, mierzą się codziennie z wyzwaniem przetwarzania masowych ilości danych wideo. W momencie, gdy użytkownik przesyła nowy plik na platformę, system nie tylko zapisuje surowy materiał, ale musi natychmiast uruchomić sekwencję zróżnicowanych operacji przetwarzania. Należą do nich m.in. konwersja filmu do wielu różnych rozdzielczości (transkodowanie), generowanie miniatur podglądu, zaawansowana kompresja danych, automatyczna analiza treści pod kątem bezpieczeństwa oraz ostateczny zapis w rozproszonej strukturze pamięci masowej. 

Z punktu widzenia inżynierii systemów chmurowych, każda z tych operacji stanowi pojedyncze, niezależne zadanie obliczeniowe (w nomenklaturze chmurowej: *task* lub *cloudlet*), które musi zostać precyzyjnie przypisane do odpowiedniej maszyny wirtualnej (VM) funkcjonującej w centrum danych.

### 0.2. Definicja problemu badawczego
Głównym problemem, na który odpowiada niniejszy projekt, jest zarządzanie zasobami w godzinach szczytowego natężenia ruchu na platformie. Gdy liczba przesyłanych jednocześnie materiałów gwałtonie rośnie, w infrastrukturze chmurowej dochodzi do zjawiska przeciążenia serwerów, drastycznego wydłużenia czasu oczekiwania na przetworzenie filmów, skokowego wzrostu zużycia energii elektrycznej oraz bezpośredniego ryzyka wystąpienia awarii lub opóźnień po stronie użytkownika końcowego.

Kluczem do optymalizacji takiego środowiska nie jest bezmyślne dokładanie kolejnych fizycznych serwerów, lecz zastosowanie inteligentnego algorytmu alokacji zadań. Problem sprowadza się do znalezienia takiej strategii, która pozwoli na jednoczesne skrócenie czasu przetwarzania plików (Makespan), zminimalizowanie przeciążeń maszyn wirtualnych oraz redukcję zużycia energii całego centrum danych.

### 0.3. Cel i zakres projektu
Głównym celem badawczym jest implementacja, analiza porównawcza oraz ocena efektywności różnych algorytmów szeregowania zadań w środowisku chmurowym symulowanym za pomocą zaawansowanego frameworka CloudSim. W ramach prac porównano klasyczne podejścia inżynierskie, takie jak:
* **FCFS (First Come First Serve):** podejście kolejki naiwnej realizujące zadania dokładnie w kolejności ich napływania.
* **Min-Min:** algorytm zachłanny, przydzielający najkrótsze zadania do maszyn zapewniających najszybszy czas wykonania.
* **Max-Min:** strategia dająca pierwszeństwo zadaniom najdłuższym, zapobiegająca blokowaniu dużych procesów w kolejce.

Istotnym punktem i unikalną wartością projektu jest opracowanie, zaimplementowanie i przetestowanie własnej autorskiej modyfikacji algorytmu o nazwie EAMM (Energy-Aware Min-Min). Projekt ma na celu udzielenie odpowiedzi na fundamentalne pytanie z zakresu Green Computing: czy możliwe jest efektywne zmniejszenie zużycia energii centrum danych poprzez zrównoważone rozkładanie obciążenia infrastruktury, bez drastycznego i zauważalnego pogorszenia ogólnej wydajności czasowej systemu?



### 1. Wymagania i setup środowiska
- Java 17
- Maven 3.x
- Python 3.x (do analizy wyników)

#### 1.2 Instalacja biblioteki CloudSim
Jeśli Maven nie widzi biblioteki, zainstaluj ją lokalnie komendą:
```bash
mvn install:install-file -Dfile=lib/cloudsim-3.0.3.jar -DgroupId=org.cloudbus.cloudsim -DartifactId=cloudsim -Dversion=3.0.3 -Dpackaging=jar -DgeneratePom=true
```

#### 1.3 Jak uruchomić symulację?
Trzeba zapuscic komende dla wybranego eksperymentu, wariantu infrastruktury i liczby taskow według schemtu: (pamietaj o spacji miedzy litera wariantu a liczba taskow)

```bash
mvn clean compile exec:java -Dexec.mainClass="pl.edu.pw.wso.experiments.[NazwaEksperymentu]" -Dexec.args="[Wariant A/B] [LiczbaZadań]"
```

**Przykłady uruchomienia:**
- EAMM (Wariant A, 500 zadań):
```bash
  mvn clean compile exec:java 
  -Dexec.mainClass="pl.edu.pw.wso.experiments.EammExperiment" -Dexec.args="A 500"
```
- Min-Min (Wariant B, 1000 zadań):
```bash
  mvn clean compile exec:java 
  -Dexec.mainClass="pl.edu.pw.wso.experiments.MinMinExperiment" -Dexec.args="B 1000"
```
- Max-Min (Wariant B, 100 zadań):
```bash  
  mvn clean compile exec:java 
  -Dexec.mainClass="pl.edu.pw.wso.experiments.MaxMinExperiment" -Dexec.args="B 100"
```

Aby uruchomić wizualizację należy posłużyć się komendą:

```bash
uv run streamlit run app.py
```


### 2. Eksperymenty

Wyniki eksperymentów zapisywane są automatycznie w folderze `DATA/`. Pliki mają przypisywane tagi w nazwie, zgodnie z wykorzystanym algorytmem, wariantem hosta oraz ilością tasków (np. `wyniki_EAMM_A_500.csv`).

#### 2.1 Metodyka i zakres przeprowadzonych badań
W celu rzetelnej oceny zaimplementowanych algorytmów alokacji zadań (FCFS, Min-Min, Max-Min oraz autorskiego EAMM), przygotowano kompleksowe środowisko symulacyjne oparte na bibliotece CloudSim. Aby wyniki eksperymentów wiernie odzwierciedlały warunki panujące w rzeczywistych, rozproszonych centrach danych, wprowadzono kluczowy element heterogeniczności zasobów i zadań. Maszyny wirtualne dysponują zróżnicowaną mocą obliczeniową (od 500 do 2500 MIPS), a generowane zadania różnią się długością i wymaganym czasem przetwarzania (symulacja różnego rodzaju operacji, np. od kompresji wideo po generowanie miniatur).


#### 2.2 Faza kalibracji: Wpływ homogeniczności środowiska na wyniki
W początkowej fazie eksperymentów napotkano anomalię polegającą na tym, że wszystkie badane algorytmy (FCFS, Min-Min, Max-Min oraz EAMM) osiągały niemal identyczny czas wykonania (Makespan oscylujący na płaskim poziomie ok. 60 sekund). Analiza tego zjawiska dostarczyła istotnych wniosków na temat samej natury chmurowych algorytmów przydziału.

**Identyfikacja problemu:**
Przyczyną identycznych wyników był brak początkowego zróżnicowania (heterogeniczności) maszyn wirtualnych. W pierwotnej konfiguracji każdej maszynie wirtualnej przydzielono dokładnie taką samą moc obliczeniową (sztywny limit 1000 MIPS). W środowisku całkowicie homogenicznym, gdzie każda jednostka jest tak samo szybka, zaawansowane algorytmy heurystyczne tracą swoje pole manewru decyzyjnego. Niezależnie od tego, czy zadanie przydzielał naiwny algorytm FCFS, czy poszukujący optymalizacji Min-Min, czas jego przetworzenia był z góry zdeterminowany i stały dla każdej maszyny. W efekcie wszystkie algorytmy degradowały się do roli prostego rozdzielacza zadań (typu Round-Robin/Load Balancer).


![Podgląd dashboardu](images/Wstepna_wizu_wynikow.png)

**Wdrożone rozwiązanie i wnioski badawcze:**
Aby środowisko symulacyjne nabrało wartości analitycznej i wiernie odzwierciedlało rzeczywiste centra danych (gdzie występują zarówno słabe, jak i potężne instancje obliczeniowe), przeprowadzono rekonfigurację alokatora maszyn. Wprowadzono pule maszyn wirtualnych o zróżnicowanej mocy (od wolnych instancji 500 MIPS po wysoce wydajne węzły 2500 MIPS). 

Dopiero wprowadzenie heterogeniczności środowiska "odblokowało" potencjał badanych heurystyk:
* **Min-Min i Max-Min** zyskały możliwość aktywnego poszukiwania najszybszych instancji i kierowania na nie odpowiednich zadań, co od razu drastycznie skróciło Makespan.
* **EAMM** mógł zaprezentować swoją autorską mechanikę obliczania kary za obciążenie (load penalty), unikając zjawiska "zatykania" nielicznych, najszybszych maszyn nadmierną liczbą zadań.

Powyższe studium przypadku stanowi twardy dowód na to, że stosowanie skomplikowanych heurystyk przydziału ma uzasadnienie inżynierskie i biznesowe wyłącznie w środowiskach heterogenicznych. W infrastrukturze homogenicznej nakład obliczeniowy na działanie algorytmu mija się z celem, gdyż najprostsza polityka kolejkowania przynosi identyczne rezultaty czasowe.


#### 2.3 Wizualizacja docelowa
Aby lepiej zilustrować opisane różnice w czasie wykonania dla badanych scenariuszy, wyniki zostały przetworzone za pomocą dedykowanego panelu analitycznego. Przygotowany dashboard składa się z trzech głównych elementów: bocznego panelu filtrów (pozwalającego na dynamiczne włączanie i wyłączanie widoczności wariantów oraz algorytmów), zgrupowanego wykresu słupkowego prezentującego zestawienie czasowe z podziałem na obciążenie zadaniami, a także interaktywnej tabeli zawierającej surowe logi z przebiegu symulacji. Poniżej zamieszczono zrzut ekranu prezentujący główne zestawienie:

![Podgląd dashboardu](images/Raport_po_poprawkach.png)


#### 2.4 Eksperymenty właściwe

Zestaw eksperymentów oparto na trzech głównych wymiarach testowych, co pozwala na pełną ocenę i profilowanie zachowania poszczególnych algorytmów:
1. **Zróżnicowanie obciążenia (100, 500, 1000 zadań):** Badania te mają na celu weryfikację skalowalności algorytmów i ich odporności na skokowe wzrosty zapotrzebowania na zasoby chmurowe.
2. **Zróżnicowanie infrastruktury (Wariant A vs Wariant B):** Środowisko przetestowano w dwóch konfiguracjach: bazowej (Wariant A: 5 hostów fizycznych, 20 VM) oraz rozszerzonej (Wariant B: 10 hostów fizycznych, 40 VM). Pozwala to sprawdzić, czy dany algorytm potrafi optymalnie zagospodarować dodatkowe zasoby po przeprowadzeniu skalowania horyzontalnego.
3. **Analiza modyfikacji EAMM względem klasycznych heurystyk:** Trzecim filarem badawczym jest ocena autorskiego algorytmu EAMM (Energy-Aware Min-Min). Eksperyment ma wykazać, jak wprowadzenie kary za skumulowane obciążenie węzła wpływa na całkowity czas wykonania (Makespan) w porównaniu z rozwiązaniami klasycznymi (Min-Min, Max-Min) oraz przydziałem w pełni naiwnym (FCFS i Round Robin).

#### 2.5 Interpretacja wyników i wnioski
Na podstawie zagregowanych danych symulacyjnych (parametr Makespan dla każdej z badanych kombinacji) sformułowano następujące wnioski, stanowiące punkt wyjścia do szczegółowej analizy:

* **Słabość przydziału naiwnego (FCFS jako Baseline):**
  Zgodnie z przewidywaniami teoretycznymi, algorytm FCFS (First-Come, First-Serve) osiąga najsłabsze rezultaty (Makespan na poziomie około 60-65 sekund). Jego działanie "na ślepo" w środowisku heterogenicznym sprawia, że zasobochłonne zadania często trafiają na najsłabsze maszyny wirtualne, co całkowicie dławi wydajność systemu i blokuje kolejkę. Stanowi to twardy punkt odniesienia, udowadniający konieczność stosowania zaawansowanego równoważenia obciążenia.

* **Dominacja czasowa algorytmów heurystycznych (Min-Min):**
  Zastosowanie algorytmu Min-Min pozwala na drastyczną poprawę wydajności – czas całkowity skraca się o około 20-30% w stosunku do metody FCFS. Dzięki "zachłannemu" (greedy) parowaniu najkrótszych zadań z najszybszymi instancjami VM, system niezwykle sprawnie rozładowuje kolejkę. Wyniki algorytmu Max-Min plasują się nieco poniżej Min-Min, co wynika z priorytetyzacji najcięższych zadań kosztem wstrzymywania operacji krótszych.

* **Efektywność i stabilność infrastrukturalna algorytmu EAMM:**
  Wyniki autorskiego algorytmu EAMM potwierdzają założenia optymalizacyjne projektu. EAMM drastycznie wyprzedza bazowy FCFS, jednocześnie notując jedynie marginalnie wyższy Makespan w stosunku do algorytmu Min-Min. Należy jednak zaznaczyć, że ten niewielki narzut czasowy **nie jest anomalią, lecz celowym kompromisem (trade-off)**. Dzięki zastosowaniu w funkcji kosztu tzw. kary za przeciążenie (load penalty), algorytm EAMM w przeciwieństwie do Min-Min nie forsuje całej pracy na najwydajniejsze maszyny. Prowadzi to do bardziej zrównoważonego obciążenia całego klastra, co w ujęciu długoterminowym przekłada się na wyższą stabilność sprzętu i ograniczenie szczytowego zużycia energii w centrum danych.

* **Ograniczony zysk ze skalowania przy niskim ruchu:**
  Porównanie Wariantu A z Wariantem B uwydatniło, że dokładanie zasobów sprzętowych przynosi wymierne korzyści tylko w scenariuszach wysokiego obciążenia (500 i 1000 zadań). Przy małym ruchu (100 zadań) czasy wykonania pozostają niemal identyczne w obu wariantach, ponieważ zadania i tak swobodnie mieszczą się w puli najszybszych maszyn wirtualnych.

* **Ograniczenia strategii cyklicznej (Round Robin):**
  Analiza wykresów dowodzi, że algorytm Round Robin osiąga rezultaty niemal identyczne z przydziałem FCFS (stanowiąc wspólnie najsłabsze podejścia w zestawieniu). Wynika to z faktu, że metoda ta całkowicie ignoruje parametry zadań oraz zróżnicowaną moc maszyn wirtualnych. Cykliczne "rozdawanie" zadań w środowisku heterogenicznym sprawia, że zasobochłonne operacje z takim samym prawdopodobieństwem trafiają na najsłabsze jednostki (500 MIPS), co na najsilniejsze (2500 MIPS), prowadząc do powstawania wąskich gardeł (bottlenecks). Stanowi to dowód na to, że sam równy podział ilościowy zadań nie wystarczy do optymalizacji czasu wykonania.


#### 2.6 Modelowanie i analiza efektywności energetycznej

##### 2.6.1 Teoretyczne podstawy modelu energetycznego w CloudSim
W celu realizacji założeń paradygmatu *Green Computing* (zielonego przetwarzania), infrastruktura symulacyjna zdefiniowana w pliku `DatacenterFactory.java` wykorzystuje komponenty klasy `PowerHost`. Pobór mocy maszyn fizycznych podczas wykonywania zadań obliczeniowych jest kalkulowany przez silnik CloudSim w oparciu o liniowy model energetyczny (`PowerModelLinear`). Model ten definiuje zużycie energii jako funkcję aktualnego stopnia utylizacji procesora (CPU utilization):

$$P(u) = P_{\text{static}} + (P_{\text{max}} - P_{\text{static}}) \times u$$

Gdzie:
* $P(u)$ – całkowity pobór mocy przez host w danym momencie symulacji (wyrażony w watach),
* $P_{\text{static}}$ – moc statyczna pobierana przez serwer w stanie spoczynku (tzw. *idle power*, ustawiona w konfiguracji na poziomie 30% mocy maksymalnej),
* $P_{\text{max}}$ – maksymalny pobór mocy przy 100% obciążeniu procesora (zdefiniowany indywidualnie dla każdego typu hosta, np. 150W dla energooszczędnych węzłów H1-H2, aż do 500W dla wysokowydajnych serwerów H9-H10),
* $u$ – aktualny stopień wykorzystania zasobów CPU przez przypisane maszyny wirtualne ($u \in \langle 0; 1 \rangle$).

##### 2.6.2 Mechanizm optymalizacji energetycznej w algorytmie EAMM
W klasycznych heurystykach szeregowania (Min-Min, Max-Min) alokacja koncentruje się bezwzględnie na minimalizacji czasu Makespan. Prowadzi to do zjawiska, w którym najwydajniejsze maszyny wirtualne są nieustannie eksploatowane w 100%, podczas gdy pozostałe hosty fizyczne pozostają włączone i zużywają energię w stanie spoczynku ($P_{\text{static}}$), nie wykonując żadnej produktywnej pracy. 

Zaimplementowany algorytm EAMM (Energy-Aware Min-Min) przeciwdziała temu zjawisku na poziomie samej funkcji kosztu alokacji, wprowadzając komponent kary za skumulowane obciążenie kolejki zadań danej maszyny wirtualnej (`loadPenalty`):

```java
double loadPenalty = vmTaskCount[i] * executionTime;
double cost = (weightTime * completionTime) + (weightLoad * loadPenalty);
```

Wprowadzenie tego parametru realizuje teoretyczne założenia optymalizacji energetycznej na dwóch kluczowych poziomach:
* Dywersyfikacja profilu termicznego (Load Balancing): Zapobiega powstawaniu tzw. hot-spots (punktów krytycznego przegrzania pojedynczych serwerów w szafie rack). Praca jest rozkładana na większą liczbę maszyn wirtualnych, co pozwala serwerom fizycznym pracować w optymalnych zakresach sprawności energetycznej. W rzeczywistych centrach danych przekłada się to bezpośrednio na drastyczne obniżenie kosztów energii zużywanej przez systemy klimatyzacji i chłodzenia.
* Redukcja strat dynamicznych: Poprzez kontrolowane dopuszczanie do obliczeń maszyn o niższym taktowaniu MIPS w sytuacji, gdy maszyny najszybsze są już obciążone, EAMM wypłaszcza profil poboru mocy dynamicznej całego centrum danych, unikając gwałtownych skoków obciążenia sieci zasilającej.

#### 2.6.3 Architektura integracji pomiaru i perspektywy rozwoju klastra
Na obecnym etapie prac badawczych parametr efektywności energetycznej został w pełni zintegrowany z logiką decyzyjną brokera chmurowego (algorytm dynamicznie szacuje i ogranicza obciążenie maszyn). Aby dokonać pełnej, empirycznej weryfikacji zysków energetycznych wyrażonych bezpośrednio w kilowatogodzinach (kWh) lub dżulach (J), naturalnym kolejnym krokiem rozwoju platformy testowej jest rozbudowa modułu raportowania.
Wymaga to modyfikacji wywołania w głównych klasach eksperymentów i pełnego przejścia z podstawowej klasy symulacyjnej Datacenter na dedykowaną klasę PowerDatacenter. Pozwoli to na cykliczne odpytywanie silnika CloudSim o sumaryczną energię zużytą przez wszystkie komponenty sprzętowe od początku trwania symulacji przy użyciu metody:

```python
double totalEnergyConsumed = ((PowerDatacenter) datacenter).getConsumedEnergy();
```

Wprowadzenie tej zmiennej do modułu CsvExporter oraz zaktualizowanie skryptu analitycznego app.py umożliwi wdrożenie do dashboardu dodatkowego wykresu porównawczego. Wykres ten w sposób jednoznaczny zobrazuje zysk energetyczny jako bezpośredni i mierzalny trade-off (kompromis) dla nieznacznie wydłużonego całkowitego czasu Makespan, w pełni udowadniając zasadność stosowania algorytmu EAMM w strukturach nowoczesnych centrów danych.