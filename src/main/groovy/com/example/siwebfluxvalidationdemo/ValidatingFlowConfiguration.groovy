package com.example.siwebfluxvalidationdemo

import groovy.transform.ToString
import org.reactivestreams.Publisher
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
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Flux

import javax.validation.constraints.NotEmpty

@EnableIntegration
@EnableWebFlux
@Configuration
class ValidatingFlowConfiguration {

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
                .log()
                .map { "Hello, ${it.name}" as String }
    }
}

@ToString(includeNames = true)
class HelloRequest {

    @NotEmpty
    String name
}
