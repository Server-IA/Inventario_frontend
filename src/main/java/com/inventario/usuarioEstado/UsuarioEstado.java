/*=============================================================================
 Nombre del archivo : UsuarioEstado.java
 Descripcion        : Entidad de persistencia para el estado del usuario.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-16 | 0.4.0   | JUAN JOSE CASTRO     | Adición del atributo nombre |
 |            |         |                      | con su respectiva anotación |
 |            |         |                      | @Column mapeado a la base   |
 |            |         |                      | de datos como use_nombre.   |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.inventario.usuarioEstado;

import java.io.Serializable;

import com.inventario.estado.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario_estado")
public class UsuarioEstado implements Serializable {

	private static final long serialVersionUID = 1L;

	// --- CONSTANTES DE NEGOCIO (Solo IDs) ---
	public static final Long ID_DESACTIVADO = 0L;

	public static final Long ID_PENDIENTE_VERIFICACION = 1L;

	public static final Long ID_ACTIVADO_SIN_INFO = 2L;

	public static final Long ID_ACTIVADO_SIN_EMPRESA = 3L;

	public static final Long ID_ACTIVADO_CON_EMPRESA = 4L;

	public static final Long ID_ACTIVADO_DEBE_CAMBIAR_CONTRASENA = 5L;

	// --- MAPEO DE BASE DE DATOS ---
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "use_id")
	private Long id;

	@Column(name = "use_nombre", nullable = false, unique = false)
	private String nombre;

	@Column(name = "use_descripcion", nullable = false, unique = true, length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "use_estado_id", nullable = false)
	private Estado estadoBase;

	// --- MÉTODOS DE AYUDA (Helper Methods) ---
	public boolean esPendienteActivacion() {
		return this.id != null && this.id.equals(ID_PENDIENTE_VERIFICACION);
	}

	public boolean esActivadoSinInfo() {
		return this.id != null && this.id.equals(ID_ACTIVADO_SIN_INFO);
	}

	public boolean esActivadoSinEmpresa() {
		return this.id != null && this.id.equals(ID_ACTIVADO_SIN_EMPRESA);
	}

	public boolean esActivadoConEmpresa() {
		return this.id != null && this.id.equals(ID_ACTIVADO_CON_EMPRESA);
	}

	public boolean esDesactivado() {
		return this.id != null && this.id.equals(ID_DESACTIVADO);
	}

	public boolean debeCambiarContrasena() {
		return this.id != null && this.id.equals(ID_ACTIVADO_DEBE_CAMBIAR_CONTRASENA);
	}

	// --- JAP EQUALS & HASHCODE OPTIMIZADO ---
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UsuarioEstado))
			return false;
		UsuarioEstado that = (UsuarioEstado) o;
		return id != null && id.equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}