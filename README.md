# spring-integration-webflux-validation

A sample showing how to validate POSTed data in a Spring Integration route


Start it up by running `./mvnw spring-boot:run`.

Try it by calling:

```bash
❯ http POST http://localhost:8080/greet foo=bar     
HTTP/1.1 400 Bad Request
Connection: keep-alive
Content-Length: 12868
Content-Type: application/json;charset=UTF-8

{
    "error": "Bad Request",
    "message": "org.springframework.validation.BeanPropertyBindingResult: 1 errors\nField error in object 'request' on field 'name': rejected value [null]; codes [NotEmpty.request.name,NotEmpty.name,NotEmpty.java.lang.String,NotEmpty]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [request.name,name]; arguments []; default message [name]]; default message [must not be empty]",
    "path": "/greet",
    "status": 400,
    "timestamp": "2019-06-24T23:23:50.864+0000",
    "trace": "..."
}

```

```bash
❯ http POST http://localhost:8080/greet name=someone
HTTP/1.1 200 OK
Connection: keep-alive
Content-Type: application/json
transfer-encoding: chunked

[
    "Hello, someone"
]

```
