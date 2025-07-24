package com.coagronet.userRole.models;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleId implements Serializable {

	private Long user;

	private Long role;

}
