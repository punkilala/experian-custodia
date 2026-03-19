package com.experian.custodia.infrastructure.config;

import java.time.Duration;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
public class CustodiaConfig {
	

	@Bean
    public WebClient webClient(CustodiaProperties props) {
		CustodiaProperties.Orquestador orq = props.getOrquestador();
		
		int timeoutrequest = Optional.ofNullable(orq)
		        .map(CustodiaProperties.Orquestador::getTimeoutRequest)
		        .orElse(15000);

		int timeoutresponse = Optional.ofNullable(orq)
		        .map(CustodiaProperties.Orquestador::getTimeoutResponse)
		        .orElse(15000);

        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutrequest)
            .responseTimeout(Duration.ofMillis(timeoutresponse));

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .filter(logRequestResponse())
            .build();
    }
	
	private ExchangeFilterFunction logRequestResponse() {
	    return (request, next) -> {
	        long start = System.currentTimeMillis();

	        log.info("CustodiaToOrquestador Request: {} {} headers={}", 
	        	    request.method(), 
	        	    request.url(), 
	        	    request.headers());

	        return next.exchange(request)
	        	    .doOnNext(response -> {
	        	        long duration = System.currentTimeMillis() - start;
	        	        log.info("Custodia Response Status: {} {} ms",
	        	                 response.statusCode(),
	        	                 duration);
	        	    })
	        	    .doOnError(error -> {
	        	        long duration = System.currentTimeMillis() - start;
	        	        log.error("Custodia Error after {} ms: {}",
	        	                  duration,
	        	                  error.getMessage());
	         });
	    };
	}

}
