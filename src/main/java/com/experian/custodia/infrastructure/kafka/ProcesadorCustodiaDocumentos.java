package com.experian.custodia.infrastructure.kafka;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.experian.custodia.domain.dto.CustodiaDocumentoEvent;
import com.experian.custodia.infrastructure.persistence.repository.RepositoryCustodiaDocumento;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesadorCustodiaDocumentos {
	
	private final RepositoryCustodiaDocumento repositoryCustodiaDocumento;
	private final KafkaProduceCustodiaDocumentosResult kafkaProduceCustodiaDocumentosResult;
	
	public void procesar (CustodiaDocumentoEvent documento) {
		
		repositoryCustodiaDocumento.recuperarBinarioDocumento(documento);
		
		int intentos = Objects.requireNonNullElse(documento.getIntentos(), 4);
		
		if(intentos >= 4 || null == documento.getBinDocumento()) {
			kafkaProduceCustodiaDocumentosResult.publicar(documento);
			return;
		}
		
		//procesar custodia documento y si ha ido bien
		documento.setDocumentoCustodiado(true);
		kafkaProduceCustodiaDocumentosResult.publicar(documento);
		
	}
}
