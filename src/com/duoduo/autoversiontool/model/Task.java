package com.duoduo.autoversiontool.model;

import java.util.List;

/**
 * 处理任务
 * @author chengesheng@gmail.com
 * @date 2014-1-17 下午4:50:12
 */
public class Task {

	/** webapp根目录，用于检测相对路径资源 */
	private String webappRoot;
	/** 主版本号，eg: /main.js?t=1.0.0.0_20140117中的"1.0.0.0" */
	private String mainVersion;
	/** 版本号标签，eg: /main.js?t=1.0.0.0_20140117中的"t" */
	private String versionTag;
	/** 版本号连接符，eg: /main.js?t=1.0.0.0_20140117中的"_" */
	private String versionConnector;
	/** 标签列表 */
	private List<Tag> tags;
	/** 内容匹配列表 */
	private List<Match> matchs;

	public String getWebappRoot() {
		return webappRoot;
	}

	public void setWebappRoot(String webappRoot) {
		this.webappRoot = webappRoot;
	}

	public String getMainVersion() {
		return mainVersion;
	}

	public void setMainVersion(String mainVersion) {
		this.mainVersion = mainVersion;
	}

	public String getVersionTag() {
		return versionTag;
	}

	public void setVersionTag(String versionTag) {
		this.versionTag = versionTag;
	}

	public String getVersionConnector() {
		return versionConnector;
	}

	public void setVersionConnector(String versionConnector) {
		this.versionConnector = versionConnector;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Match> getMatchs() {
		return matchs;
	}

	public void setMatchs(List<Match> matchs) {
		this.matchs = matchs;
	}
}
