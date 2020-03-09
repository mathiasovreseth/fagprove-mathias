# :computer: Fagprøve for Mathias

## Start database

```bash
sudo docker-compose up
```

### Database info

Parameter | Verdi
------------ | -------------
Host | localhost
Port | 6000
User | fagprove
Passord | mathias
Database | fagprove

## Start backend

```bash
cd backend
grails run-app
```

## Postman

I Postman:

1. Trykk import
2. Velg fila `Fagprove.postman_collection.json`

## API dokumentasjon

Opne `index.html` i `api-docs` mappa
