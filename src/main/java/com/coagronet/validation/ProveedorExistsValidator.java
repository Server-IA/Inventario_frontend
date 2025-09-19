package com.coagronet.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.stereotype.Component;

import com.coagronet.proveedor.repositories.ProveedorRepository;
import com.coagronet.utils.UserEmpresaService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProveedorExistsValidator implements ConstraintValidator<ProveedorExists, Long> {

	private final ProveedorRepository proveedorRepository;

	private final UserEmpresaService userEmpresaService;

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext ctx) {
		if (value == null)
			return true;

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		boolean exists = proveedorRepository.findByIdAndEmpresaId(value, empresaId).isPresent();

		if (!exists) {
			// Interpolar {id} en el mensaje
			HibernateConstraintValidatorContext hctx = ctx.unwrap(HibernateConstraintValidatorContext.class);

			hctx.disableDefaultConstraintViolation();
			hctx.addMessageParameter("id", value)
				.buildConstraintViolationWithTemplate("{proveedor.not-found}")
				.addConstraintViolation();
		}
		return exists;
	}

}
