package com.experian.custodia.infrastructure.worker;

import java.util.Optional;

import org.hibernate.exception.JDBCConnectionException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

import com.experian.custodia.infrastructure.persistence.entity.ColaCustodiaDocumentosEntity;
import com.experian.custodia.infrastructure.persistence.repository.RepositoryProcesarCustodia;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WorkerCustodiaDocumentos {
	
	private final RepositoryProcesarCustodia repositoryProcesarCustodia;
	private final ProcesarWorker procesarWorker;
	
	private volatile long WORKER_DORMIDO = 0;
	
	@Scheduled(fixedDelayString = "15000")
	public void custodiaDocumentos() {
		System.out.println("inicio worker");
		ColaCustodiaDocumentosEntity documento = null;
		
		if (System.currentTimeMillis() < WORKER_DORMIDO) {
	        return;
	    }
		
		try {
			
			Optional<ColaCustodiaDocumentosEntity> docOpt = repositoryProcesarCustodia.reclamar();
			
			if(docOpt.isEmpty()) {
				System.out.println("fin worker");
				return;
			}
			
			documento = docOpt.get();
			procesarWorker.procesarCustodia(documento);
			
			
		} catch (CannotCreateTransactionException | JDBCConnectionException | DataAccessResourceFailureException 
				|TransientDataAccessResourceException | BadSqlGrammarException | RecoverableDataAccessException e) {
			//caida de bdd
			log.error("BDD no disponible, pausando worker 5 minutos", e);
			WORKER_DORMIDO = System.currentTimeMillis() + 400000;
        } catch (Exception e) {
        	if(null == documento) {
        		 log.error("ERROR CUSTODIA: err inesperado en worker", e);
        		 repositoryProcesarCustodia.registrarErrorCustodia(null, e);
        	}else {
        		//se necesita saber que errores son reintentables o cuales no del gd
        		log.error("ERROR CUSTODIA: err inesperado en worker para {} - {}", documento.getQueryId(), documento.getDocumentCode(), e);
        		repositoryProcesarCustodia.reprogramarCustodia(documento, e);
        	}
        }
		
		
	}
}
