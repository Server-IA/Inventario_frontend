package com.inventario.validation;

import org.springframework.stereotype.Component;

import com.inventario.pedido.repositories.PedidoRepository;
import com.inventario.utils.UserEmpresaService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PedidoExistsValidator implements ConstraintValidator<PedidoExists, Long> {

	private final PedidoRepository pedidoRepository;

	private final UserEmpresaService userEmpresaService;

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		if (value == null)
			return true;

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		boolean exists = pedidoRepository.findByIdAndEmpresaId(value, empresaId).isPresent();
		if (!exists) {
			var hctx = context
				.unwrap(org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext.class);

			hctx.disableDefaultConstraintViolation();
			hctx.addMessageParameter("id", value)
				.buildConstraintViolationWithTemplate("{pedido.not-found}")
				.addConstraintViolation();
		}
		return exists;
	}

}
