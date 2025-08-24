package com.shortener.util;

public class Base62 {
	private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	public static String encode(long value) {
		if (value == 0)
			return "0";
		StringBuilder sb = new StringBuilder();
		while (value > 0) {
			int rem = (int) (value % 62);
			sb.append(ALPHABET[rem]);
			value /= 62;
		}
		return sb.reverse().toString();
	}
}
