package com.experian.custodia.infrastructure.worker;

import java.util.List;

import org.springframework.stereotype.Component;

import com.experian.custodia.application.documentos.ProcesadorDocumentos;
import com.experian.custodia.infrastructure.dto.CustodiaDocumentResponse;
import com.experian.custodia.infrastructure.dto.OrquestadorWebhookEvent;
import com.experian.custodia.infrastructure.persistence.entity.ColaCustodiaDocumentosEntity;
import com.experian.custodia.infrastructure.persistence.repository.RepositoryProcesarCustodia;
import com.experian.custodia.infrastructure.webclient.OrquestadorEventosClient;
import static com.experian.custodia.infrastructure.constants.CustodiaConstants.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesarWorker {
	
	private static final int MAX_INTENTOS = 3;
	private final List<ProcesadorDocumentos> procesadores;
	private final RepositoryProcesarCustodia repositoryProcesarCustodia;
	private final OrquestadorEventosClient orquestadorEventosClient;
	
	
	public void procesarCustodia (ColaCustodiaDocumentosEntity documento) {
		if(PTE_CUSTODIA.equals(documento.getEstado()) || "IN_PROGRESS".equals(documento.getEstado())){
			procesarGuardadoGD(documento);
		}
		
		if(PTE_ENVIO.equals(documento.getEstado())){
			finalizarCustodia(documento);
		}
	}
	
	/**
	 * enviar a custodiar
	 * @param documento
	 */
	private void procesarGuardadoGD(ColaCustodiaDocumentosEntity documento) {
		if(documento.getIntentos()  >= MAX_INTENTOS) {
			actualizarEstado(documento);
			return;
		}
		
		//procesar custodia
		boolean procesado = false;
		for(ProcesadorDocumentos procesador : procesadores) {
			if(procesador.aplica(documento.getDocumentCode())) {
				procesado = procesador.procesar(documento);
				break;
			}
		}

		if(procesado) {
			documento.setResultadoCustodia(STATUS_CUSTODIA_OK);
		}else {
			//ningun procesador se ha ejecutado
			documento.setResultadoCustodia(STATUS_CUSTODIA_KO);
		}
		
		actualizarEstado(documento);
	}
	
	private void actualizarEstado(ColaCustodiaDocumentosEntity documento) {
		documento.setEstado(PTE_ENVIO);
		repositoryProcesarCustodia.actualizarEvioDoc(documento);
	}
	
	
	
	/**
	 * enviar resultado al orquestador
	 * @param documento
	 */
	private void finalizarCustodia(ColaCustodiaDocumentosEntity documento) {
		CustodiaDocumentResponse response = new CustodiaDocumentResponse();
		response.setDocumentCode(documento.getDocumentCode());
		response.setStatus(EVENT_STATUS);
		response.setSubstatus(documento.getResultadoCustodia());
		
		OrquestadorWebhookEvent event = new OrquestadorWebhookEvent();
		event.setQueryId(documento.getQueryId());
		event.setNotificationId(documento.getNotificationId());
		event.setOrigen(ORIGEN_CUSTODIA);
		event.setEventType(EVENT_TYPE);
		event.setEventData(response);
		
		orquestadorEventosClient.reenviarEvento(event);
		repositoryProcesarCustodia.borrarColaWorker(documento);

	}

}
