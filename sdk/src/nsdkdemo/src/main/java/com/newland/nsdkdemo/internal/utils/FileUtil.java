package com.newland.nsdkdemo.internal.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class FileUtil {

	public static String readFileByLines(File file) {

		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String tempString = null;
			while ((tempString = bufferedReader.readLine()) != null) {
				stringBuilder.append(tempString).append("\n");
			}
			bufferedReader.close();
			return stringBuilder.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
