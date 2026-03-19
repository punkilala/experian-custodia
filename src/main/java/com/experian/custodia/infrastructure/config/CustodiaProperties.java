package com.experian.custodia.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api")
public class CustodiaProperties {

    private Orquestador orquestador;
    
    @Getter
    @Setter
    public static class Orquestador{
    	private String baseUrl;
    	private String eventosUrl;
    	private Integer timeoutRequest;
    	private Integer timeoutResponse;
    }
}