package com.harunaydin.tomcat.security;

public class HexUtils {
	public static byte[] hexToBytes(String str) {
		byte[] bytes = new byte[(str.length() + 1) / 2];
		if (str.length() == 0) {
			return bytes;
		}
		bytes[0] = 0;
		int nibbleIdx = (str.length() % 2);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (!isHex(c)) {
				throw new IllegalArgumentException("string contains non-hex chars");
			}
			if ((nibbleIdx % 2) == 0) {
				bytes[nibbleIdx >> 1] = (byte) (hexValue(c) << 4);
			} else {
				bytes[nibbleIdx >> 1] += (byte) hexValue(c);
			}
			nibbleIdx++;
		}
		return bytes;
	}

	private static boolean isHex(char c) {
		return ((c >= '0') && (c <= '9')) || ((c >= 'a') && (c <= 'f')) || ((c >= 'A') && (c <= 'F'));
	}

	private static int hexValue(char c) {
		if ((c >= '0') && (c <= '9')) {
			return (c - '0');
		} else if ((c >= 'a') && (c <= 'f')) {
			return (c - 'a') + 10;
		} else {
			return (c - 'A') + 10;
		}
	}

	public static String convert(byte bytes[]) {

		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(convertDigit((int) (bytes[i] >> 4)));
			sb.append(convertDigit((int) (bytes[i] & 0x0f)));
		}
		return (sb.toString());

	}

	private static char convertDigit(int value) {
		value &= 0x0f;
		if (value >= 10)
			return ((char) (value - 10 + 'a'));
		else
			return ((char) (value + '0'));
	}

}
