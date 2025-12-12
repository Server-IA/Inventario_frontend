package com.coagronet.usuarioEstado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario_estado", uniqueConstraints = {
		@UniqueConstraint(name = "usuario_estado_descripcion_unique", columnNames = "use_descripcion") })
public class UsuarioEstado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "use_id")
	private Long id;

	@Column(name = "use_descripcion", nullable = false, length = 255)
	private String descripcion;

	// Definir estados como constantes
	public static final UsuarioEstado DESACTIVADO = new UsuarioEstado(0L, "Usuario desactivado");

	public static final UsuarioEstado PENDIENTE_VERIFICACION = new UsuarioEstado(1L,
			"Usuario registrado, pero no se ha activado el email");

	public static final UsuarioEstado ACTIVADO_SIN_INFO = new UsuarioEstado(2L,
			"Usuario activado, pero no ha llenado información personal y no se ha asociado a una empresa");

	public static final UsuarioEstado ACTIVADO_SIN_EMPRESA = new UsuarioEstado(3L,
			"Usuario activado, ha llenado información personal, pero no se ha asociado a una empresa");

	public static final UsuarioEstado ACTIVADO_CON_EMPRESA = new UsuarioEstado(4L,
			"Usuario activado, ha llenado información personal y se ha asociado a una empresa");

	public boolean esPendienteActivacion() {
		return this.id != null && this.id == 1L;
	}

	public boolean esActivadoSinInfo() {
		return this.id != null && this.id == 2L;
	}

	public boolean esActivadoSinEmpresa() {
		return this.id != null && this.id == 3L;
	}

	public boolean esActivadoConEmpresa() {
		return this.id != null && this.id == 4L;
	}

	public boolean esDesactivado() {
		return this.id != null && this.id == 0L;
	}

}
