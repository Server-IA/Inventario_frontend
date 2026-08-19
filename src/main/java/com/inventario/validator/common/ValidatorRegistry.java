package com.inventario.validator.common;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidatorRegistry {

    private final Map<Class<?>, BaseValidator> validatorsByType = new HashMap<>();

    public ValidatorRegistry(List<BaseValidator> validators) {
        for (BaseValidator validator : validators) {
            validatorsByType.put(validator.getClass(), validator);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseValidator> T getValidator(Class<T> validatorClass) {
        return (T) validatorsByType.get(validatorClass);
    }
}
