package com.coagronet.usuarioEstado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario_estado")
public class UsuarioEstado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "use_id")
	private Long id;

	// Definir estados como constantes
	public static final UsuarioEstado DESACTIVADO = new UsuarioEstado(0L);

	public static final UsuarioEstado PENDIENTE_VERIFICACION = new UsuarioEstado(1L);

	public static final UsuarioEstado ACTIVADO_SIN_INFO = new UsuarioEstado(2L);

	public static final UsuarioEstado ACTIVADO_SIN_EMPRESA = new UsuarioEstado(3L);

	public static final UsuarioEstado ACTIVADO_CON_EMPRESA = new UsuarioEstado(4L);

	public static final UsuarioEstado ACTIVADO_DEBE_CAMBIAR_CONTRASENA = new UsuarioEstado(5L);

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

	public boolean debeCambiarContrasena() {
		return this.id != null && this.id == 5L;
	}

}
