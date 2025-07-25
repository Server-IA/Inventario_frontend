package com.coagronet.userRole.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleId implements Serializable {

	private static final long serialVersionUID = -6306773639536762670L;

	private Long user;

	private Long role;

}
