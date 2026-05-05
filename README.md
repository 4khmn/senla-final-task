# Сервис для размещения частных объявлений.

---

## Инструкция по запуску приложения

Для запуска проекта вам понадобится **Docker** и **Docker Compose**.

1. **Клонируйте репозиторий:**
   ```bash
   git clone [https://github.com/4khmn/senla-final-task.git](https://github.com/4khmn/senla-final-task.git)
   cd senla-final-task

2. **Настройте переменные окружения:**

Создайте файл .env в корневой директории (используйте .env.example как образец):


3. **Запустите контейнеры:**
   ```bash
    docker-compose up -d --build


4. **Документация API:**

После запуска Swagger UI доступен по адресу: http://localhost:8080/swagger-ui/index.html

---

## Паттерны проектирования

В проекте реализованы следующие паттерны для обеспечения масштабируемости и оптимизации ресурсов:

### 1. Strategy (GoF)
Интерфейс `FileStorageService` позволяет абстрагировать логику сохранения файлов. Благодаря этому приложение может прозрачно переключаться между локальным хранилищем (`LocalFileStorageService`) и облачными решениями (S3/Azure), не меняя бизнес-логику.

### 2. Smart Cache Flag
Оптимизация высоконагруженных запросов в сущности `Advertisement`. Вместо ресурсозатратного сравнения дат в каждом SQL-запросе, используется булев флаг `is_top` как первичный фильтр, что значительно ускоряет выборку "топовых" объявлений.

### 3. Unit of Work & Batching (PoEAA)
Для минимизации количества запросов к БД применены:
* **Batch Size:** Пакетная загрузка связанных сущностей (изображений) для решения проблемы N+1.
* **Sequence Allocation:** Настройка `allocationSize = 20` для генератора ID, что позволяет резервировать ключи пачками и снижать нагрузку на сетевой ввод-вывод.

### 4. Builder (Lombok / GoF)
Используется для создания сложных объектов DTO и Entity. Это исключает появление "магических" конструкторов с десятком аргументов и делает код создания объектов более надежным.

---

## Планы развития проекта
- **Email Confirmation:** Подтверждение регистрации через почту (`spring-boot-starter-mail`).
- **Notification System:** Уведомления владельцев при модерации или блокировке объявлений.
-  **Nested Categories:** Поддержка вложенности категорий (древовидная структура).
-  **Favorites:** Возможность добавлять объявления в раздел «Избранное».

---

## Структура каталогов
```text
├── diagram            # ER-диаграммы и схемы БД
├── src                # Исходный код (Controller, Service, Repository, Entity)
├── uploads            # Файловое хранилище (игнорируется Git)
│   ├── advertisements # Фотографии товаров
│   └── avatars        # Аватары пользователей
└── docker-compose.yml # Описание инфраструктуры
```
---

## Примеры работы с API (кратко)

1. **Регистрация пользователя**

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user1",
  "password": "strong_password",
  "email": "email@google.com",
  "firstName": "firstName",
  "lastName": "lastName"
}
```

2. **Логин и получение JWT**

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "strong_password"
}
```

В ответе придёт объект `AuthResponse` с полем `token`. Далее все защищённые запросы выполняются с заголовком:

```http
Authorization: Bearer <jwt-токен>
```

3. **Получение списка товаров (публично)**

```http
GET /api/advertisements
```

4. **Доступ к защищённым ресурсам**

```http
GET /api/profiles/my
Authorization: Bearer <jwt-токен>
```
