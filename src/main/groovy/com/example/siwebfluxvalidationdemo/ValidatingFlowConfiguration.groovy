package com.example.siwebfluxvalidationdemo

import groovy.transform.ToString
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ResolvableType
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.FluxMessageChannel
import org.springframework.integration.config.EnableIntegration
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.webflux.dsl.WebFlux
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.validation.annotation.Validated
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Flux

import javax.validation.constraints.NotEmpty

@EnableIntegration
@Configuration
class ValidatingFlowConfiguration {

  @Autowired
  Validator validator

  @Bean
  Publisher<Message<String>> helloFlow() {
    IntegrationFlows
        .from(
            WebFlux
                .inboundGateway("/greet")
                .requestMapping { m ->
                  m
                      .methods(HttpMethod.POST)
                      .consumes(MediaType.APPLICATION_JSON_VALUE)
                }
                .requestPayloadType(ResolvableType.forClassWithGenerics(Flux, HelloRequest))
                .requestChannel(greetingInputChannel())


        )

        .toReactivePublisher()
  }

  @Bean
  MessageChannel greetingInputChannel() {
    return new FluxMessageChannel()
  }

  @ServiceActivator(
      inputChannel = "greetingInputChannel"
  )
  Flux<String> greetingHandler(Flux<HelloRequest> seq) {
    seq
        .doOnNext { HelloRequest it -> validate(it) }
        .log()
        .map { "Hello, ${it.name}" as String }
  }

  void validate(HelloRequest request) {
    Errors errors = new BeanPropertyBindingResult(request, "request")
    validator.validate(request, errors);
    if (errors.hasErrors()) {
      throw new ServerWebInputException(errors.toString());
    }
  }
}

@ToString(includeNames = true)
@Validated
class HelloRequest {

  @NotEmpty
  String name
}
