package com.newland.smmanager.assistant;

public class ST_RSA_PRIVATE_KEY {
	public short bits;
	public byte[] modulus = new byte[513];
	public byte[] publicExponent = new byte[513];
	public byte[] exponent = new byte[513];
	public byte[][] prime = new byte[2][257];
	public byte[][] primeExponent = new byte[2][257];
	public byte[] coefficient = new byte[257];
}
