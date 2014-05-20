package com.duoduo.autoversiontool.model;

/**
 * 内容匹配规则
 * @author chengesheng@gmail.com
 * @date 2014-1-17 下午4:45:16
 */
public class Match {

	/** 匹配正则表达式 */
	private String regex;
	/** 匹配url的分组索引 */
	private int matchIndex;
	/** 处理的文件扩展名，可多个（用逗号分隔），eg："js,jsp,html,htm" */
	private String fileExt;

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public int getMatchIndex() {
		return matchIndex;
	}

	public void setMatchIndex(int matchIndex) {
		this.matchIndex = matchIndex;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}
}
