package com.coagronet.user;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.coagronet.persona.Persona;
import com.coagronet.rol.Rol;
import com.coagronet.usuarioEstado.UsuarioEstado;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id", // nombre de la columna en
																					// usuario_rol
			referencedColumnName = "usu_id" // PK de usuario
	), inverseJoinColumns = @JoinColumn(name = "rol_id", // nombre de la columna en usuario_rol
			referencedColumnName = "id" // PK de rol
	))
	private Set<Rol> roles;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usu_estado_id", referencedColumnName = "use_id")
	private UsuarioEstado usuarioEstado;

	@Column(name = "usu_preferred_empresa_id")
	private Long preferredEmpresaId;

	@Column(name = "usu_preferred_rol_id")
	private Long preferredRolId;

	@Builder.Default
	@Column(name = "usu_token_version", nullable = false)
	private Integer tokenVersion = 0;

	public void incrementTokenVersion() {
		this.tokenVersion = (this.tokenVersion == null ? 1 : this.tokenVersion + 1);
	}

	/*@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(r -> new SimpleGrantedAuthority(r.getNombre())).collect(Collectors.toSet());
	}*/



	/**
	 * <h4> * Si authorities ya fueron cargadas (permiso + roles), se usan.
	 * 	 * Si no, devuelve solo roles (compatibilidad con endpoints quemados)</h4>

	 */
	@Transient
	private Collection<? extends GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities != null ? authorities :
				roles.stream()
						.map(r -> new SimpleGrantedAuthority(r.getNombre()))
						.collect(Collectors.toSet());
	}

	public void setUsuarioEstado(UsuarioEstado usuarioEstado) {
		this.usuarioEstado = usuarioEstado;
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
		return this.usuarioEstado.getId() >= 2;
	}




}
