package com.inventario.verificationToken.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.verificationToken.TokenPurpose;
import com.inventario.verificationToken.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	Optional<VerificationToken> findByEmail(String email);

	Optional<VerificationToken> findByToken(String token);

	Optional<VerificationToken> findByEmailAndPurpose(String email, TokenPurpose purpose);

	Optional<VerificationToken> findByTokenAndPurpose(String token, TokenPurpose purpose);

	void deleteByExpiryDateBefore(LocalDateTime dateTime);

}
