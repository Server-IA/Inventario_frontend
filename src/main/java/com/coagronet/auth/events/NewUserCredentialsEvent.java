package com.coagronet.auth.events;

public record NewUserCredentialsEvent(Long usuarioRolId, String tempPassword) {

}
