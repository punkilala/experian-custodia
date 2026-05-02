package com.experian.custodia.infrastructure.kafka;


import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.experian.custodia.domain.dto.CustodiaDocumentoEvent;
import com.experian.custodia.infrastructure.exceptions.NonRetryableProcessingException;
import com.experian.custodia.infrastructure.exceptions.RetryableProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumeCustodiaDocumentos {
	
	private final ObjectMapper objectMapper;
	private final ProcesadorCustodiaDocumentos procesadorCustodiaDocumentos;
	
	@RetryableTopic(
            attempts = "100", 
            backoff = @Backoff(delay = 1000),
            exclude = {NonRetryableProcessingException.class},
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            autoCreateTopics = "true"
    )
    @KafkaListener(
            topics =  "documento.custodia.orden",
            groupId = "custodia-experian"
    )
    public void listener(String mensaje,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String queryId,
            Acknowledgment ack) {
		
		try {
			CustodiaDocumentoEvent documento = objectMapper.readValue(mensaje, CustodiaDocumentoEvent.class);
			procesadorCustodiaDocumentos.procesar(documento);
			
			ack.acknowledge();

		} catch ( KafkaException e) {
	        log.error("Error Kafka procesando evento Experian", e);
	        throw new RetryableProcessingException("Error Kafka procesando evento Experian", e);
	    }catch (RetryableProcessingException | NonRetryableProcessingException e) {
	        throw e;
	    } catch (Exception e) {
	        log.error("Error inesperado procesando evento Experian", e);
	        throw new RetryableProcessingException("Error inesperado procesando evento Experian", e);
	    }
    }
	
	@DltHandler
    public void dlt(
    		String mensaje,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String queryId,
            @Header(name = "kafka_exception-message", required = false) byte[] exceptionMessage,
            @Header(name = "kafka_exception-stacktrace", required = false) byte[] exceptionStacktrace) {
    	
		log.error("Error inesperado procesando evento Experian");

    }
		
}
