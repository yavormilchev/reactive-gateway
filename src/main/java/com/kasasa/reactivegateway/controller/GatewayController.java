package com.kasasa.reactivegateway.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kasasa.reactivegateway.EndpointCaller;
import com.kasasa.reactivegateway.RouteResolver;
import com.kasasa.reactivegateway.dto.route.Route;
import com.kasasa.reactivegateway.repository.EndpointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Priority;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Priority(1)
@Slf4j
public class GatewayController {

    private final RouteResolver routeResolver;
    private final EndpointCaller endpointCaller;
    private final EndpointRepository endpointRepository;
    private final JsonParser jsonParser;

    public GatewayController(RouteResolver routeResolver,
                             EndpointCaller endpointCaller,
                             JsonParser jsonParser,
                             EndpointRepository endpointRepository
    ) {
        this.routeResolver = routeResolver;
        this.endpointCaller = endpointCaller;
        this.jsonParser = jsonParser;
        this.endpointRepository = endpointRepository;
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> handle(ServerHttpRequest request) {

        log.info("request received: " + request.getMethod() + " " + request.getURI());

        Route route = routeResolver.resolve(request);

        List<Mono<String>> endpointMonoResponses = route.getServiceEndpoints().parallelStream()
                .map(serviceEndpoint -> endpointRepository.getServiceEndpoint(serviceEndpoint.getServiceId(), serviceEndpoint.getEndpointPath()))
                .map(endpointCaller::call)
                .collect(Collectors.toList());

        return Flux.mergeSequential(endpointMonoResponses)
                .collect(JsonObject::new, (jsonGatewayResponse, stringEndpointResponse) -> {
                    var newResponse = jsonParser.parse(stringEndpointResponse).getAsJsonObject();

                    newResponse.entrySet().forEach(entry ->
                            //if key is in my output for that endpoint then add the mappedToKey
                            jsonGatewayResponse.add(entry.getKey(), entry.getValue())
                    );
                })
                .map(JsonElement::toString);
    }
}
