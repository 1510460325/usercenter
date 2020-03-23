package com.kelab.usercenter.dal.model;

public class NewsModel {

    private Integer id;

    private String title;

    private String content;

    private String picUrl;

    private Long pubTime;

    private Integer viewNum;

    private Integer publishUser;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public Long getPubTime() {
		return pubTime;
	}

	public void setPubTime(Long pubTime) {
		this.pubTime = pubTime;
	}

	public Integer getViewNum() {
		return viewNum;
	}

	public void setViewNum(Integer viewNum) {
		this.viewNum = viewNum;
	}

	public Integer getPublishUser() {
		return publishUser;
	}

	public void setPublishUser(Integer publishUser) {
		this.publishUser = publishUser;
	}
}