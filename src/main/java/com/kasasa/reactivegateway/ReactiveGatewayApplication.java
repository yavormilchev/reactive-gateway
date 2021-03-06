package com.kasasa.reactivegateway;

import com.google.gson.JsonParser;
import com.kasasa.reactivegateway.repository.EndpointRepository;
import com.kasasa.reactivegateway.repository.ServiceRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ReactiveGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveGatewayApplication.class, args);
    }

    @Bean
    public ServiceRepository getServiceRepository() {
        return new ServiceRepository();
    }


    @Bean
    public EndpointRepository getEndpointRepository() {
        return new EndpointRepository();
    }

    @Bean()
    public WebClient getWebClient(){
      return WebClient.create();
    }

    @Bean
    public JsonParser getJsonParser() {
        return new JsonParser();
    }
}
