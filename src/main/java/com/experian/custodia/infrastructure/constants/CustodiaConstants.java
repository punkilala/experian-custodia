package com.experian.custodia.infrastructure.constants;


public final class CustodiaConstants {
	private CustodiaConstants() {
		throw new UnsupportedOperationException(" no instanciable");
	}
	
	public static final String STATUS_CUSTODIA_OK = "CUSTODIA_OK";
	public static final String STATUS_CUSTODIA_KO = "CUSTODIA_KO";
	public static final String ORIGEN_CUSTODIA = "CUSTODIA";
	public static final String EVENT_TYPE = "Custodia_Documento";
	public static final String EVENT_STATUS = "documento_procesado";
	public static final String PTE_CUSTODIA = "PTE_CUSTODIA";
	public static final String PTE_ENVIO = "PTE_ENVIO";

}
