<div align="center">
    <h3>BOOKSTER</h3>
    <p>Bookster is an virtual library, where users can review books and track what books they have read.</p>
<br>
<!-- <a href="www.bookster.com">VIEW DEMO</a> -->
</div>

![Screenshot][Screenshot]

## TECH STACK

* [![Java][Java-badge]][Java-url]
* [![Spring][Spring-badge]][Spring-url]
* [![Spring Boot][Spring-boot-badge]][Spring-boot-url]
* [![Spring Security][Spring-security-badge]][Spring-security-url]
* [![Thymeleaf][Thymeleaf-badge]][Thymeleaf-url]
* [![Hibernate][Hibernate-badge]][Hibernate-url]
* [![PostgreSQL][Postgres-badge]][Postgres-url]
* [![JUnit5][Junit-badge]][Junit-url]
* [![HTMX][HTMX-badge]][HTMX-url]

## GETTING STARTED

1. Clone this repo

```shell
git clone https://github.com/nerfi58/bookster.git
```

2. Create new Database

3. Edit `src/main/resources/application.properties` file

```properties
# CHANGE THIS TO YOUR DATABASE CREDENTIALS
spring.datasource.username=postgres
spring.datasource.password=postgres

# CHANGE THIS TO YOUR DATABASE URL
spring.datasource.url=jdbc:postgresql://localhost:5432/bookster

# CHANGE THIS TO MATCH YOUR MAIL SMTP SETTINGS 
# THIS MAIL WILL BE USED FOR SENDING VERIFICATION MAILS FOR NEW USER REGISTRATIONS
spring.mail.host=smtp@gmail.com
spring.mail.port=587

# CHANGE THIS TO MATCH YOUR MAIL CREDENTIALS
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
```

4. Install it

```shell
cd bookster
mvn clean install
```

## HOW TO RUN THIS

1. Run Spring Boot application

```shell
cd bookster
mvn spring-boot:run
```

2. Open browser and enter `localhost:8080`

## ACCOUNTS CREDENTIALS

App comes with three predefined accounts

```
User account
username: user
password: user

Moderator account
username: moderator
password: moderator

Admin account
username: admin
password: admin
```

[Screenshot]: images/screenshot.png

[Java-badge]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white

[Java-url]: https://www.java.com/

[Spring-badge]: https://img.shields.io/badge/spring-6DB33F?logo=spring&style=for-the-badge&logoColor=white

[Spring-url]: https://spring.io/

[Spring-boot-badge]: https://img.shields.io/badge/spring%20boot-6DB33F?logo=springboot&style=for-the-badge&logoColor=white

[Spring-boot-url]: https://spring.io/projects/spring-boot

[Spring-security-badge]: https://img.shields.io/badge/spring%20security-6DB33F?logo=springsecurity&style=for-the-badge&logoColor=white

[Spring-security-url]: https://spring.io/projects/spring-security

[Thymeleaf-badge]: https://img.shields.io/badge/thymeleaf-005f05?logo=thymeleaf&style=for-the-badge&logoColor=white

[Thymeleaf-url]: https://www.thymeleaf.org/

[Hibernate-badge]: https://img.shields.io/badge/hibernate-59666C?logo=hibernate&style=for-the-badge&logoColor=white

[Hibernate-url]: https://hibernate.org/

[Postgres-badge]: https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&style=for-the-badge&logoColor=white

[Postgres-url]: https://www.postgresql.org/

[Junit-badge]: https://img.shields.io/badge/JUNIT5-25A162?logo=junit5&style=for-the-badge&logoColor=white

[Junit-url]: https://junit.org/junit5/

[HTMX-badge]: https://img.shields.io/badge/HTMX-3366CC?logo=htmx&style=for-the-badge&logoColor=white

[HTMX-url]: https://htmx.org/