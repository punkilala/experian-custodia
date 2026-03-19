package com.experian.custodia.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustodiaDocumentRequest {
	private String queryId;
	private String documentCode;
	private String notificationId;
}
