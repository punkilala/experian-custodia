package com.experian.custodia.infrastructure.webclient;


import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.experian.custodia.infrastructure.config.CustodiaProperties;
import com.experian.custodia.infrastructure.dto.OrquestadorWebhookEvent;
import com.experian.custodia.infrastructure.exceptions.WebclientErrorMapper;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class OrquestadorEventosClient {
	
	private final WebClient webClient;
	private final CustodiaProperties props;
	
	public void reenviarEvento(OrquestadorWebhookEvent response) {
		webClient.post()
			.uri(props.getOrquestador().getBaseUrl() + props.getOrquestador().getEventosUrl())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(response)
			.retrieve()
			.onStatus(HttpStatusCode::isError,
				    resp -> WebclientErrorMapper.toAgoraException(resp, "Error en Custodia-experian"))
			.toBodilessEntity()
			.block();
		
	}

}
