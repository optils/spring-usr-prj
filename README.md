# Sample Spring REST Project

## User Service

### POST http://localhost:8000/api/users
```
POST http://localhost:8000/api/users
Content-Type: application/json
Request Body:
{
  "vorname": "tim",
  "name": "user",
  "email": "tim.user@gmail.com"
}

Response Http Status: 201
Response Body:
{
  "id": 1,
  "vorname": "tim",
  "name": "user",
  "email": "tim.user@gmail.com"
}
```

### GET http://localhost:8000/api/users
```
GET http://localhost:8000/api/users
Accept: application/json

Response Http Status: 200
Response Body:
[
  {
    "id": 1,
    "vorname": "tim",
    "name": "user",
    "email": "tim.user@gmail.com"
  }
]
```

### PUT http://localhost:8000/api/users/1
```
PUT http://localhost:8000/api/users/1
Content-Type: application/json
Request Body:
{
  "id": 1,
  "vorname": "tim",
  "name": "user2",
  "email": "tim.user2@gmail.com"
}

Response Http Status: 200
Response Body:
{
  "id": 1,
  "vorname": "tim",
  "name": "user2",
  "email": "tim.user2@gmail.com"
}
```

### GET http://localhost:8000/api/users/1
```
GET http://localhost:8000/api/users/1
Accept: application/json

Response Http Status: 200
Resposne Body:
{
  "id": 1,
  "vorname": "tim",
  "name": "user2",
  "email": "tim.user2@gmail.com"
}
```

### DELETE http://localhost:8000/api/users/1
```
DELETE http://localhost:8000/api/users/1

Response Http Status: 200
```

### GET http://localhost:8000/api/users/findByVorname/tim
```
GET http://localhost:8000/api/users/findByVorname/tim
Accept: application/json

Response Http Status: 200
Response Http Body:
[
  {
    "id": 1,
    "name": "user2",
    "vorname": "tim",
    "email": "tim.user2@gmail.com"
  }
]
```

# TODO

 - documentation

[![Java CI with Maven](https://github.com/timpr0/spring-usr-prj/actions/workflows/maven.yml/badge.svg)](https://github.com/timpr0/spring-usr-prj/actions/workflows/maven.yml)
