package com.experian.custodia.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.experian.custodia.application.ApplicationCustodiaDocumento;
import com.experian.custodia.infrastructure.dto.CustodiaDocumentRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping("/custodia")
@RestController
@RequiredArgsConstructor
public class CustodiaController {

	private final ApplicationCustodiaDocumento applicationCustodiaDocumento;
	
	@PostMapping("/documento")
	public ResponseEntity<Void> custodiaDocumento (@RequestBody CustodiaDocumentRequest request){
		applicationCustodiaDocumento.custodiarDocumento(request);
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}

}
