package com.experian.custodia.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustodiaDocumentResponse {
	private String status;
    private String substatus;
    private String documentCode;

}
