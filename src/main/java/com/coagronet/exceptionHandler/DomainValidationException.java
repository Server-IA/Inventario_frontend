package com.coagronet.exceptionHandler;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class DomainValidationException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	@Getter
	public static class Violation {

		private final String field;

		private final String code;

		private final Object[] args;

		public Violation(String field, String code, Object... args) {
			this.field = field;
			this.code = code;
			this.args = args;
		}

	}

	private final List<Violation> violations = new ArrayList<>();

	public DomainValidationException() {
		super("domain.validation");
	}

	public DomainValidationException add(String field, String code, Object... args) {
		this.violations.add(new Violation(field, code, args));
		return this;
	}

	public boolean isEmpty() {
		return this.violations.isEmpty();
	}

	public List<Violation> getViolations() {
		return Collections.unmodifiableList(violations);
	}

}
