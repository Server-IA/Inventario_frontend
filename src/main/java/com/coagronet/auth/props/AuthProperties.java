package com.coagronet.auth.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class AuthProperties {

	/**
	 * Rol por defecto que se asigna al nuevo usuario. Se lee de la propiedad
	 * <code>jwt.default_role</code>
	 */
	private String defaultRole;

	public String getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}

}
