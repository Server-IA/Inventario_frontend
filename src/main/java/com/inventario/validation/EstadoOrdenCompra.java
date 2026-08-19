package com.inventario.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = EstadoOrdenCompraValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface EstadoOrdenCompra {

	long value();

	String message() default "validation.orden-compra.estado.invalid-category";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
