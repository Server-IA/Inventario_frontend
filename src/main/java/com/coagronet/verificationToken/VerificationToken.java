package com.coagronet.verificationToken;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_tokens",
		uniqueConstraints = { @UniqueConstraint(name = "verification_tokens_vet_email_key", columnNames = "email") })
public class VerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vet_id")
	private Long id;

	@Column(name = "vet_email", nullable = false, length = 255)
	private String email;

	@Column(name = "vet_expiry_date", nullable = false)
	private LocalDateTime expiryDate;

	@Column(name = "vet_token", nullable = false, length = 255)
	private String token;

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiryDate);
	}

}