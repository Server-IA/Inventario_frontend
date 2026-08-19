package com.inventario.auth.listeners;

public record RoleActivatedEvent(Long usuarioRolId, String fallbackLanguageTag) {

}
