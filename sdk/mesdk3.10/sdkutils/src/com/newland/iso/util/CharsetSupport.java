package com.newland.iso.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class CharsetSupport {
	private static String charset = getDefaultCharset();

	public static String getDefaultCharset() {
		return "ISO-8859-1";
	}

	public static void setCharset(String charset)
			throws UnsupportedEncodingException {
		CharsetSupport.charset = validate(charset);
	}

	public static String getCharset() {
		return charset;
	}

	public static String validate(String charset)
			throws UnsupportedEncodingException {
		if (!Charset.isSupported(charset)) {
			throw new UnsupportedEncodingException();
		}
		return charset;
	}
}
