package com.experian.custodia.application;

import org.springframework.stereotype.Service;

import com.experian.custodia.infrastructure.dto.CustodiaDocumentRequest;
import com.experian.custodia.infrastructure.persistence.repository.RepositoryProcesarCustodia;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationCustodiaDocumento {
	
	private final RepositoryProcesarCustodia repositoryProcesarCustodia;
	
	public void custodiarDocumento(CustodiaDocumentRequest request) {
		repositoryProcesarCustodia.custodiarDocumento(request);
	}
}
