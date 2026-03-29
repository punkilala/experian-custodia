package com.experian.custodia.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.experian.custodia.infrastructure.persistence.entity.DocumentoPendienteCustodiaEntity;
import com.experian.custodia.infrastructure.persistence.entity.DocumentoPendienteCustodiaPK;


public interface DocumentoPendienteCustodiaRepository extends JpaRepository<DocumentoPendienteCustodiaEntity, DocumentoPendienteCustodiaPK>{

}
