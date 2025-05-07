## Даталогическая модель вспомогательных сущностей базы данных

```mermaid
erDiagram
    key_storage {
        key text PK
        value text
    }

    events {
        id text PK
        type text 
        payload jsonb
        created_at timestamp 
    }
```