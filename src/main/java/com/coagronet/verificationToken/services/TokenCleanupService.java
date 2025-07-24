package com.coagronet.verificationToken.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.verificationToken.repositories.VerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {

	private final VerificationTokenRepository verificationTokenRepository;

	@Transactional
	public void deleteExpiredTokens() {
		verificationTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
		System.out.println("Tokens caducados eliminados.");
	}

}
