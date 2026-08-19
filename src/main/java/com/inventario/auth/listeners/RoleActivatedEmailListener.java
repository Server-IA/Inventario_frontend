package com.inventario.auth.listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.inventario.email.services.EmailVerificationService;
import com.inventario.user.User;
import com.inventario.usuariorol.UsuarioRol;
import com.inventario.usuariorol.repositories.UsuarioRolRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleActivatedEmailListener {

	private final UsuarioRolRepository usuarioRolRepository;
	private final EmailVerificationService emailService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onRoleActivated(RoleActivatedEvent event) {

		UsuarioRol ur = usuarioRolRepository.findForEmailById(event.usuarioRolId())
				.orElseThrow(() -> new IllegalStateException(
						"No se encontró UsuarioRol para enviar correo. id=" + event.usuarioRolId()));

		User user = ur.getUser();

		emailService.sendRoleActivatedEmail(user.getUsername(), ur.getRol().getNombre(), user.getPersona().getNombre(),
				user.getPersona().getApellido(), ur.getEmpresa().getNombre(), event.fallbackLanguageTag());
	}
}
