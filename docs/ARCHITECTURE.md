# Architektura backendu

Projekt jest modularnym monolitem w stylu DDD + Hexagonal Architecture.

## Bounded contexts

```text
com.example.springboot_backend
├── account
├── event
├── favorite
├── notification
└── shared
```

## Wzorzec w każdym kontekście

```text
context
├── api             # adapter wejściowy REST
├── application     # przypadki użycia, command/query, DTO, porty integracyjne
├── domain          # model domenowy, value objects, repozytoria jako porty, domain services, events
├── infrastructure  # adaptery wyjściowe: JPA, sender, hasher, token generator, scraper demo
└── mapper          # mapowanie domain -> DTO
```

## Najważniejszy przepływ: rejestracja i logowanie

```mermaid
sequenceDiagram
    actor User
    participant REST as AuthController
    participant APP as RegistrationApplicationService
    participant DOMAIN as UserAccount + PasswordPolicy
    participant REPO as UserAccountRepository
    participant INFRA as BCrypt/Token/Mail adapters

    User->>REST: POST /api/auth/register
    REST->>APP: RegisterUserCommand
    APP->>DOMAIN: validate email/password
    APP->>INFRA: hash password + generate token
    APP->>REPO: save account
    APP->>INFRA: send verification link
    APP-->>REST: RegisterResponse
```

## Przepływ wydarzeń

```mermaid
sequenceDiagram
    participant Admin
    participant REST as EventController
    participant Fetch as FetchEventsApplicationService
    participant Norm as NormalizeEventsApplicationService
    participant Scraper as DemoEventScraperAdapter
    participant Repo as RawEventRepository/EventRepository

    Admin->>REST: POST /api/admin/events/fetch
    REST->>Fetch: fetchFromScraper()
    Fetch->>Scraper: scrapeEvents()
    Fetch->>Repo: save RawEvents
    Admin->>REST: POST /api/admin/events/normalize
    REST->>Norm: normalize()
    Norm->>Repo: load RawEvents
    Norm->>Repo: save Events
```

## Integracja między kontekstami

Konteksty nie sięgają bezpośrednio do obcych tabel. Komunikują się przez porty aplikacyjne:

- `AccountAccessPort` — favorite/event/notification pytają o użytkownika i sesję.
- `EventAvailabilityPort` — favorite/notification pytają, czy wydarzenie jest dostępne.
- `FavoriteEventsAccessPort` — notification sprawdza, czy event jest w ulubionych.
