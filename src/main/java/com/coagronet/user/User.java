package com.coagronet.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.coagronet.empresa.Empresa;
import com.coagronet.persona.Persona;
import com.coagronet.rol.Rol;
import com.coagronet.usuarioEstado.UsuarioEstado;
import com.coagronet.usuariorol.UsuarioRol; // Importar la clase asociativa correcta

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "usuario")
public class User implements UserDetails {

	private static final long serialVersionUID = -4111948693138979290L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usu_id")
	private Long id;

	@Email
	@Column(name = "usu_email", unique = true, nullable = false, length = 255)
	private String username;

	@Column(name = "usu_password", nullable = false, length = 255)
	private String password;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usu_persona_id", referencedColumnName = "per_id")
	private Persona persona;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<UsuarioRol> rolesAsignados = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usu_estado_id", referencedColumnName = "use_id", nullable = false)
	private UsuarioEstado usuarioEstado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usu_preferred_empresa_id")
	private Empresa preferredEmpresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usu_preferred_rol_id")
	private Rol preferredRol;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "usu_preferred_language", nullable = false, length = 5)
	private LanguagePreference preferredLanguage = LanguagePreference.ES;

	@Builder.Default
	@Column(name = "usu_token_version", nullable = false)
	private Integer tokenVersion = 0;

	public void incrementTokenVersion() {
		this.tokenVersion++;
	}

	// ====== Spring Security / UserDetails Implementation ======

	@Transient
	private Collection<? extends GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (authorities != null) {
			return authorities;
		}

		return rolesAsignados.stream()
				.filter(ur -> ur.getEstado() != null && ur.getEstado().getId() == 1L)
				.map(ur -> new SimpleGrantedAuthority("ROLE_" + ur.getRol().getNombre()))
				.collect(Collectors.toSet());
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.usuarioEstado != null && this.usuarioEstado.getId() >= 2;
	}

	/*
	 * ==================================================================== HELPER
	 * METHODS
	 * PARA RELACIONES BIDIRECCIONALES (JPA Best Practices)
	 * ====================================================================
	 */

	/**
	 * Sincroniza ambos lados de la relación al agregar un nuevo rol/contrato.
	 * Obligatorio
	 * para evitar FKs nulas y mantener el Contexto de Persistencia coherente.
	 */
	public void addUsuarioRol(UsuarioRol usuarioRol) {
		// 1. Añade el contrato a la lista del usuario
		this.rolesAsignados.add(usuarioRol);
		// 2. Asigna este usuario como el dueño (owner) en la entidad hija
		usuarioRol.setUser(this);
	}

	/**
	 * Sincroniza ambos lados de la relación al remover un rol/contrato. Activa el
	 * orphanRemoval = true si está configurado.
	 */
	public void removeUsuarioRol(UsuarioRol usuarioRol) {
		this.rolesAsignados.remove(usuarioRol);
		usuarioRol.setUser(null);
	}

	// ====== JPA Equals & HashCode ======

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User))
			return false;
		User user = (User) o;
		return id != null && id.equals(user.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}