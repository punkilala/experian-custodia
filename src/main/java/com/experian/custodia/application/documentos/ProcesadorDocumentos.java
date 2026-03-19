package com.experian.custodia.application.documentos;

import com.experian.custodia.infrastructure.persistence.entity.ColaCustodiaDocumentosEntity;

public interface ProcesadorDocumentos {
	boolean aplica(String documento);
	boolean procesar(ColaCustodiaDocumentosEntity documento);
}
