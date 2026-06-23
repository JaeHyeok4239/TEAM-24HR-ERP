package com.hr24.global.crypto;

public final class MaskingUtil {

	private MaskingUtil() {
	}

	public static String maskRrn(String rrn) {
		if (rrn == null || rrn.isBlank()) {
			return null;
		}

		String normalized = rrn.trim();

		if (normalized.contains("-")) {
			String[] parts = normalized.split("-", 2);

			if (parts.length != 2) {
				return maskAllButLast(normalized, 0);
			}

			return parts[0] + "-" + "*".repeat(parts[1].length());
		}

		if (normalized.length() <= 6) {
			return "*".repeat(normalized.length());
		}

		return normalized.substring(0, 6) + "*".repeat(normalized.length() - 6);
	}

	public static String maskAccountNumber(String accountNumber) {
		if (accountNumber == null || accountNumber.isBlank()) {
			return null;
		}

		String normalized = accountNumber.trim();

		if (normalized.length() <= 4) {
			return "*".repeat(normalized.length());
		}

		int visibleLength = 4;
		int maskedLength = normalized.length() - visibleLength;

		return "*".repeat(maskedLength) + normalized.substring(maskedLength);
	}

	public static String maskAllButLast(String value, int visibleLastLength) {
		if (value == null || value.isBlank()) {
			return null;
		}

		String normalized = value.trim();

		if (visibleLastLength <= 0) {
			return "*".repeat(normalized.length());
		}

		if (normalized.length() <= visibleLastLength) {
			return "*".repeat(normalized.length());
		}

		int maskedLength = normalized.length() - visibleLastLength;

		return "*".repeat(maskedLength) + normalized.substring(maskedLength);
	}
}