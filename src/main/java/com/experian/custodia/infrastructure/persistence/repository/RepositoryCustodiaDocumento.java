package com.experian.custodia.infrastructure.persistence.repository;

import java.util.Objects;

import org.springframework.stereotype.Repository;

import com.experian.custodia.domain.dto.CustodiaDocumentoEvent;
import com.experian.custodia.infrastructure.persistence.entity.DocumentoPendienteCustodiaEntity;
import com.experian.custodia.infrastructure.persistence.entity.DocumentoPendienteCustodiaPK;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RepositoryCustodiaDocumento {
	
	private final DocumentoPendienteCustodiaRepository documentoPendienteCustodiaRepository;
	
	/**
	 * recuperar binario del documento a custodiar
	 * @param documento
	 */
	public void recuperarBinarioDocumento (CustodiaDocumentoEvent documento) {
		DocumentoPendienteCustodiaEntity entity = documentoPendienteCustodiaRepository.findById(
					new DocumentoPendienteCustodiaPK(documento.getQueryId(), documento.getDocumentCode()))
				.orElse(new DocumentoPendienteCustodiaEntity());
		
		int intentos = Objects.requireNonNullElse(documento.getIntentos(), 0);
		
		if("DESCARGA".equals(entity.getEstatus())) {
			entity.setIntentos(1);
			entity.setEstatus("CUSTODIA");
			
			documentoPendienteCustodiaRepository.save(entity);
		}
		
		documento.setIntentos(intentos +1);
		documento.setBinDocumento(entity.getPdfDocument());
	}

}
