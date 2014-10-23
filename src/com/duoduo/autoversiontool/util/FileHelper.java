package com.duoduo.autoversiontool.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * 文件处理帮助类
 * @author chengesheng@gmail.com
 * @date 2014-1-20 上午10:42:47
 * @version 1.0.0
 */
public class FileHelper {

	private static char hexChar[] = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};

	// 获取文档的CheckSum
	public static String getCheckSum(String path) {
		String ret = "";
		try {
			FileInputStream fis = new FileInputStream(new File(path));
			CheckedInputStream cis = new CheckedInputStream(fis, new CRC32());
			BufferedInputStream in = new BufferedInputStream(cis);
			while (in.read() != -1) {
			}
			Long checkSum = cis.getChecksum().getValue();
			ret = checkSum.toString();

			if (in != null) {
				in.close();
			}
			if (cis != null) {
				cis.close();
			}
			if (fis != null) {
				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("错误：无法生成CheckSum :" + path);
		}
		return ret;
	}

	// 获取文档的MD5
	public static String getFileMD5(String filename) {
		String str = "";
		try {
			str = getHash(filename, "MD5");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("错误：无法生成MD5 :" + filename);
		}
		return str;
	}

	// 获取文档的SHA1
	public static String getFileSHA1(String filename) {
		String str = "";
		try {
			str = getHash(filename, "SHA1");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("错误：无法生成SHA1 :" + filename);
		}
		return str;
	}

	public static String getFileSHA256(String filename) {
		String str = "";
		try {
			str = getHash(filename, "SHA-256");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("错误：无法生成SHA256 :" + filename);
		}
		return str;
	}

	public static String getFileSHA384(String filename) {
		String str = "";
		try {
			str = getHash(filename, "SHA-384");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("错误：无法生成384 :" + filename);
		}
		return str;
	}

	public static String getFileSHA512(String filename) {
		String str = "";
		try {
			str = getHash(filename, "SHA-512");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("错误：无法生成512 :" + filename);
		}
		return str;
	}

	private static String getHash(String fileName, String hashType) throws Exception {
		InputStream fis = new FileInputStream(fileName);
		byte buffer[] = new byte[1024];
		MessageDigest md5 = MessageDigest.getInstance(hashType);
		for (int numRead = 0; (numRead = fis.read(buffer)) > 0;) {
			md5.update(buffer, 0, numRead);
		}
		fis.close();
		return toHexString(md5.digest());
	}

	private static String toHexString(byte b[]) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0xf]);
		}
		return sb.toString();
	}

	/**
	 * 获取文件, 若路径不存在则获取当前目录下的文件
	 * @param path 绝对路径或当前目录下的相对路径
	 * @return
	 */
	public static File getFile(String path) {
		String fullPath = path;
		File file = new File(fullPath);
		if (!file.exists()) {
			String userDir = System.getProperty("user.dir");
			if (!userDir.endsWith(File.separator)) {
				fullPath = userDir + File.separator + fullPath;
			} else {
				fullPath = userDir + fullPath;
			}

			file = new File(fullPath);
		}
		return file;
	}

	/**
	 * 根据文件扩展名和正则表达式过滤文件
	 * @param path 处理的路径
	 * @return
	 */
	public static List<String> filterFile(String path) {
		return filterFile(path, null);
	}

	/**
	 * 根据文件扩展名和正则表达式过滤文件
	 * @param path 处理的路径
	 * @param fileExt 过滤的文件扩展名, 多个使用半角逗号(,)分隔
	 * @return
	 */
	public static List<String> filterFile(String path, String fileExt) {
		return filterFile(path, fileExt, null);
	}

	/**
	 * 根据文件扩展名和正则表达式过滤文件
	 * @param path 处理的路径
	 * @param fileExt 过滤的文件扩展名, 多个使用半角逗号(,)分隔
	 * @param regex 过滤的正则表达式
	 * @return
	 */
	public static List<String> filterFile(String path, String fileExt, String regex) {
		return filterFile(path, fileExt, regex, null);
	}

	/**
	 * 根据文件扩展名和正则表达式过滤文件
	 * @param path 处理的路径
	 * @param fileExt 过滤的文件扩展名, 多个使用半角逗号(,)分隔
	 * @param regex 过滤的正则表达式
	 * @param ingorePath 忽略处理的路径
	 * @return
	 */
	public static List<String> filterFile(String path, String fileExt, String regex, String ingorePath) {
		return filterFile(new File(path), fileExt, regex, ingorePath);
	}

	/**
	 * 根据文件扩展名和正则表达式过滤文件
	 * @param file 处理的文件
	 * @param fileExt 过滤的文件扩展名, 多个使用半角逗号(,)分隔
	 * @param regex 过滤的正则表达式
	 * @param ingorePath 忽略处理的路径, 多个使用半角逗号(,)分隔
	 * @return
	 */
	public static List<String> filterFile(File file, String fileExt, String regex, String ingorePath) {
		List<String> fileList = new ArrayList<String>(0);
		if (!file.exists()) {
			return fileList;
		}

		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if (f.isDirectory()) {
					// 过滤忽略的路径
					if (!filterIngorePath(f.getAbsolutePath(), ingorePath)) {
						fileList.addAll(filterFile(f, fileExt, regex, ingorePath));
					}
				} else {
					String filePath = f.getAbsolutePath();
					if (!filterFileExt(filePath, fileExt)) {
						if (regex != null) {
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern.matcher(filePath);
							if (matcher.find()) {
								fileList.add(filePath);
							}
						} else {
							fileList.add(filePath);
						}
					}
				}
			}
		} else {
			String filePath = file.getAbsolutePath();
			if (!filterFileExt(filePath, fileExt)) {
				if (regex != null) {
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(filePath);
					if (matcher.find()) {
						fileList.add(filePath);
					}
				} else {
					fileList.add(filePath);
				}
			}
		}
		return fileList;
	}

	/**
	 * 过滤文件扩展名
	 * @param filePath 文件路径
	 * @param ingorePath 过滤的文件扩展名, 多个使用半角逗号(,)分隔
	 * @return true表示过滤, false表示不过滤
	 */
	private static boolean filterIngorePath(String filePath, String ingorePath) {
		if (ingorePath == null) {
			return false;
		}

		boolean filter = false;
		String path = filePath.toLowerCase() + File.separator; // 不管路径是否以"\"(windows)或"/"(unix)结尾
		String[] ingorePaths = ingorePath.split(",");
		for (String p : ingorePaths) {
			if (!p.endsWith(File.separator)) {
				p = p + File.separator;
			}
			if (path.startsWith(p.toLowerCase())) {
				filter = true;
			}
		}
		return filter;
	}

	/**
	 * 过滤文件扩展名
	 * @param filePath 文件路径
	 * @param fileExt 过滤的文件扩展名, 多个使用半角逗号(,)分隔
	 * @return true表示过滤, false表示不过滤
	 */
	private static boolean filterFileExt(String filePath, String fileExt) {
		if (fileExt == null) {
			return false;
		}

		boolean filter = true;
		String path = filePath.toLowerCase();
		String[] fileExts = fileExt.split(",");
		for (String ext : fileExts) {
			if (path.endsWith("." + ext.toLowerCase())) {
				filter = false;
			}
		}
		return filter;
	}

	/**
	 * 读取文件内容到List
	 * @param path
	 * @return
	 */
	public static List<String> readFileToList(String path, String encoding) {
		File file = new File(path);
		List<String> contentList = new ArrayList<String>();
		if (!file.isFile()) {
			System.out.println("错误：未找到指定文件 -> " + path);
			return contentList;
		}
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			Reader r = new InputStreamReader(is, encoding);
			BufferedReader br = new BufferedReader(r);
			// if (!encoding.equals("GBK")) {
			// // 文件编码非ANSI跳过1个字节，避免文件起始出现 ？
			// br.skip(1);
			// }
			String line = null;
			while ((line = br.readLine()) != null) {
				contentList.add(line);
			}
			br.close();
			r.close();
		} catch (Exception e) {
			System.out.println("错误：读文件失败 -> " + path);
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return contentList;
	}

	/**
	 * 将List内容写入指定文件
	 * @param path
	 * @param contentList
	 * @param encoding
	 */
	public static void writeListToFile(String path, List<String> contentList, String encoding) {
		File file = new File(path);
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			Writer w = null;
			if (encoding != null) {
				w = new OutputStreamWriter(os, encoding);
			} else {
				w = new OutputStreamWriter(os);
			}
			BufferedWriter bw = new BufferedWriter(w);
			if (contentList == null) {
				return;
			}

			for (String line : contentList) {
				bw.write(line + "\r\n");
			}
			// 更新到文件
			bw.flush();
			// 关闭流
			bw.close();
			w.close();
		} catch (Exception e) {
			System.out.println("错误：写文件失败 ->" + path);
			e.printStackTrace();
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 获取文件的编码
	public static String getFileCharset(String filePath) {
		File file = new File(filePath);
		return getFileCharset(file);
	}

	/**
	 * 获取文件的编码，不同编码的文本是根据文本的前两个字节来定义其编码格式的，定义如下： ANSI： 无格式定义 Unicode： 前两个字节为FFFE，Unicode文档以0xFFFE开头 Unicode big endian：
	 * 前两个字节为FEFF UTF-8： 前两个字节为EFBB，UTF-8文档以0xEFBBBF开头
	 * @param file
	 * @return
	 */
	// public static String getFileCharset(File file) {
	// String charset = "GBK"; // 默认文件编码为 ANSI
	// byte[] b = new byte[3];
	// try {
	// BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
	// int read = bis.read(b, 0, 3);
	// bis.close();
	//
	// if(read!=-1) {
	// if (b[0] == (byte) 0xFF && b[1] == (byte) 0xFE) {
	// charset = "UTF-16LE"; // 文件编码为 Unicode
	// } else if (b[0] == (byte) 0xFE && b[1] == (byte) 0xFF) {
	// charset = "UTF-16BE"; // 文件编码为 Unicode big endian
	// } else if (b[0] == (byte) 0xEF && b[1] == (byte) 0xBB && b[2] == (byte) 0xBF) {
	// charset = "UTF-8"; // 文件编码为 UTF-8
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return charset;
	// }

	public static String getFileCharset(File file) {
		String charset = "UTF-8";
		try {
			CharsetDetector detector = new CharsetDetector();
			String[] charsets = detector.detectCharset(new FileInputStream(file));
			if (charsets.length > 1) {
				boolean isUtf = false;
				for (String s : charsets) {
					if (s.startsWith("UTF")) {
						isUtf = true;
						charset = s;
						break;
					}
				}
				if (!isUtf) {
					charset = charsets[0];
				}
			} else {
				charset = charsets[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return charset;
	}

	// 获取文件的最后修改时间
	public static String getFileLastModTime(File file) {
		return getFileLastModTime(file, null);
	}

	public static String getPath(String filePath) {
		File file = new File(filePath);
		return getPath(file);
	}

	public static String getPath(File file) {
		String strFileName = file.getAbsolutePath().toLowerCase();
		String name = file.getName().toLowerCase();
		return strFileName.replace(name, "");
	}

	public static String getFileLastModTime(File file, String format) {
		if (format == null) {
			format = "yyyyMMddHHmmss";
		}
		Long modifiedTime = file.lastModified();
		Date date = new Date(modifiedTime);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String dd = sdf.format(date);
		return dd;
	}

	// 获取文件大小
	public static String getFileSize(File file, String format) {
		Long size = file.length();
		return size.toString();
	}

	// 获取当前路径
	public static String getCurrentDir() {
		String dir = "";
		File file = new File(""); // 设定为当前文件夹
		try {
			dir = file.getAbsolutePath(); // 获取绝对路径
		} catch (Exception e) {
		}
		if (!"".equals(dir) && !dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		return dir;
	}

	// 获取文件CheckSum
	public static String getCheckSum(File file) {
		return getCheckSum(file.getAbsolutePath());
	}

	// 获取文件的MD5
	public static String getMD5(File file) {
		return getFileMD5(file.getAbsolutePath());
	}

	// 获取文件的SHA1
	public static String getSHA1(File file) {
		return getFileSHA1(file.getAbsolutePath());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// List<String> fileList = filterFile("c:/temp", "dll", "\\w*\\.exe", null);
		// for (String file : fileList) {
		// System.out.println("--> " + file);
		// }

		printCharset("C:\\fileencoding\\gbk.txt");
		printCharset("C:\\fileencoding\\unicode.txt");
		printCharset("C:\\fileencoding\\unicode_big.txt");
		printCharset("C:\\fileencoding\\utf8.txt");

		System.out.println("Current dir: " + getCurrentDir());
	}

	private static void printCharset(String filePath) {
		System.out.println(filePath + " --> " + getFileCharset(filePath));
	}
}
