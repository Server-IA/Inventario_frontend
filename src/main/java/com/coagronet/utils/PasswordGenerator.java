package com.coagronet.utils;

import java.security.SecureRandom;
import java.util.Set;

public class PasswordGenerator {

	private static final SecureRandom RND = new SecureRandom();

	// Puedes ampliar el set de símbolos si quieres.
	private static final char[] LOWER = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	private static final char[] UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final char[] DIGIT = "0123456789".toCharArray();
	private static final char[] SYMBOL = "!@#$%^&*()-_=+[]{}:,.?/~".toCharArray();

	private static final char[] ALL = concat(LOWER, UPPER, DIGIT, SYMBOL);

	// Debe estar alineado con tu validatePasswordPolicy
	private static final int MIN_LEN = 16; // recomendado >= 16
	private static final int MAX_LEN = 64;

	// Si ya tienes COMMON_PASSWORDS en AuthService, reutilízalo.
	private static final Set<String> COMMON_PASSWORDS = Set.of("123456", "12345678", "password", "qwerty", "admin",
			"password1");

	public static String generateStrongPassword(int length) {
		if (length < 8 || length > MAX_LEN) {
			throw new IllegalArgumentException("length debe estar entre 8 y " + MAX_LEN);
		}

		// Reintentos por si cae en alguna validación (muy raro)
		for (int attempt = 0; attempt < 50; attempt++) {
			char[] pwd = new char[length];

			// Garantiza diversidad mínima (aunque NIST no obliga, tu validador pide
			// distinct >= 4)
			pwd[0] = LOWER[RND.nextInt(LOWER.length)];
			pwd[1] = UPPER[RND.nextInt(UPPER.length)];
			pwd[2] = DIGIT[RND.nextInt(DIGIT.length)];
			pwd[3] = SYMBOL[RND.nextInt(SYMBOL.length)];

			for (int i = 4; i < length; i++) {
				pwd[i] = ALL[RND.nextInt(ALL.length)];
			}

			// Mezcla Fisher-Yates
			for (int i = pwd.length - 1; i > 0; i--) {
				int j = RND.nextInt(i + 1);
				char tmp = pwd[i];
				pwd[i] = pwd[j];
				pwd[j] = tmp;
			}

			String candidate = new String(pwd);

			// Evitar espacios al inicio/fin (aquí no generamos espacios, pero por
			// seguridad)
			if (!candidate.equals(candidate.trim()))
				continue;

			// Evitar lista de comunes
			if (COMMON_PASSWORDS.contains(candidate.toLowerCase()))
				continue;

			// Evitar repetición total (no debería ocurrir)
			if (allCharactersAreTheSame(candidate))
				continue;

			// Evitar baja variedad (distinct < 4)
			long distinct = candidate.codePoints().distinct().count();
			if (distinct < 4)
				continue;

			return candidate;
		}

		throw new IllegalStateException("No se pudo generar una contraseña válida tras varios intentos.");
	}

	public static String generateStrongPassword() {
		int length = MIN_LEN + RND.nextInt(9); // 16..24
		return generateStrongPassword(length);
	}

	private static boolean allCharactersAreTheSame(String s) {
		int first = s.codePointAt(0);
		return s.codePoints().allMatch(cp -> cp == first);
	}

	private static char[] concat(char[]... arrays) {
		int len = 0;
		for (char[] a : arrays)
			len += a.length;
		char[] out = new char[len];
		int pos = 0;
		for (char[] a : arrays) {
			System.arraycopy(a, 0, out, pos, a.length);
			pos += a.length;
		}
		return out;
	}
}
