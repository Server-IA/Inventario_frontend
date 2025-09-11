package com.coagronet.user.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.user.User;
import com.coagronet.user.events.OnRegistrationCompleteEvent;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;
import com.coagronet.verificationToken.TokenPurpose;
import com.coagronet.verificationToken.repositories.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

	private final UserRepository userRepository;

	private final VerificationTokenRepository verificationTokenRepository;

	private final ApplicationEventPublisher publisher;

	@Transactional
	public void registerUser(User user) {
		userRepository.save(user);
		publisher.publishEvent(new OnRegistrationCompleteEvent(user));
	}

	@Transactional
	public boolean activateUser(String token) {
		var tokenOptional = verificationTokenRepository.findByTokenAndPurpose(token, TokenPurpose.VERIFY);
		if (tokenOptional.isEmpty())
			return false;

		var vt = tokenOptional.get();
		if (vt.isExpired()) {
			verificationTokenRepository.delete(vt); // opcional, para limpieza
			return false;
		}

		var user = userRepository.findByUsername(vt.getEmail())
			.orElseThrow(() -> new RuntimeException("User not found with email: " + vt.getEmail()));

		user.setUsuarioEstado(UsuarioEstado.ACTIVADO_SIN_INFO);
		userRepository.save(user);

		// ? MUY IMPORTANTE: invalidar el token tras activar
		verificationTokenRepository.delete(vt);

		return true;
	}

}
