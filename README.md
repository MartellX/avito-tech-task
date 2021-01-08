# README

### Что использовал
- Язык программирования: Java (Spring boot, Junit)
- База данных: PostgreSQL

### Инструкция по запуску
1. [Скачать]() последний релиз
2. Распаковать архив в любую директорию
3. Прописать в `docker-compose.yml` для PostgreSQL сервера: адрес, порт, название бд, логин и пароль пользователя
4. Для самого сервера прописать порт в `SERVER_PORT` и в `ports`: ![Пример](https://i.imgur.com/apbbtE5.png)
5. Запустить сервер командой `docker compose up`
6. Немного подожать
7. Использовать

### Структура запросов
Метод | Путь | Описание | Пример запроса | Пример ответа
------|------|----------|----------------|--------------
POST|/rooms|Добавляет номер отеля и возвращает id созданной комнаты `room_id` | `curl -X POST "http://localhost:8889/rooms" -d "description=Test room" -d "cost=123`|```json {"room_id": 77}```|
DELETE|/rooms|Удаляет номер отеля и возвращает статус| `curl -X DELETE "http://localhost:8889/rooms?room_id=1"` | ```json {"status":"OK","message":"Deleted successfully"} ``` |
GET|/rooms|Возвращает список комнат. Необязательные параметры <br> `sorting`: <br> `1` - сортирует по цене (по умолчанию) <br> `2` - сортирует по дате добавления<br> `isDescending`: `false` - по возрастанию (по умолчанию) <br> `true` - по убыванию | `curl -X GET "http://localhost:8889/rooms"` | ```json [{"description":"Test 3","cost":100.0,"room_id":5},{"description":"Test 1","cost":254.0,"room_id":3},{"description":"Test 2","cost":1200.0,"room_id":4}] ```
-|-|По дате и по убыванию | `curl -X GET "http://localhost:8889/rooms?sorting=2&isDescending=true"`|```json [{"description":"Test 3","cost":100.0,"room_id":5},{"description":"Test 2","cost":1200.0,"room_id":4},{"description":"Test 1","cost":254.0,"room_id":3}] ```

