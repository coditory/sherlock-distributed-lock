# Databases

A [Docker](https://www.docker.com) composition with databases used by sherlock.

## Sample usage

```
docker-compose up
```

## Important values

### Mongo
- Connection string: `mongodb://localhost:27017`

### MySql
- database: test
- username: mysql
- password: mysql
- Jdbc connection: `jdbc:mysql://localhost:3306/test`

### PostgreSQL
- database: test
- username: postgres
- password: postgres
- Jdbc connection: `jdbc:postgresql://localhost:5432/test`
