package com.duoduo.autoversiontool.model;

import java.util.List;

/**
 * 配置信息
 * @author chengesheng@gmail.com
 * @date 2014-1-17 下午4:45:45
 */
public class Config extends Task {

	/** 处理后保存文件编码格式，缺省为null表示不更改文件编码格式，建议设置为UTF-8 */
	private String fileCharset = null;
	/** 处理的资源列表 */
	private List<Source> sources;

	public String getFileCharset() {
		return fileCharset;
	}

	public void setFileCharset(String fileCharset) {
		this.fileCharset = fileCharset;
	}

	public List<Source> getSources() {
		return sources;
	}

	public void setSources(List<Source> sources) {
		this.sources = sources;
	}
}
