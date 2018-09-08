package org.agnitas.emm.core.action.operations;

public class ActionOperationServiceMail extends AbstractActionOperation {
    
	private static final long serialVersionUID = 1L;

	private String textMail;	
	private String subjectLine;
	private String toAdr;
	private int mailtype;
	private String htmlMail;	

	public ActionOperationServiceMail(String type) {
		super(type);
	}
	
	public String getTextMail() {
		return textMail;
	}

	public void setTextMail(String textMail) {
		this.textMail = textMail;
	}

	public String getSubjectLine() {
		return subjectLine;
	}

	public void setSubjectLine(String subjectLine) {
		this.subjectLine = subjectLine;
	}

	public String getToAdr() {
		return toAdr;
	}

	public void setToAdr(String toAdr) {
		this.toAdr = toAdr;
	}

	public int getMailtype() {
		return mailtype;
	}

	public void setMailtype(int mailtype) {
		this.mailtype = mailtype;
	}

	public String getHtmlMail() {
		return htmlMail;
	}

	public void setHtmlMail(String htmlMail) {
		this.htmlMail = htmlMail;
	}

}
