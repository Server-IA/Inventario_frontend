package com.inventario.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.stereotype.Component;

import com.inventario.estado.repositories.EstadoRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EstadoOrdenCompraValidator implements ConstraintValidator<EstadoOrdenCompra, Long> {

	private final EstadoRepository estadoRepository;

	private long categoriaId;

	@Override
	public void initialize(EstadoOrdenCompra annotation) {
		this.categoriaId = annotation.value();
	}

	@Override
	public boolean isValid(Long estadoId, ConstraintValidatorContext ctx) {
		if (estadoId == null)
			return true;

		boolean valid = estadoRepository.findByIdAndEstadoCategoriaId(estadoId, categoriaId).isPresent();

		if (!valid) {
			// Interpolar {id} (puedes usar {estadoId} si prefieres, ver nota abajo)
			HibernateConstraintValidatorContext hctx = ctx.unwrap(HibernateConstraintValidatorContext.class);

			hctx.disableDefaultConstraintViolation();
			hctx.addMessageParameter("id", estadoId)
				.buildConstraintViolationWithTemplate("{validation.orden-compra.estado.invalid-category}")
				.addConstraintViolation();
		}
		return valid;
	}

}
