package com.inventario.validation;

import org.springframework.stereotype.Component;

import com.inventario.estado.repositories.EstadoRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EstadoExistsValidator implements ConstraintValidator<EstadoExists, Long> {

	private final EstadoRepository estadoRepository;

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		if (value == null)
			return true;

		boolean exists = estadoRepository.findById(value).isPresent();
		if (!exists) {
			var hctx = context
				.unwrap(org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext.class);

			hctx.disableDefaultConstraintViolation();
			hctx.addMessageParameter("id", value)
				.buildConstraintViolationWithTemplate("{validation.estado.exists}")
				.addConstraintViolation();
		}
		return exists;
	}

}
