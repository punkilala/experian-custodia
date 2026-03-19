package com.experian.custodia.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrquestadorWebhookEvent {
    private String queryId;
    private String notificationId;
    private String origen;
    private String eventType;
    private CustodiaDocumentResponse custodiaDocument;
}
