# Combuddy
Combuddy (Company buddy) - это приложение для поиска единомышленников. Каждый может сделать пост в ленте, описав предметную область с помощью тегов.
Данный проект - реализация Rest API приложения. Вызовы к API [доступны через Postman](https://www.postman.com/satellite-technologist-54574909/workspace/combuddy/overview). Во всех методах, требующих авторизацию, она установлена для пользователя **moderator** на практически неограниченный срок

## Ключевые технологии:
- Spring Data Jpa
- PostgreSQL
- Criteria API
- Spring MVC
- Spring Security
- Spring Boot Test

## Диаграмма базы данных:

![image](https://github.com/GreatMimperator/Combuddy/assets/93261336/54e3c383-d533-4314-b7b2-2f739c539eed)


## Сборка
```bash
git clone https://github.com/GreatMimperator/Combuddy.git
cd Combuddy
chmod +x gradlew
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Профиля два: **dev** и **prod**. Перед запуском следует в ```application-{профиль}.properties``` поместить актуальные данные базы данных, а также вызвать ```CREATE EXTENSION pgcrypto``` в **psql** для работы метода генерации соли

## Система Аутентификации

Аноним имеет доступ только к путям ```/api/v1/user/auth/**```  

Остальные пути защищены с помощью **Spring Security** - требуется **Bearer JWT токен** в заголовке **Authentication** http запроса.

Аноним может зарегистрироваться через запрос ```/api/v1/user/register/{username}``` с параметром запроса ```password```.  

Пароль будет закодирован через ```BCrypt```, а значит даже при компрометации базы данных пароли нельзя будет подобрать через радужные таблицы, потому что для каждого пароля предусмотрена своя "соль"

Запрос регистрации вернет **токены доступа** и **обновления**. Первый потребуется для доступа ко всем остальным API методам, а второй - для получения продленной версии первого.

В целях безопасности при повторном использовании **токена обновления** потребуется повторная аутентификация с помощью пароля. Также предусмотрен выход из приложения, делающий недействительными **токен обновления**

Токены подписываются ключами, размещенными в ```src/main/resources/rsa```. Следует сгенерировать новые для работы в рабочей среде

## Структура проекта

В **ru.combuddy.backend** присутствуют следующие пакеты:
- **entities**, содержит тематические (по главной сущности) пакеты с классами, представляющими в виде объекта таблицу (**Hibernate ORM**)
- **repositories**, содержит соответствующие табличным сущностям репозитории
- **controllers**, содержит контроллеры для каждой сущности, а также сервисные классы с интерфейсами, которые, в свою очередь, используют репозитории сущностей. Также здесь размещаются модельные объекты, которыми идет обмен с пользователем
- **exceptions**, содержит исключения для сервисных методов, помеченные с помощью @ResponseStatus для исключения необходимости их ловить для формирования исключения
- **security**, содержит настройки безопасности, а также несколько пар сущность-репозиторий, напрямую относящихся к безопасности. Пакет **verifiers** играет важную роль, предоставляя сервисы для проверки прав пользователей на некоторые действия

Интеграционные тесты представлены в виде пары сервиса из **queries**, осуществляющий сами запросы, и тестировщика из **controllers**

Не все сущности еще задейстованы, а именно пока что не реализованы сервисы обмена сообщениями и жалоб на пользователей
