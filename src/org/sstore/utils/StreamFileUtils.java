package org.sstore.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamFileUtils {

	public static byte[] readBytes(String inpath) {
		File file = new File(inpath);
		byte[] data = new byte[(int) file.length()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static void writeBytes(String outpath, byte[] data) {
		File file = new File(outpath);
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {

		}
	}
}
