package com.coagronet.user.services;

import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.user.User;
import com.coagronet.user.events.OnRegistrationCompleteEvent;
import com.coagronet.user.repositories.UserRepository;
import com.coagronet.usuarioEstado.UsuarioEstado;
import com.coagronet.verificationToken.VerificationToken;
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

	public boolean activateUser(String token) {
		Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(token);
		if (tokenOptional.isPresent()) {
			VerificationToken verificationToken = tokenOptional.get();

			Optional<User> userOptional = userRepository.findByUsername(verificationToken.getEmail());
			if (userOptional.isPresent()) {
				User user = userOptional.get();

				// Cambiar el estado a 2: Usuario activado, pero no ha llenado información
				// personal y no se ha asociado a una empresa
				user.setUsuarioEstado(UsuarioEstado.ACTIVADO_SIN_INFO);
				userRepository.save(user);

				return true; // Activación exitosa
			}
			else {
				throw new RuntimeException("User not found with email: " + verificationToken.getEmail());
			}
		}
		else {
			return false; // Token no válido o ha expirado
		}
	}

}
