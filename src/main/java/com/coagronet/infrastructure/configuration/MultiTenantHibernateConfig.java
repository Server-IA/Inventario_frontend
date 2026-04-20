package com.coagronet.infrastructure.configuration;

import java.util.Map;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MultiTenantHibernateConfig implements HibernatePropertiesCustomizer {

	private final EmpresaTenantIdentifierResolver tenantResolver;

	public MultiTenantHibernateConfig(EmpresaTenantIdentifierResolver tenantResolver) {
		this.tenantResolver = tenantResolver;
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put("hibernate.tenant_identifier_resolver", tenantResolver);
	}

}
