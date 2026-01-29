# Driving School System

## Problem biznesowy
Szkoly nauki jazdy nadal umawiaja jazdy recznie (telefon/SMS/papier). To powoduje kolizje, niska wykorzystanie czasu instruktorow, brak wgladu w grafiki i brak automatyzacji.

## Grupa uzytkownikow
- Kursanci (rezerwacje, historia, motywacja)
- Instruktorzy (grafik, potwierdzanie jazd)
- Administratorzy (raporty, zarzadzanie)

## Architektura mikroserwisow
- user-service (uzytkownicy i role)
- booking-service (rezerwacje jazd)
- rewards-service (punkty, losowania, mini-gra)

Kazdy serwis ma osobna baze danych. booking-service komunikuje sie z user-service przez REST.

## Uruchomienie lokalne
1) Build i testy:
```
./mvnw -B clean verify
```

2) Docker Compose (wymaga zbudowanych jarow):
```
./mvnw -B -pl user-service,booking-service,rewards-service -am package

docker compose up --build
```

Porty:
- user-service: http://localhost:8080
- booking-service: http://localhost:8082
- rewards-service: http://localhost:8083

## Swagger UI (Web UI dla API)
Po uruchomieniu serwisow:
- user-service: http://localhost:8080/swagger-ui/index.html (spec: /v3/api-docs)
- booking-service: http://localhost:8082/swagger-ui/index.html (spec: /v3/api-docs)
- rewards-service: http://localhost:8083/swagger-ui/index.html (spec: /v3/api-docs)

Jesli po wejściu na Swagger UI widzisz `500 /v3/api-docs`, przebuduj obrazy po zmianie zaleznosci:
```
./mvnw -B -Pskip-security clean verify
./mvnw -B -pl user-service,booking-service,rewards-service -am package
docker compose up --build
```

## Skan bezpieczeństwa (NVD API Key)
OWASP Dependency-Check wymaga klucza NVD. Ustaw go w terminalu przed uruchomieniem buildu:
```
export NVD_API_KEY=twoj_klucz
./mvnw -B clean verify
```
Windows (PowerShell):
```
$env:NVD_API_KEY="twoj_klucz"
./mvnw -B clean verify
```
Windows (cmd):
```
set NVD_API_KEY=twoj_klucz
./mvnw -B clean verify
```
Jeśli NVD zwraca 403/404 mimo poprawnego klucza, możesz lokalnie pominąć skan:
```
./mvnw -B -Pskip-security clean verify
```

## API

### user-service
Base URL: `http://localhost:8080`

- `GET /health`
  - Response: `"OK"`

- `GET /api/users`
  - Response: lista uzytkownikow
  - 200 OK

- `GET /api/users/{id}`
  - Response: obiekt uzytkownika
  - 200 OK
  - 404 gdy nie istnieje

- `POST /api/users`
  - Body:
    - `firstName` (string, required)
    - `lastName` (string, required)
    - `email` (string, required, format email, unikalny)
    - `role` (enum: TRAINEE, INSTRUCTOR, ADMIN, required)
    - `password` (string, optional)
  - Walidacje:
    - email poprawny i unikalny
  - Response: utworzony uzytkownik
  - 200 OK
  - 400 dla blednych danych
  - 409 gdy email zajety

- `DELETE /api/users/{id}`
  - 204 No Content
  - 404 gdy nie istnieje

### booking-service
Base URL: `http://localhost:8082`

- `GET /health`
  - Response: `"OK"`

- `POST /api/bookings`
  - Body:
    - `traineeId` (long, required)
    - `instructorId` (long, required)
    - `startTime` (ISO-8601, required)
    - `endTime` (ISO-8601, required)
  - Walidacje biznesowe:
    - startTime nie moze byc w przeszlosci
    - endTime musi byc po startTime
    - czas trwania 60-120 minut
    - brak nakladania jazd instruktora
    - trainee ma role TRAINEE, instructor ma role INSTRUCTOR (weryfikacja w user-service)
  - Response: rezerwacja ze statusem PENDING
  - 200 OK
  - 400 dla blednych danych
  - 422 dla naruszen biznesowych
  - 502 gdy user-service niedostepny

- `POST /api/bookings/{id}/confirm`
  - Walidacje: nie mozna potwierdzic anulowanej rezerwacji
  - Response: rezerwacja ze statusem CONFIRMED
  - 200 OK
  - 404 gdy nie istnieje
  - 422 gdy reguly biznesowe

- `POST /api/bookings/{id}/cancel`
  - Response: rezerwacja ze statusem CANCELLED
  - 200 OK
  - 404 gdy nie istnieje

- `GET /api/bookings/{id}`
  - Response: rezerwacja
  - 200 OK
  - 404 gdy nie istnieje

- `GET /api/bookings/trainees/{traineeId}`
  - Response: lista rezerwacji kursanta
  - 200 OK

### rewards-service
Base URL: `http://localhost:8083`

- `GET /health`
  - Response: `"OK"`

- `GET /api/rewards/{traineeId}`
  - Response: konto punktow
  - 200 OK
  - 404 gdy nie istnieje

- `POST /api/rewards/earn`
  - Body:
    - `traineeId` (long, required)
    - `points` (int, required, min 1)
  - Response: konto punktow po dodaniu
  - 200 OK
  - 400 dla blednych danych
  - 422 dla naruszen biznesowych

- `POST /api/rewards/lottery`
  - Body:
    - `traineeId` (long, required)
  - Walidacje:
    - koszt losowania 10 punktow
  - Response: konto punktow po losowaniu
  - 200 OK
  - 404 gdy brak konta
  - 422 gdy brak punktow

- `POST /api/rewards/coin-flip`
  - Body:
    - `traineeId` (long, required)
    - `wagerPoints` (int, required, min 1)
  - Walidacje:
    - nie mozna postawic wiecej niz sie ma
  - Response: konto punktow po grze
  - 200 OK
  - 404 gdy brak konta
  - 422 gdy brak punktow

## Logowanie
Serwisy loguja zdarzenia biznesowe (utworzenie uzytkownika, rezerwacje, przyznanie punktow).


TEST IF IT WORKS CORRECTLY:

USERS

curl -X POST -H "Content-Type: application/json" \
-d '{"firstName":"Jan","lastName":"Kowalski","email":"jan@example.com","role":"TRAINEE"}' \
http://localhost:8080/api/users

curl -X DELETE http://localhost:8080/api/users/1

RESERVATIONS

curl -X POST -H "Content-Type: application/json" \
-d '{"traineeId":1,"instructorId":2,"startTime":"2030-01-01T10:00:00","endTime":"2030-01-01T11:00:00"}' \
http://localhost:8082/api/bookings

curl -X POST http://localhost:8082/api/bookings/1/confirm

curl -X POST http://localhost:8082/api/bookings/1/cancel

GAMBLING

curl -X POST -H "Content-Type: application/json" \
-d '{"traineeId":1,"points":20}' \
http://localhost:8083/api/rewards/earn


curl -X POST -H "Content-Type: application/json" \
-d '{"traineeId":1}' \
http://localhost:8083/api/rewards/lottery


curl -X POST -H "Content-Type: application/json" \
-d '{"traineeId":1,"wagerPoints":20}' \
http://localhost:8083/api/rewards/coin-flip
