# spring-integration-webflux-validation

A sample showing how to validate POSTed data in a Spring Integration route


Start it up by running `./mvnw spring-boot:run`.

Try it by calling:


```bash
http POST http://localhost:8080/greet foo=bar  # returns validation error
http POST http://localhost:8080/greet name=someone # returns greeting 
```
