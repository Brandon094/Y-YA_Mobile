# Diagrama de Entidad-Relación (ERD) - YÁYA

Este diagrama representa la estructura lógica, los atributos y las relaciones de cardinalidad de la base de datos de YÁYA en Supabase PostgreSQL.

---

## 📊 Diagrama Dinámico (Mermaid)

```mermaid
erDiagram
    profiles {
        uuid id PK
        varchar full_name
        varchar phone
        varchar document_id UK
        date birth_date
        text address
        varchar role
        text avatar_url
        text fcm_token
        text municipality
        boolean is_suspended
        timestamptz created_at
    }

    categories {
        uuid id PK
        varchar name UK
        text description
        varchar icon_name
    }

    services {
        uuid id PK
        uuid provider_id FK
        uuid category_id FK
        varchar title
        text description
        numeric price
        varchar estimated_time
        boolean materials_included
        numeric extra_cost
        varchar status
        integer_array working_days
        time start_time
        time end_time
        text municipality
        timestamptz created_at
    }

    availability {
        uuid id PK
        uuid provider_id FK
        integer day_of_week
        time start_time
        time end_time
    }

    requests {
        uuid id PK
        uuid client_id FK
        uuid service_id FK
        text request_description
        text service_address
        timestamptz scheduled_date
        varchar status
        numeric final_price
        timestamptz created_at
    }

    ratings {
        uuid id PK
        uuid request_id FK
        uuid client_id FK
        uuid provider_id FK
        integer score
        text comment
        timestamptz created_at
    }

    messages {
        uuid id PK
        uuid sender_id FK
        uuid receiver_id FK
        text content
        boolean is_read
        timestamptz sent_at
    }

    reports {
        uuid id PK
        uuid reporter_id FK
        uuid reported_user_id FK
        text reason
        timestamptz created_at
    }

    service_images {
        uuid id PK
        uuid service_id FK
        text image_url
        timestamptz created_at
    }

    %% Relaciones
    profiles ||--o{ services : "ofrece"
    categories ||--o{ services : "clasifica"
    profiles ||--o{ availability : "define"
    profiles ||--o{ requests : "contrata (cliente)"
    services ||--o{ requests : "es solicitado"
    requests ||--o| ratings : "es calificado"
    profiles ||--o{ ratings : "califica (cliente)"
    profiles ||--o{ ratings : "recibe (prestador)"
    profiles ||--o{ messages : "envía"
    profiles ||--o{ messages : "recibe"
    profiles ||--o{ reports : "denuncia"
    profiles ||--o{ reports : "es denunciado"
    services ||--o{ service_images : "contiene"
```
## Previsualizacion del diagrama ER
[](/docs/assets/DiagramER.png)

---

## 🔗 Referencia Cruzada
*   **Esquema SQL Maestro:** [DATABASE_SCHEMA.md](./DATABASE_SCHEMA.md)
*   **Diccionario de Datos:** [DATA_DICTIONARY.md](./DATA_DICTIONARY.md)

---
*Diseño Arquitectónico por BH++ Team - v1.2.0*
