package cn.org.tpeach.nosql.tools;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileUtil {
	public static final long RECORD_LENGTH = 100;
	public static final String EMPTY_STRING = " ";
	public static final String CRLF = "\n";

	public static final String PATHNAME = "D:\\temp\\mahtew.txt";

	/**
	 * one two three Text to be appended with five six seven eight nine ten
	 * 
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		boolean init = false;
		if(init) {
			createFile();
			return ;
		}
		String starPrefix = "Text to be appended with";
		String replacedString = "new text has been appended";

		RandomAccessFile file = new RandomAccessFile(new File(PATHNAME), "rw");

		String line = "";
		while ((line = file.readLine()) != null) {
			if (line.startsWith(starPrefix)) {
				file.seek(file.getFilePointer());
				file.seek(file.getFilePointer() - RECORD_LENGTH - 1);
				file.writeBytes(replacedString);
			}

		}
	}

	public static void createFile() throws IOException {
		RandomAccessFile file = new RandomAccessFile(new File(PATHNAME), "rw");
		file.seek(0);
		String line1 = "one two three";
		String line2 = "Text to be appended with";
		String line3 = "five six seven";
		String line4 = "eight nine ten";

		file.writeBytes(paddingRight(line1));
		file.writeBytes(CRLF);
		file.writeBytes(paddingRight(line2));
		file.writeBytes(CRLF);
		file.writeBytes(paddingRight(line3));
		file.writeBytes(CRLF);
		file.writeBytes(paddingRight(line4));
		file.writeBytes(CRLF);

		file.close();

		System.out.println(String.format("File is created in [%s]", PATHNAME));
	}

	public static String paddingRight(String source) {
		StringBuilder result = new StringBuilder(100);
		if (source != null) {
			result.append(source);
//			for (int i = 0; i < RECORD_LENGTH - source.length(); i++) {
//				result.append(EMPTY_STRING);
//			}
		}

		return result.toString();
	}
}