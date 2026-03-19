package com.experian.custodia.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.experian.custodia.infrastructure.persistence.entity.CustodiaDocumentosErrorEntity;
import com.experian.custodia.infrastructure.persistence.entity.CustodiaDocumentosErrorPK;

public interface CustodiaDocumentosErrorRepository extends JpaRepository<CustodiaDocumentosErrorEntity, CustodiaDocumentosErrorPK>{

}
