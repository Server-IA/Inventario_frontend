package com.coagronet.user;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.coagronet.persona.Persona;
import com.coagronet.role.Role;
import com.coagronet.usuarioEstado.UsuarioEstado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
	@JoinTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "usr_usuario_id"),
			inverseJoinColumns = @JoinColumn(name = "usr_rol_id"))
	private Set<Role> roles;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usu_estado_id", referencedColumnName = "use_id")
	private UsuarioEstado usuarioEstado;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
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
