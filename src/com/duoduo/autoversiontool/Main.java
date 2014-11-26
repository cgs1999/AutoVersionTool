package com.duoduo.autoversiontool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.duoduo.autoversiontool.model.Config;
import com.duoduo.autoversiontool.model.Match;
import com.duoduo.autoversiontool.model.Source;
import com.duoduo.autoversiontool.model.Tag;
import com.duoduo.autoversiontool.util.FileHelper;
import com.duoduo.autoversiontool.util.XmlParseHelper;

/**
 * 资源版本管理工具
 * @author chengesheng@gmail.com
 * @date 2014-1-17 下午4:21:08
 * @TODO 如何解决由于文件版本更新的先后造成的版本不准确的问题?
 * @TODO 场景如: 循环先修改了A文件,A引用的B文件没有修改则其版本号没有更新, 循环后面又修改了B文件,但A中引用B文件的版本没有再更新了
 * @TODO 解决方案: 1)设置处理的先后顺序 2)循环修改直至没有发现修改(处理效率比较低,但可以解决问题)
 */
public class Main {

	private static Config config = null;

	public static void main(String[] args) {
		// String p = "C:/temp/webapp/WEB-INF/jsps/index.jsp";
		// // 处理WEB-INF
		// int find = p.toLowerCase().indexOf("web-inf");
		// if (find != -1) {
		// p = p.substring(0, find) + p.substring(find + 8);
		// }
		// System.out.println(">>" + p);
		// System.exit(0);

		String configFile = "config.xml";
		if (args.length > 0) {
			configFile = args[0];
		}

		File file = FileHelper.getFile(configFile);
		if (!file.exists()) {
			System.out.println("错误：配置文件不存在！");
			System.exit(0);
		}

		config = XmlParseHelper.parseXml(file);
		if (config.getSources().isEmpty()) {
			System.out.println("没有需要处理的资源!");
			System.exit(0);
		}

		long start = System.currentTimeMillis();
		System.out.println("开始处理文件......");
		for (Source source : config.getSources()) {
			File sourceFile = FileHelper.getFile(source.getSrc());
			if (!sourceFile.exists()) {
				System.out.println("资源[" + source.getSrc() + "]不存在!");
				continue;
			}

			List<String> fileList = FileHelper.filterFile(sourceFile, source.getFileExt(), source.getRegex(),
					source.getIngorePath());
			for (String path : fileList) {
				System.out.println("--------------------------------------------");
				System.out.println("正在处理文件 : " + path);
				scanFile(path, config.getFileCharset());
				// System.out.println("--> " + path);
			}
		}
		long end = System.currentTimeMillis() - start;
		System.out.println("处理结束, 共耗时: " + end + " 毫秒");
	}

	// 修改单个文件的方法
	private static void scanFile(String filePath, String fileCharset) {
		// 获取文件编码格式
		String encoding = FileHelper.getFileCharset(filePath);
		// 读取文件
		List<String> fileList = FileHelper.readFileToList(filePath, encoding);

		List<String> contentList = new ArrayList<String>();
		boolean changed = false;
		String xPath = FileHelper.getPath(filePath);
		for (String line : fileList) {
			ArrayList<String> arrlist = getMatch(line);// 获取需要替换的路径列表
			String modLine = line;
			if (arrlist.size() > 0) {
				modLine = getModLine(xPath, line, arrlist);
			}
			if (!line.equals(modLine)) {
				changed = true;
			}
			contentList.add(modLine);
		}
		if (changed) {
			// 若有修改则将修改后的内容按原有的文件编码格式写回去
			if (fileCharset != null) {
				encoding = fileCharset;
			}
			FileHelper.writeListToFile(filePath, contentList, encoding);
			// FileHelper.writeListToFile(filePath, contentList, "UTF-8");
		}
	}

	// 进行修改
	private static String getModLine(String xPath, String line, ArrayList<String> arrlist) {
		for (String url : arrlist) {
			String newUrl = parseModUrl(xPath, url);
			if (!url.equals(newUrl)) {
				line = line.replace(url, newUrl);
				System.out.println("    【成功】");
			} else {
				System.out.println("    【未修改】");
			}
		}
		return line;
	}

	// 修改版本号
	private static String parseModUrl(String xPath, String url) {
		try {
			String[] urls = url.split("\\?");
			String path = urls[0];
			String oldVersion = config.getVersionTag();
			if (urls.length > 1) {
				oldVersion = urls[1];
			}
			String fullPath = getRelativePath(xPath, path, config.getTags());
			System.out.println("    fullPath = " + fullPath);
			File file = new File(fullPath);
			if (!file.isFile()) {
				System.out.println("    错误：引用文件未找到！ " + url);
				System.out.println("    错误路径：" + fullPath);
				return url;
			}
			// 获取文件的当前hash
			String curHash = FileHelper.getCheckSum(file); // HASH规则主要的逻辑
			// System.out.println(file + curHash);
			String version = getVersion(curHash);
			// 获取文件修改时间
			String dd = FileHelper.getFileLastModTime(file, "yyyy-MM-dd HH:mm:ss");
			if (!version.equals(oldVersion)) {
				url = path + "?" + version;
			}
			System.out.print("    " + url + " 修改时间: " + dd);
			return url;
		} catch (Exception e) {
			System.out.println("    错误：文件版本命名格式不对！" + url);
			return url;
		}
	}

	/**
	 * 计算版本号
	 * @param config
	 * @param hash
	 * @return
	 */
	private static String getVersion(String hash) {
		return config.getVersionTag() + "=" + config.getMainVersion() + config.getVersionConnector() + hash;
	}

	// 获取文件路径
	private static String getRelativePath(String xPath, String path, List<Tag> tagList) {
		path.trim();
		if (path.indexOf("../") > -1) {
			String[] paths = xPath.split("\\\\");
			StringBuffer p = new StringBuffer();
			Pattern pattern = Pattern.compile("\\.\\./");
			Matcher match = pattern.matcher(path);
			int x = 0;
			while (match.find()) {
				x++;
			}
			path = path.replace("../", "");
			for (int i = 0; i < paths.length - x; i++) {
				p.append(paths[i]);
				p.append("\\");
			}
			xPath = p.toString();
		}

		if (path.startsWith("http://")) {
			xPath = "";
		} else if (path.startsWith("./")) {
			path = path.replace("./", "");
		} else if (path.startsWith("/")) {
			xPath = config.getWebappRoot();
		}

		// 处理标记，如: ${RESOUCE_STATIC_URL}
		if (tagList != null) {
			for (Tag tag : tagList) {
				if (tag.getName() != null && tag.getValue() != null) {
					if (path.startsWith("${" + tag.getName() + "}")) {
						xPath = "";
					}
					path = path.replace("${" + tag.getName() + "}", tag.getValue());
				}
			}
		}

		String fullPath = xPath + path.replace("/", "\\");

		// 处理WEB-INF
		int find = fullPath.toLowerCase().indexOf("web-inf");
		if (find != -1) {
			fullPath = fullPath.substring(0, find) + fullPath.substring(find + 8);
		}

		return fullPath;
	}

	private static ArrayList<String> getMatch(String line) {
		ArrayList<String> arrlist = new ArrayList<String>(0);
		for (Match match : config.getMatchs()) {
			arrlist.addAll(find(line, match));
		}
		return arrlist;
	}

	private static ArrayList<String> find(String line, Match match) {
		Pattern pattern = Pattern.compile(match.getRegex());
		Matcher matcher = pattern.matcher(line);
		ArrayList<String> arrlist = new ArrayList<String>(0);
		while (matcher.find()) {
			String url = matcher.group(match.getMatchIndex());
			url = filterSet(matcher.group(0), url);
			System.out.println("--> " + url);
			// 文件对应类型验证
			if (fileTypeVal(url, match.getFileExt())) {
				arrlist.add(url);
			}
		}
		return arrlist;
	}

	// 验证文件类型
	private static boolean fileTypeVal(String url, String fileTypes) {
		url = url.toLowerCase();
		String tx = url.split("\\?")[0];

		String[] fileType = fileTypes.split(",");
		for (String type : fileType) {
			if (tx.endsWith("." + type)) {
				return true;
			}
		}
		System.out.println("错误:文件类型不匹配" + url);
		return false;
	}

	private static String REG_JSP_URL_START = "<c:url";
	private static String REG_JSP_URL = "<c:url(.*)? value=\"([^\"]+)\"";
	private static String REG_JSP_URL2 = "<c:url(.*)? value='([^\"]+)'";

	// 识别<c:set url 的情况
	private static String filterSet(String line, String url) {
		if (url.indexOf(REG_JSP_URL_START) > -1) {
			Pattern pattern = Pattern.compile(REG_JSP_URL);
			Matcher match = pattern.matcher(line);
			if (match.find()) {
				url = match.group(2);
			} else {
				pattern = Pattern.compile(REG_JSP_URL2);
				match = pattern.matcher(line);
				if (match.find()) {
					url = match.group(2);
				}
			}
		}
		return url;
	}
}
