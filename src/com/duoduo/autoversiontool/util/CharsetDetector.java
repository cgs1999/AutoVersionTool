package com.duoduo.autoversiontool.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

/**
 * 文件字符集检测器
 * @author chengesheng@gmail.com
 * @date 2014-1-20 上午10:44:23
 * @version 1.0.0
 */
public class CharsetDetector {

	private boolean found = false;
	private String result;

	public String[] detectCharset(InputStream is) {
		return detectCharset(is, nsPSMDetector.ALL);
	}

	public String[] detectCharset(InputStream is, int langFlag) {
		String[] prob = null;
		nsDetector detector = new nsDetector(langFlag);
		detector.Init(new nsICharsetDetectionObserver() {

			@Override
			public void Notify(String charset) {
				found = true;
				result = charset;
			}
		});

		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] b = new byte[1024];
			int len;
			boolean isAscii = true;

			while ((len = bis.read(b, 0, b.length)) != -1) {
				if (isAscii) {
					isAscii = detector.isAscii(b, len);
				}
				if (!isAscii) {
					if (detector.DoIt(b, len, false)) {
						break;
					}
				}
			}
			bis.close();
			is.close();
			detector.DataEnd();

			if (isAscii) {
				found = true;
				prob = new String[] {
					"UTF-8"
				};
			} else if (found) {
				prob = new String[] {
					result
				};
			} else {
				prob = detector.getProbableCharsets();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return prob;
	}

	public String[] detectChineseCharset(String filePath) throws FileNotFoundException {
		return detectChineseCharset(new File(filePath));
	}

	public String[] detectChineseCharset(File file) throws FileNotFoundException {
		return detectChineseCharset(new FileInputStream(file));
	}

	public String[] detectChineseCharset(InputStream is) {
		return detectCharset(is, nsPSMDetector.CHINESE);
	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// doDetectFile("C:\\demo\\resource.jsp");
		// doDetectFile("C:\\demo\\WEB-INF\\jsps\\mcc\\manager.jsp");

		// doDetect("C:\\demo");
		// doDetect("C:\\demo\\WEB-INF\\jsps");
		doDetect("D:\\exeTool\\vrswebclient");
	}

	private static void doDetect(String path) throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("指定路径不存在!" + path);
			return;
		}

		if (file.isDirectory()) {
			doDetectDirectory(file);
		} else {
			doDetectFile(path);
		}
	}

	private static void doDetectDirectory(File dir) throws FileNotFoundException {
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				doDetectDirectory(f);
			} else {
				doDetectFile(f);
			}
		}
	}

	private static void doDetectFile(String filePath) throws FileNotFoundException {
		doDetectFile(new File(filePath));
	}

	private static void doDetectFile(File file) throws FileNotFoundException {
		CharsetDetector detector = new CharsetDetector();
		String[] charsets = detector.detectChineseCharset(file);

		// if(charsets.length>1)
		printResult(file.getAbsolutePath(), charsets);
	}

	private static void printResult(String path, String[] charsets) {
		System.out.print(path + " --> ");
		for (int i = 0; i < charsets.length; i++) {
			if (i != 0) {
				System.out.print(", ");
			}
			System.out.print(charsets[i]);
		}
		System.out.println();
	}

}
