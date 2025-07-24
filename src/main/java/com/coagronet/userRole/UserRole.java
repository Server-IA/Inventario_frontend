package com.coagronet.userRole;

import java.io.Serializable;

import com.coagronet.empresa.Empresa;
import com.coagronet.role.Role;
import com.coagronet.user.User;
import com.coagronet.userRole.models.UserRoleId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "usuario_rol")
@IdClass(UserRoleId.class)
public class UserRole implements Serializable {

	@Id
	@ManyToOne
	@JoinColumn(name = "usr_usuario_id")
	private User user;

	@Id
	@ManyToOne
	@JoinColumn(name = "usr_rol_id")
	private Role role;

	@ManyToOne
	@JoinColumn(name = "usr_empresa_id", referencedColumnName = "emp_id", nullable = true)
	private Empresa empresa;

	// getters and setters

	public String getName() {
		return role.getName();
	}

}
