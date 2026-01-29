# Zarządzanie zależnoścniami
Projekt jest skonfigurowany jako Maven multi‑module z parent POM w pom.xml(plik konfiguracyjny mavena). Każdy z serwisóœ ma własny plik konfiguracyjny.
Budowanie i testowanie odbywa się przez Maven (./mvnw ...)

# Opis testow (lokalnie vs CI)

## CI (GitHub Actions)
Workflow `CI` uruchamia sie automatycznie po `push` i `pull_request` i wykonuje:
- Build i testy: `./mvnw -B clean verify -Dnvd.api.key=${NVD_API_KEY}`
- Testy jednostkowe (JUnit + Mockito):
  - `booking-service`: logika biznesowa rezerwacji (walidacja czasu, kolizje, role uzytkownikow)
  - `rewards-service`: logika punktow, losowania i mini-gry (warunki brzegowe, bledy)
- Testy integracyjne HTTP (SpringBootTest + RestTestClient + WireMock):
  - `user-service`: testy CRUD uzytkownikow przez REST
  - `user-service`: walidacje danych wejsciowych (np. bledny email, brak roli)
  - `booking-service`: tworzenie rezerwacji przez REST, z zasymulowanym `user-service` (WireMock)
  - `booking-service`: walidacje danych wejsciowych (np. brak `endTime`)
  - `rewards-service`: dodawanie punktow przez REST
  - `rewards-service`: walidacje danych wejsciowych (np. `points=0`)
- Kontrole jakosci:
  - Checkstyle: sprawdza styl kodu (np. importy, nawiasy, dlugosc linii)
  - PMD (Programming Mistake Detector):
    - statyczna analiza kodu Java
    - wykrywa bledy i zle praktyki (np. nieuzywane zmienne, zbyt zlozone metody)
    - raport generowany w czasie buildu
  - SpotBugs:
    - statyczna analiza bajtkodu (po kompilacji)
    - wykrywa potencjalne bledy runtime (np. NPE, bledy synchronizacji)
    - raport generowany w czasie buildu
- Skan bezpieczenstwa:
  - OWASP Dependency-Check:
    - skanuje zaleznosci pod katem znanych podatnosci (CVE)
    - pobiera dane z NVD (wymaga NVD API key w CI)
    - raport generowany w czasie buildu
  - Build jest blokowany, gdy wykryte CVE maja CVSS >= 7.0
  - Przy czasowej niedostepnosci NVD build nie jest blokowany, ale raport moze byc niepelny

W CI wymagana jest zmienna `NVD_API_KEY` ustawiona jako Secret w repozytorium.

## Lokalnie
Sa dwa typowe tryby:

1) Pelny build jak w CI (wymaga NVD API key):
```
export NVD_API_KEY=twoj_klucz
./mvnw -B clean verify
```

2) Build bez skanu bezpieczenstwa (szybszy, bez NVD):
```
./mvnw -B -Pskip-security clean verify
```

W obu trybach uruchamiane sa te same testy jednostkowe i integracyjne.
Roznica dotyczy jedynie skanowania OWASP Dependency-Check.

# Integracja z usluga zewnetrzna (pogoda)
W `booking-service` dodano integracje HTTP z zewnetrznym API Open-Meteo:
- Klient HTTP: `WeatherClient` uzywa `RestClient` do pobrania prognozy godzinowej.
- Endpoint aplikacji: `GET /api/bookings/weather?startTime=...` zwraca prognoze dla wskazanego czasu rezerwacji.
- Konfiguracja: `weather.base-url`, `weather.latitude`, `weather.longitude` (mozliwe nadpisanie przez zmienne srodowiskowe).
- Gdy API pogodowe jest niedostepne lub brak danych dla czasu, zwracany jest blad 502.



# Wdrożenie
W trakcie builda Maven tworzy gotowe pliki JAR dla każdego procesu w odpowiednich folderach target
