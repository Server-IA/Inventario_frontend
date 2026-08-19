package com.inventario.auth.events;

public record NewUserCredentialsEvent(Long usuarioRolId, String tempPassword, String fallbackLanguageTag) {

}
