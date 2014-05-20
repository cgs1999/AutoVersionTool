package com.duoduo.autoversiontool.model;

/**
 * 资源配置信息
 * @author chengesheng@gmail.com
 * @date 2014-1-17 下午4:42:57
 */
public class Source extends Task {

	/** 资源路径，可使用绝对路径或相对路径 */
	private String src;
	/** 资源过滤的文件扩展名，可多个（用逗号分隔），eg："js,jsp,html,htm" */
	private String fileExt;
	/** 资源过滤正则表达式 */
	private String regex;
	/** 忽略的资源路径，使用绝对路径，可多个（用逗号分隔） */
	private String ingorePath;

	/** 自定义规则标识，用于业务处理 */
	private boolean custom = false;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getIngorePath() {
		return ingorePath;
	}

	public void setIngorePath(String ingorePath) {
		this.ingorePath = ingorePath;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}
}
