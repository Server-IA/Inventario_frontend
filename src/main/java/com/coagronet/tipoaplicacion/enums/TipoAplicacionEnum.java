package com.coagronet.tipoaplicacion.enums;

public enum TipoAplicacionEnum {

	WEB(1), MOVIL(2);

	private final int id;

	TipoAplicacionEnum(int id) {
		this.id = id;
	}

	public int id() {
		return id;
	}

	public static TipoAplicacionEnum from(String raw) {
		return switch (raw.trim().toLowerCase()) {
		case "web" -> WEB;
		case "movil" -> MOVIL;
		default -> throw new IllegalArgumentException("tipoAplicacion inválido. Use: web | movil");
		};
	}

}
