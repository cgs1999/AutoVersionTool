package com.duoduo.autoversiontool.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.duoduo.autoversiontool.model.Config;
import com.duoduo.autoversiontool.model.Match;
import com.duoduo.autoversiontool.model.Source;
import com.duoduo.autoversiontool.model.Tag;
import com.duoduo.autoversiontool.model.Task;

/**
 * XML配置解析帮助类
 * @author chengesheng@gmail.com
 * @date 2014-1-17 下午5:04:22
 */
public class XmlParseHelper {

	/**
	 * 解析配置文件
	 * @param file
	 * @return
	 */
	public static Config parseXml(File file) {
		Config config = new Config();
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			Element root = document.getRootElement();

			parseConfig(config, root);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return config;
	}

	/**
	 * 解析Config
	 * @param config
	 * @param parent
	 */
	private static void parseConfig(Config config, Element parent) {
		parseTask(config, parent);

		String fileCharset = getNodeData(parent, "fileCharset", "value");
		if (fileCharset != null) {
			config.setFileCharset(fileCharset);
		}
		parseSourceList(config, getNodeChildrenByTag(parent.element("sources"), "source"));
	}

	/**
	 * 解析Task
	 * @param task
	 * @param element
	 */
	private static void parseTask(Task task, Element element) {
		task.setWebappRoot(getNodeData(element, "webappRoot", "value"));
		task.setMainVersion(getNodeData(element, "mainversion", "value"));
		task.setVersionTag(getNodeData(element, "versiontag", "value"));
		task.setVersionConnector(getNodeData(element, "versionconnector", "value"));

		parseTagList(task, getNodeChildrenByTag(element.element("tags"), "tag"));
		parseMatchList(task, getNodeChildrenByTag(element.element("matchs"), "match"));
	}

	/**
	 * 解析Source
	 * @param config
	 * @param element
	 */
	private static void parseSourceList(Config config, List<?> elements) {
		ArrayList<Source> sourceList = new ArrayList<Source>(0);
		for (int i = 0; i < elements.size(); i++) {
			Source source = new Source();
			Element element = (Element) elements.get(i);

			// System.out.println("node=[" + element.toString() + "]");
			// System.out.println("node name=[" + element.getName() + "]");

			String src = getNodeData(element, "src");
			if (src == null) {
				// 跳过
				System.out.println("错误：指定资源不存在src属性，不处理该配置");
				continue;
			}
			source.setSrc(src);

			String fileExt = getNodeData(element, "fileExt");
			if (fileExt != null) {
				// 跳过
				source.setFileExt(fileExt);
			}

			String regex = getNodeData(element, "regex");
			if (regex != null) {
				source.setRegex(regex);
			}

			String ingorePath = getNodeData(element, "ingorePath");
			if (ingorePath != null) {
				source.setIngorePath(ingorePath);
			}

			if (element.elements() != null && !element.elements().isEmpty()) {
				// 存在子节点，则设置自定义标识
				source.setCustom(true);

				parseTask(source, (Element) element);
			}

			sourceList.add(source);
		}
		config.setSources(sourceList);
	}

	/**
	 * 解析Tag
	 * @param task
	 * @param element
	 */
	private static void parseTagList(Task task, List<?> elements) {
		ArrayList<Tag> tagList = new ArrayList<Tag>(0);
		for (int i = 0; i < elements.size(); i++) {
			Tag tag = new Tag();
			Element element = (Element) elements.get(i);

			// System.out.println("node=[" + element.toString() + "]");
			// System.out.println("node name=[" + element.getName() + "]");

			String name = getNodeData(element, "name");
			if (name == null) {
				// 跳过
				System.out.println("错误：指定资源不存在name属性，不处理该配置");
				continue;
			}
			tag.setName(name);

			String value = getNodeData(element, "value");
			if (value == null) {
				// 跳过
				System.out.println("错误：指定资源不存在value属性，不处理该配置");
				continue;
			}
			tag.setValue(value);

			tagList.add(tag);
		}
		task.setTags(tagList);
	}

	/**
	 * 解析Match List
	 * @param task
	 * @param element
	 */
	private static void parseMatchList(Task task, List<?> elements) {
		ArrayList<Match> matchList = new ArrayList<Match>(0);
		for (int i = 0; i < elements.size(); i++) {
			Match match = new Match();
			Element element = (Element) elements.get(i);

			// System.out.println("node=[" + element.toString() + "]");
			// System.out.println("node name=[" + element.getName() + "]");

			String regex = getNodeText((Element) element, "regex");
			if (regex == null) {
				// 跳过
				System.out.println("错误：指定资源不存在regex属性，不处理该配置");
				continue;
			}
			match.setRegex(regex);

			String matchIndex = getNodeText((Element) element, "matchIndex");
			int index = (matchIndex == null) ? 1 : Integer.valueOf(matchIndex);
			match.setMatchIndex(index);

			String fileExt = getNodeText((Element) element, "fileExt");
			if (fileExt == null) {
				// 跳过
				System.out.println("错误：指定资源不存在fileExt属性，不处理该配置");
				continue;
			}
			match.setFileExt(fileExt);

			matchList.add(match);
		}
		task.setMatchs(matchList);
	}

	/**
	 * 获取父节点下的指定标签的第一个节点
	 * @param parentElement
	 * @param tagName
	 * @return
	 */
	private static Element getNodeByTag(Element parentElement, String tagName) {
		try {
			return parentElement.element(tagName);
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 获取父节点下的指定标签的第一个节点的子节点列表
	 * @param parent
	 * @param tagName
	 * @return
	 */
	private static List<?> getNodeChildrenByTag(Element parent, String tagName) {
		try {
			return parent.elements();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取父节点下的指定标签的第一个节点的指定属性
	 * @param parentElement
	 * @param tagName
	 * @param propertyName
	 * @return
	 */
	private static String getNodeData(Element parentElement, String tagName, String propertyName) {
		return getNodeData(getNodeByTag(parentElement, tagName), propertyName);
	}

	/**
	 * 获取父节点下的指定标签的第一个节点的文本
	 * @param parentElement
	 * @param tagName
	 * @return
	 */
	private static String getNodeText(Element parentElement, String tagName) {
		Element element = getNodeByTag(parentElement, tagName);
		if (element == null) {
			return null;
		}
		return element.getText();
	}

	/**
	 * 获取节点的指定属性值
	 * @param node
	 * @param propertyName
	 * @return
	 */
	private static String getNodeData(Element element, String propertyName) {
		if (element == null) {
			return null;
		}
		return element.attributeValue(propertyName);
	}

	public static void main(String[] args) {
		String configFile = "config.xml";
		if (args.length > 0) {
			configFile = args[0];
		}

		File file = new File(configFile);
		if (!file.exists()) {
			String userDir = System.getProperty("user.dir");
			if (!userDir.endsWith(File.separator)) {
				configFile = userDir + File.separator + configFile;
			} else {
				configFile = userDir + configFile;
			}

			file = new File(configFile);
			if (!file.exists()) {
				System.out.println("错误：配置文件不存在！");
				System.exit(0);
			}
		}

		XmlParseHelper.parseXml(file);
	}
}
