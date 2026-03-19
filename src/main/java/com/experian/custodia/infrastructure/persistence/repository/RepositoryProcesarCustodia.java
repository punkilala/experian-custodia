package com.experian.custodia.infrastructure.persistence.repository;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.experian.custodia.infrastructure.dto.CustodiaDocumentRequest;
import com.experian.custodia.infrastructure.exceptions.AgoraException;
import com.experian.custodia.infrastructure.persistence.entity.ColaCustodiaDocumentosEntity;
import com.experian.custodia.infrastructure.persistence.entity.ColaCustodiaDocumentosPK;
import com.experian.custodia.infrastructure.persistence.entity.CustodiaDocumentosErrorEntity;

import static com.experian.custodia.infrastructure.utils.CustodiaUtils.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RepositoryProcesarCustodia {
	
	private final ColaCustodiaDocumentosRepository colaCustodiaDocumentosRepository;
	private final CustodiaDocumentosErrorRepository custodiaDocumentosErrorRepository;
	
	private final JdbcTemplate template;
	
	/**
	 * reclamar documento para el worker
	 * @return
	 */
	@Transactional
	public Optional<ColaCustodiaDocumentosEntity> reclamar() {

	    OffsetDateTime ahora = OffsetDateTime.now();
	    OffsetDateTime limiteProceso = ahora.minusMinutes(10);

	    String sqlBloqueo = """
        SELECT *
        FROM COLA_CUSTODIA_DOCUMENTOS
        WHERE (QUERY_ID, DOCUMENT_CODE) IN (
            SELECT QUERY_ID, DOCUMENT_CODE
            FROM (
                SELECT QUERY_ID, DOCUMENT_CODE
                FROM COLA_CUSTODIA_DOCUMENTOS
                WHERE (
                        ESTADO  IN = ('PTE_CUSTODIA', 'PTE_ENVIO')
                     OR (ESTADO = 'IN_PROGRESS' AND PROCESO_DESDE < ?)
                )
                ORDER BY FECHA_ALTA
            )
            WHERE ROWNUM = 1
        )
        FOR UPDATE SKIP LOCKED
        """;

    String sqlUpdate = """
        UPDATE COLA_CUSTODIA_DOCUMENTOS
        SET ESTADO = 'IN_PROGRESS',
            PROCESO_DESDE = ?
        WHERE QUERY_ID = ?
          AND DOCUMENT_CODE = ?
        """;

    return template.query(
    	    sqlBloqueo,
    	    ps -> ps.setObject(1, limiteProceso),
    	    (ResultSet rs) -> {
    	        if (!rs.next()) {
    	            return Optional.empty();
    	        }

    	        ColaCustodiaDocumentosEntity doc = new ColaCustodiaDocumentosEntity();
    	        doc.setQueryId(rs.getString("QUERY_ID"));
    	        doc.setDocumentCode(rs.getString("DOCUMENT_CODE"));
    	        doc.setNotificationId(rs.getString("NOTIFICATION_ID"));
    	        doc.setEstado(rs.getString("ESTADO"));
    	        doc.setPdfBinario(rs.getBytes("PDF_BINARIO"));
    	        doc.setFechaAlta(rs.getObject("FECHA_ALTA", OffsetDateTime.class));

    	        template.update(
    	            sqlUpdate,
    	            ahora,
    	            doc.getQueryId(),
    	            doc.getDocumentCode()
    	        );

    	        return Optional.of(doc);
    	    }
    	);
		
	}
	
	/**
	 * activar un documento para que el worker lo procese
	 * Esta orden viene desde el orquestador
	 * @param request
	 */
	@Transactional
	public void custodiarDocumento (CustodiaDocumentRequest request) {
			
		ColaCustodiaDocumentosEntity entity = colaCustodiaDocumentosRepository.
				findByQueryIdAndDocumentCodeAndNotificationId(
						request.getQueryId(), 
						request.getDocumentCode(), 
						request.getNotificationId())
				.orElseThrow(()-> new AgoraException(HttpStatus.NOT_FOUND.value(), "no se puede custodiar", 
						Map.of("queryId:", request.getQueryId(), 
								"documentCode", request.getDocumentCode(),
								"notificationId", request.getNotificationId())
		));
		if ("BLOQUEADO".equals(entity.getEstado())) {
			entity.setEstado("PTE_CUSTODIA");
		}
	}
	
	/**
	 * activar documento para enviar a orquestador
	 */
	public void actualizarEvioDoc (ColaCustodiaDocumentosEntity entity) {
		colaCustodiaDocumentosRepository.save(entity);
	}
	
	/**
	 * reprogramar custodia fallida
	 * @param entity
	 * @param e
	 */
	public void reprogramarCustodia(ColaCustodiaDocumentosEntity entity, Throwable e) {
		entity.setIntentos(entity.getIntentos() +1);
		entity.setErrorMensaje(stackTraceToString(e, 15));
		entity.setNextRetry(OffsetDateTime.now().plusHours(1));
		entity.setEstado("PTE_CUSTODIA");
		
		colaCustodiaDocumentosRepository.save(entity);
		
	}
	
	/**
	 * Registrar error en tabla
	 * @param documento
	 * @param errorCode
	 */
	public void registrarErrorCustodia (ColaCustodiaDocumentosEntity documento, Throwable e) {
		CustodiaDocumentosErrorEntity entity = new CustodiaDocumentosErrorEntity();
		if (null == documento){
			entity.setQueryId("WORKER_" + UUID.randomUUID());
			entity.setDocumentCode("ERROR_TECNICO");
			entity.setIntentos(0);
		}else {
			entity.setQueryId(documento.getQueryId());
			entity.setDocumentCode(documento.getDocumentCode());
			entity.setIntentos(documento.getIntentos());
		}
		entity.setErrorCode( e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
		entity.setErrorMensaje(stackTraceToString(e, 15));
		entity.setFechaErrorFinal(OffsetDateTime.now());
		
		try {
		    custodiaDocumentosErrorRepository.save(entity);
		} catch (Exception ex) {
		    log.error("ERROR CUSTODIA error de custodia", stackTraceToString(ex, 15));
		}
		
	}
	
	/**
	 * borrar de cola de trabajo del worker
	 */
	public void borrarColaWorker (ColaCustodiaDocumentosEntity documento) {
		colaCustodiaDocumentosRepository.deleteById(
				new ColaCustodiaDocumentosPK(documento.getQueryId(), documento.getDocumentCode())
			);
		
	}

}
