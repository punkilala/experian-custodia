package com.experian.custodia.infrastructure.persistence.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CUSTODIA_DOCUMENTOS_ERROR")
@IdClass(CustodiaDocumentosErrorPK.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustodiaDocumentosErrorEntity {
	@Id
    @Column(name = "QUERY_ID", nullable = false, length = 60)
    private String queryId;

    @Id
    @Column(name = "DOCUMENT_CODE", nullable = false, length = 100)
    private String documentCode;

    @Column(name = "INTENTOS", nullable = false)
    private Integer intentos;

    @Column(name = "ERROR_CODE", length = 50)
    private String errorCode;

    @Lob
    @Column(name = "ERROR_MENSAJE")
    private String errorMensaje;

    @Column(name = "FECHA_ERROR_FINAL", nullable = false)
    private OffsetDateTime fechaErrorFinal;

}
