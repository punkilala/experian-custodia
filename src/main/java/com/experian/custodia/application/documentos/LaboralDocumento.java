package com.experian.custodia.application.documentos;

import com.experian.custodia.infrastructure.persistence.entity.ColaCustodiaDocumentosEntity;

public class LaboralDocumento implements ProcesadorDocumentos{

	@Override
	public boolean aplica(String documento) {
		return true;
	}

	@Override
	public boolean procesar(ColaCustodiaDocumentosEntity documento) {
		return true;

		
	}

}
