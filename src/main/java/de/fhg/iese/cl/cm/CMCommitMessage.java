package de.fhg.iese.cl.cm;

import java.util.Date;

import de.fhg.iese.cl.cm.CMErrorMessage.CMErrorType;

/**
 * @author anastaso
 *
 */
public class CMCommitMessage {
	
	/**
	 * 
	 */
	private Date dateOfCommit;
	
	/**
	 * 
	 */
	private String author;
	
	/**
	 * 
	 */
	private String revision;
	
	/**
	 * 
	 */
	private CMErrorMessage errorMsg;

	/**
	 * @param dateOfCommit
	 * @param author
	 * @param revision
	 * @param errorMsg
	 */
	public CMCommitMessage(Date dateOfCommit, String author, String revision,
			CMErrorMessage errorMsg) {
		super();
		this.dateOfCommit = dateOfCommit;
		this.author = author;
		this.revision = revision;
		this.errorMsg = errorMsg;
	}

	/**
	 * @param errorMsg
	 */
	public CMCommitMessage(String errorMsg) {
		this.errorMsg = new CMErrorMessage(errorMsg,CMErrorType.TYPE_ERROR);
	}

	/**
	 * @return
	 */
	public Date getDateOfCommit() {
		return dateOfCommit;
	}

	/**
	 * @param dateOfCommit
	 */
	public void setDateOfCommit(Date dateOfCommit) {
		this.dateOfCommit = dateOfCommit;
	}

	/**
	 * @return
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * @param revision
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * @return
	 */
	public CMErrorMessage getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 */
	public void setErrorMsg(CMErrorMessage errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[CM Commit Message] date="+dateOfCommit+" author=" +author+ " new revision="+revision+" error=("+ errorMsg +")";
	}

}
