Project C.L.A.R.A
=======================
A semester project following Domain Driven Design with Spring Data JPA, based on the sample project "spring-ddd-bank" by Christoph Knabe.



This project uses

- JDK 8
- Maven 3
- Spring Boot
- Spring Data + JPA + Hibernate + Derby
- JUnit 4
- The Exception Handling and Reporting Framework MulTEx

Detailed version indications you can find in the file `pom.xml`.

By  `mvn clean test`   all necessary libraries will be fetched, the project will be compiled, exception message texts will be collected and the test suite will be executed.



## Which DDD principles are implemented?

- Modelling the domain layer as one package, which does not depend on any other package besides standard Java SE packages as `java.time` and `javax.persistence`. The latter only for the JPA annotations.

- The Domain Layer references required services only by self-defined, minimal interfaces (in package `domain.imports`).

- Implementing required services in the infrastructure layer (in package `infrastructure`).

- Linking together required services and their implementations by Dependency Injection. 

- Implementing an interface layer for external access to the application. 
  This is implemented as a REST service in package `rest_interface`.


## Other Characteristics

- It is a little online shop application, where the user can search for articles, add them to their shoppingcard. etc.
- The analysis class diagram is in file `documents/ClaraModel.pdf`. Its editable source by UMLet has the extension `.uxf`.
- Tests are run against an empty in-memory Derby database.
- Generation of a test coverage report by the [JaCoCo Maven plugin](http://www.eclemma.org/jacoco/trunk/doc/maven.html) into [target/site/jacoco-ut/index.html](file:target/site/jacoco-ut/index.html).

### Where are the exception message texts?
In the file `MessageText.properties`. The editable original with some fixed message texts is in `src/main/resources/`.
By Maven phase `compile` this file is copied to `target/classes/`.
During the following phase `process-classes` the exception message texts are extracted from the JavaDoc comments of all exceptions under `src/main/java/`
by the  `ExceptionMessagesDoclet`  as configured for the `maven-javadoc-plugin`. They are appended to the message text file in `target/classes/`.
This process is excluded from m2e lifecycle mapping in the `pom.xml`.



## References and Sources
- [The spring-ddd-bank by Christoph Knabe](https://github.com/ChristophKnabe/spring-ddd-bank), from where this is based on.
- [The Ports and Adapters Pattern](http://alistair.cockburn.us/Hexagonal+architecture)
- [Can DDD be Adequately Implemented Without DI and AOP?](https://www.infoq.com/news/2008/02/ddd-di-aop)
- [Spring Boot](https://spring.io/guides/gs/spring-boot/)
- [Spring Dependency Injection](http://projects.spring.io/spring-framework/)
- [Spring Data JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Spring Data JPA Query Methods](http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
