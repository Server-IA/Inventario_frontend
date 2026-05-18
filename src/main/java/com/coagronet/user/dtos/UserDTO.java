package com.coagronet.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.coagronet.user.LanguagePreference;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

	private Long id;

	private String password;

	private String username;

	private Long personaId;

	private Long usuarioEstadoId;

	private LanguagePreference preferredLanguage;

}
