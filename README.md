# Hotels API 

RESTful-сервис для работы с отелями: список, расширенная информация, поиск, создание, добавление удобств и гистограммы по параметрам. Документация API доступна через Swagger UI.

## Технологии

- Java 21, Maven
- Spring Boot 4 (Web MVC, Data JPA, Validation)
- Liquibase (миграции схемы)
- H2 (по умолчанию, в памяти); профили для PostgreSQL и MySQL
- springdoc-openapi (Swagger UI)
- Lombok
- Тесты: JUnit 5, Mockito, AssertJ, Spring Boot Test


## API

| Метод | Путь | Описание | 
|---|---|---|---|
| GET | `/property-view/hotels` | Список всех отелей (краткая информация) | 
| GET | `/property-view/hotels/{id}` | Расширенная информация об отеле | 
| GET | `/property-view/search` | Поиск по `name`, `brand`, `city`, `country`, `amenities` | 
| POST | `/property-view/hotels` | Создание отеля | 
| POST | `/property-view/hotels/{id}/amenities` | Добавление списка удобств к отелю | 
| GET | `/property-view/histogram/{param}` | Количество отелей по значениям параметра: `brand`, `city`, `country`, `amenities` | 



