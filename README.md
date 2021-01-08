# README

### Что использовал
- Язык программирования: Java (Spring boot, Junit)
- База данных: PostgreSQL

### Инструкция по запуску
1. [Скачать](https://github.com/MartellX/avito-tech-task/releases/latest) последний релиз
2. Распаковать архив в любую директорию
3. Прописать в `docker-compose.yml` для PostgreSQL сервера: адрес, порт, название бд, логин и пароль пользователя
4. Для самого сервера прописать порт в `SERVER_PORT` и в `ports`:

![Пример](https://i.imgur.com/apbbtE5.png)

5. Запустить сервер командой `docker compose up`
6. Немного подождать
7. Использовать

P.S. В файле initDB находятся запросы для создания таблиц в бд

### Структура запросов
Метод | Путь | Описание | Пример запроса | Пример ответа
------|------|----------|----------------|--------------
POST|/rooms|Добавляет номер отеля и возвращает id созданной комнаты `room_id` <br><hr> Принимает:<br><li>`description` - описание номера</li><br><li>`cost` - цена комнаты</li>| `curl -X POST "http://localhost:8889/rooms" -d "description=Test room" -d "cost=123`|``` {"room_id": 77}```|
DELETE|/rooms|Удаляет номер отеля по параметру `room_id` и возвращает статус| `curl -X DELETE "http://localhost:8889/rooms?room_id=1"` | ```{"status":"OK","message":"Deleted successfully"} ``` |
GET|/rooms|Возвращает список комнат.<br><hr> Необязательные параметры: <br><ul> <li>`sorting`: <br> `1` - сортирует по цене (по умолчанию) <br> `2` - сортирует по дате добавления</li><br> <li>`isDescending`: `false` - по возрастанию (по умолчанию) <br> `true` - по убыванию</li> </ul>| `curl -X GET "http://localhost:8889/rooms"` | ```[{"description":"Test 3","cost":100.0,"room_id":5},{"description":"Test 1","cost":254.0,"room_id":3},{"description":"Test 2","cost":1200.0,"room_id":4}] ```
-|-|По дате и по убыванию | `curl -X GET "http://localhost:8889/rooms?sorting=2&isDescending=true"`|```[{"description":"Test 3","cost":100.0,"room_id":5},{"description":"Test 2","cost":1200.0,"room_id":4},{"description":"Test 1","cost":254.0,"room_id":3}] ```
POST|/bookings|Добавляет бронь<br><hr>Принимает:<br><li>`room_id` - номер комнаты, на которую заявлена бронь (должна существовать)</li><br><li>`date_start` - дата начала брони (yyyy-MM-dd)</li><br><li>`date_end` - дата окончания брони (yyyy-MM-dd). Не должна быть раньше `date_start` </li><hr>Возвращает id созданной брони `booking_id`| `curl -X POST "http://localhost:8889/bookings" -d "room_id=3" -d "date_start=2020-01-15" -d "date_end=2020-01-20`|`{"booking_id":2}`
DELETE|/bookings|Удаляет бронь по параметру `booking_id` и возвращает статус| curl -X DELETE "http://localhost:8889/?booking_id=1"| `{"status":"OK","message":"Deleted successfully"}`
GET|/bookings|Возвращает отсортированный по дате начала список броней, привязанных к комнате `room_id` <hr>Необязательные параметры: <li>`isDescending`: `false` - по возрастанию (по умолчанию) <br> `true` - по убыванию</li> | `curl -X GET "http://localhost:8889/bookings?room_id=4"`|`[{"booking_id":5,"date_start":"2021-01-11","date_end":"2021-01-24"},{"booking_id":3,"date_start":"2021-01-14","date_end":"2021-01-19"},{"booking_id":4,"date_start":"2021-01-17","date_end":"2021-01-19"}]`

### Примечания

- Т.к. юнит-тесты я на практике никогда не писал, получились они не совсем юнит, да и не полностью охватывают программу, так что как есть
- Для ошибок создал отдельный класс и вроде все отлавливаются и возвращаются в JSON в таком виде:
```json
{
  "status": "Http status",
  "message": "Error message"
}
```
- Если использовать `localhost`, а не внешний ip, в переменной окружения `POSTGRES_URL` , сервер не может подключится к базе данных из-за особенностей работы Docker (а я долго не понимал, что не так) )
- Думал насчет использования Postgres в docker-compose, но т.к. в задании явно про это не было написано, решил что бд будет внешним
