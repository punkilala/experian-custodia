package com.experian.custodia.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.experian.custodia.infrastructure.persistence.entity.ColaCustodiaDocumentosEntity;
import com.experian.custodia.infrastructure.persistence.entity.ColaCustodiaDocumentosPK;
import java.util.Optional;




public interface ColaCustodiaDocumentosRepository extends JpaRepository<ColaCustodiaDocumentosEntity, ColaCustodiaDocumentosPK> {
	Optional<ColaCustodiaDocumentosEntity> 
	findByQueryIdAndDocumentCodeAndNotificationId(
	        String queryId,
	        String documentCode,
	        String notificationId);
}
