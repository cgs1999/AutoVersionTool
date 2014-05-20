package com.duoduo.autoversiontool.model;

/**
 * 标签信息，文件中如"${RESOUCE_STATIC_URL}"的标签
 * @author chengesheng@gmail.com
 * @date 2014-1-17 下午4:40:39
 */
public class Tag {

	/** 标签名 */
	private String name;
	/** 对应的值 */
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
