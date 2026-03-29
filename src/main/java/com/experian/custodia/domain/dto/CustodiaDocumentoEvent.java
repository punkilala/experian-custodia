package com.experian.custodia.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustodiaDocumentoEvent {
    private String queryId;
    private String notificationId;
    private String documentCode;
    private Integer intentos;
    private byte[] binDocumento;
    private boolean documentoCustodiado;
}
