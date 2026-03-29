package com.experian.custodia.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustodiaResultEvent {
    private String queryId;
    private String notificationId;
    private String eventType;
    private CustodiaResultEventData eventData;
}
