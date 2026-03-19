package com.experian.custodia.infrastructure.persistence.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustodiaDocumentosErrorPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String queryId;
    private String documentCode;
}
