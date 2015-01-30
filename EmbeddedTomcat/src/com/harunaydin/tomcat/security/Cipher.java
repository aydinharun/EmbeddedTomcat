package com.harunaydin.tomcat.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class digests clear text password for embedded tomcat
 */
public class Cipher {

	private static final String ALGORITHM = "SHA-256";

	/**
	 * Digest password using the algorithm specified and convert the result to a corresponding hex string.
	 * 
	 * @param password
	 *            Clear text password
	 * @throws CloneNotSupportedException
	 * @throws NoSuchAlgorithmException
	 */
	public final static String digest(String password) throws NoSuchAlgorithmException, CloneNotSupportedException {

		// Obtain a new message digest with "digest" encryption
		MessageDigest md = (MessageDigest) MessageDigest.getInstance(ALGORITHM).clone();

		md.update(password.getBytes());

		return (HexUtils.convert(md.digest()));

	}

	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Encrypted : " + digest("admin"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
