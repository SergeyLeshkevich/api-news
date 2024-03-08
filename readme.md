### News management system:

### Технологии, которые я использовал в проекте:

* Java 17
* Gradle 8.5
* Postgresql 15.2
* Spring-boot 3.2.2
* Spring-boot-starter-data-jpa
* Spring-boot-starter-data-redis
* Spring-boot-starter-web
* Spring-boot-starter-validation
* Spring-boot-starter-aop
* Spring-boot-starter-security
* Spring-cloud-config-server
* Spring-cloud-starter-config
* Springdoc-openapi-starter-webmvc-ui 2.3.0
* Jsonwebtoken-jjwt-api 0.12.3
* Jsonwebtoken-jjwt-impl 0.12.3
* Jsonwebtoken-jjwt-jackson 0.12.3
* Jackson-datatype-protobuf 0.9.13
* Liquibase
* Mapstruct 1.5.5.Final
* Spring-boot-starter-test
* Testcontainers-Postgresql 1.18.0

### Документация

Чтобы просмотреть документацию API Swagger, запустите приложение и перейдите:

* [http://localhost:8086/swagger-ui/index.html](http://localhost:8086/swagger-ui/index.html)
* [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html)
* [http://localhost:8085/v3/api-docs](http://localhost:8085/v3/api-docs)
* [http://localhost:8086/v3/api-docs](http://localhost:8086/v3/api-docs)
* [http://localhost:9090/swagger-ui/index.html](http://localhost:9090/swagger-ui/index.html)
* [http://localhost:8083/swagger-ui/index.html](http://localhost:8083/swagger-ui/index.html)
* [http://localhost:8083/v3/api-docs](http://localhost:8083/v3/api-docs)
* [http://localhost:9090/v3/api-docs](http://localhost:9090/v3/api-docs)

* Все ниже указанные микросервисы и стартеры расположенны каждый в своем репозитории на GitHub, где можно отследит историю
их коммитов:
- https://github.com/SergeyLeshkevich/auth-service
- https://github.com/SergeyLeshkevich/news-service
- https://github.com/SergeyLeshkevich/comment-service
- https://github.com/SergeyLeshkevich/api-gateway
- https://github.com/SergeyLeshkevich/loggingstarter
- https://github.com/SergeyLeshkevich/cachestarter
- https://github.com/SergeyLeshkevich/exceptionhandlerstarter
- https://github.com/SergeyLeshkevich/eureka-server
- https://github.com/SergeyLeshkevich/config-server
Для удобства ревьювера Все эти микроссервисы продублированы и собранны в одном репозитории:
- https://github.com/SergeyLeshkevich/News-managment-system

1. api-news Микросервис:
   - Реализован как точка входа через шлюз, используется для маршрутизации запросов.
   - Интегрирован с news-service и comment-service через WebClient для получения данных.
   - Внедрена безопасность на основе JWT токенов, с фильтрами для проверки доступа.

2. news-service Микросервис:
   - Создан для управления новостями и связанными с ними комментариями.
   - Реализованы операции CRUD для новостей и комментариев.
   - Внедрен полнотекстовый поиск для новостей и комментариев.
   - Поддерживается пагинация при просмотре новостей и комментариев.
   - Реализован контроль доступа на основе ролей и токенов JWT.

3. comment-service Микросервис:
   - Разработан для управления комментариями и их связью с новостями.
   - Предоставляет API для CRUD операций с комментариями.
   - Реализован полнотекстовый поиск для комментариев.
   - Введена пагинация и безопасность через JWT токены и роли.

4. auth-service Микросервис:
   - Служит для аутентификации пользователей и управления их данными.
   - Выдает JWT токены после успешной аутентификации.
   - Реализована проверка токенов для безопасных конечных точек.
   - Хранит информацию о пользователях и их ролях.

5. Дополнительные Компоненты:
   - Шлюз (Gateway): Осуществляет маршрутизацию запросов, используется для безопасной оркестрации взаимодействия с микросервисами.
   - Eureka: Используется для автоматической регистрации и обнаружения микросервисов, что облегчает их динамическое взаимодействие.

6. Безопасность:
   - Реализована на основе JWT токенов, с ролями для контроля доступа.
   - Разработан фильтр безопасности для шлюза, который проверяет токены.

7. Установка и Конфигурация:
   - Spring Cloud Config: Используется для централизованного управления настройками.
   - Docker и Docker Compose: Предоставляют удобные средства развертывания и масштабирования приложения.

8. Тестирование:
   - Все микросервисы покрыты юнит-тестами.
   - Написаны интеграционные тесты для слоя уровня сохранения (persistence layer) с использованием testcontainers.
   - Использован WireMock.

9. Документация:
   - Добавлены JavaDoc комментарии к коду для лучшего понимания.
   - В README.md предоставлены инструкции по настройке и запуску приложения.

10. Логирование:
    - Реализовано логирование запросов-ответов в аспектном стиле для слоя контроллеров.

11. Кеширование:
    - Реализован кеш для хранения сущностей с алгоритмами LRU и LFU.
    - Алгоритм работы с кешем определен в файле application.yml.

12. Локализация и Обработка Исключений:
    - Реализована обработка исключений согласно RESTful подходу.
    - Все настройки вынесены в файлы *.yml.

13. Документация API:
    - Использован Spring REST Docs для автоматического создания документации API.
    - Применен Swagger (OpenAPI 3.0) для удобства документирования API.

14. Контейнеризация и Docker:
    - Созданы Dockerfile для каждого микросервиса.
    - В docker-compose.yml определены настройки для развертывания приложения и базы данных в контейнерах.

15. Spring Security:
    - Реализована аутентификация с использованием JWT токенов.
    - Созданы API для регистрации пользователей с ролями admin/journalist/subscriber.
    - Реализованы правила доступа для администратора, журналиста и подписчика.

16. Кэш провайдер Redis:
    - Добавлена поддержка Redis в Docker для кэширования.
    - Использован @Profile для переключения между LRU/LFU и Redis.

17. Spring Cloud Config:
    - Настроен Spring Cloud Config для централизованного управления конфигурациями.
    - Микросервисы сконфигурированы для получения настроек в зависимости от профиля.

18. Spring Boot Starter-ы:
    - Вынесены логирование и обработка исключений в отдельные Spring Boot Starter-ы.


## Функциональность:

#### Некоторые возможности приложения:

* Комментарий успешно получен 200 (localhost:8082/api/comments/3)

```json
{
    "id": 3,
    "time": "2024-01-10T14:22:30.789",
    "text": "Comment 3",
    "user": {
        "userName": "Bob Johnson"
    },
    "newsId": 1
}
```

* Комментарий не найден 404 (localhost:8082/api/comments/1)

```json
{
    "exception": "EntityNotFoundException",
    "error_message": "Comment with 11 not found",
    "error_code": "404 NOT_FOUND"
}
```

* Комментарий успешно получен ADMIN из архива  200 (localhost:8082/api/comments/archive/1)

```json
{
    "id": 115,
    "time": "2024-01-10T22:01:52.111",
    "text": "Comment 115",
    "user": {
        "userName": "Lily White"
    },
    "newsId": 2
}
```


* Комментарии успешно получены (с пагинацией) 200 ()

```json
{
    "pageNumber": 1,
    "countPage": 56,
    "content": [
        {
            "id": 2,
            "time": "2024-01-10T14:20:15.123",
            "text": "Comment 2",
            "user": {
                "userName": "Jane Smith"
            },
            "newsId": 1
        },
        {
            "id": 3,
            "time": "2024-01-10T14:22:30.789",
            "text": "Comment 3",
            "user": {
                "userName": "Bob Johnson"
            },
            "newsId": 1
        }
    ]
}
```
* Комментарии успешно создан SUBSCRIBER 200 (localhost:8082/api/comments)

```json
{
    "id": 214,
    "time": "2024-03-04T17:36:58.383",
    "text": "Comment 1",
    "user": {
        "userName": "John Doe"
    },
    "newsId": 1
}
```

* Доступ к редактированию закрыт из-за нарушения прав 401 (localhost:8082/api/comments/2)

```json
{
    "exception": "NoAuthorizationException",
    "error_message": "Missing authorization header",
    "error_code": "401 UNAUTHORIZED"
}
```
* Комментарий обновлен его создателем (localhost:8082/api/comments/215)

```json
{
    "id": 215,
    "time": "2024-03-04T17:47:47.962",
    "text": "Update",
    "user": {
        "userName": "test6@gmail.com"
    },
    "newsId": 1
}
```
### Аналогичные возможности и многие другие есть для news

* Новость успешно получена с комментариями (с пагинацией) 200 (localhost:8082/api/news/4?pageSizeComments=1&numberPageComments=1)
```json
{
  "id": 4,
  "time": "2024-01-03T14:18:08.537",
  "title": "News 4",
  "text": "This is the text of news 4",
  "user": {
    "userName": "Jane Smith"
  },
  "comments": {
    "pageNumber": 1,
    "countPage": 10,
    "content": [
      {
        "id": 21,
        "time": "2024-01-10T15:22:05.666",
        "text": "Comment 21",
        "user": {
          "userName": "john_doe"
        },
        "newsId": 4
      }
    ]
  }
}
```
* И другие возможности

Для запуска приложения необходимо скачать этот репозиторий себе локально ПК. В root директории выполнить комманду: docker-compose up
