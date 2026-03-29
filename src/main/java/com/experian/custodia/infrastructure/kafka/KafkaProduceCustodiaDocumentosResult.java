package com.experian.custodia.infrastructure.kafka;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.experian.custodia.domain.dto.CustodiaDocumentoEvent;
import com.experian.custodia.infrastructure.dto.CustodiaResultEvent;
import com.experian.custodia.infrastructure.dto.CustodiaResultEventData;
import com.experian.custodia.infrastructure.exceptions.NonRetryableProcessingException;
import com.experian.custodia.infrastructure.exceptions.RetryableProcessingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * publicar topic kafka resultado de la custodia del documento.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceCustodiaDocumentosResult {
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	
	public void publicar(CustodiaDocumentoEvent documento) {
		
		String estadoCustodia = documento.isDocumentoCustodiado() ? "CUSTODIA_OK" : "CUSTODIA_KO";

		try {
			CustodiaResultEvent event = CustodiaResultEvent.builder()
					.queryId(documento.getQueryId())
					.notificationId(documento.getNotificationId())
					.eventType("CustodiaDocumento")
					.eventData(
							CustodiaResultEventData.builder()
								.status("custodia_documento")
								.substatus(estadoCustodia)
								.documentCode(documento.getDocumentCode())
								.build()
							)
					.build();
			
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("documento.custodia.resultado", documento.getQueryId(), payload).get();
			
		} catch (JsonProcessingException e) {
			  log.error("Error serializando mensaje Kafka", e);
		      throw  new  NonRetryableProcessingException("Error serializando mensaje Kafka", e);
		}catch (KafkaException | ExecutionException | InterruptedException e) {
			if (e instanceof InterruptedException) {
    	        Thread.currentThread().interrupt();
	    	}
			log.error("Error al publicar en Kafka", e);
	        throw  new  RetryableProcessingException("Error al publicar en Kafka", e);
		}
		 
	}
}
