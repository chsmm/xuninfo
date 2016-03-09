package com.xuninfo.zh.domain;

public class Question {
	
	private String qid;
	private String qtitle;
	private String qurl;
	private String qanswerNum;
	private String qvisitsCount;
	private String qisTopQuestion;
	private String qtopic;
	private String qtags;
	private String qvoteNum;
	private String qfollowers;
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public String getQtitle() {
		return qtitle;
	}
	public void setQtitle(String qtitle) {
		this.qtitle = qtitle;
	}
	public String getQurl() {
		return qurl;
	}
	public void setQurl(String qurl) {
		this.qurl = qurl;
	}
	public String getQanswerNum() {
		return qanswerNum;
	}
	public void setQanswerNum(String qanswerNum) {
		this.qanswerNum = qanswerNum;
	}
	public String getQvisitsCount() {
		return qvisitsCount;
	}
	public void setQvisitsCount(String qvisitsCount) {
		this.qvisitsCount = qvisitsCount;
	}
	public String getQisTopQuestion() {
		return qisTopQuestion;
	}
	public void setQisTopQuestion(String qisTopQuestion) {
		this.qisTopQuestion = qisTopQuestion;
	}
	public String getQtopic() {
		return qtopic;
	}
	public void setQtopic(String qtopic) {
		this.qtopic = qtopic;
	}
	public String getQtags() {
		return qtags;
	}
	public void setQtags(String qtags) {
		this.qtags = qtags;
	}
	public String getQvoteNum() {
		return qvoteNum;
	}
	public void setQvoteNum(String qvoteNum) {
		this.qvoteNum = qvoteNum;
	}
	public String getQfollowers() {
		return qfollowers;
	}
	public void setQfollowers(String qfollowers) {
		this.qfollowers = qfollowers;
	}
	
	@Override
	public String toString() {
		return "Question [qid=" + qid + ", qtitle=" + qtitle + ", qurl=" + qurl
				+ ", qanswerNum=" + qanswerNum + ", qvisitsCount="
				+ qvisitsCount + ", qisTopQuestion=" + qisTopQuestion
				+ ", qtopic=" + qtopic + ", qtags=" + qtags + ", qvoteNum="
				+ qvoteNum + ", qfollowers=" + qfollowers + "]";
	}
}
