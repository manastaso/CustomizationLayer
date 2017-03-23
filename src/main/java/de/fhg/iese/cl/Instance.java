package de.fhg.iese.cl;

/**
 * @author  anastaso
 */
public class Instance {

	private String path;
	private long revision;
	private String copyPath;
	private long copyRevision;

	public Instance(String path, long revision, String copyPath,
			long copyRevision) {
		this.path = path;
		this.revision = revision;
		this.copyPath = copyPath;
		this.copyRevision = copyRevision;
	}
	
	public String toString() {
		return path+" [instance revision="+revision+"] [from "+ copyPath +"]";
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public String getCopyPath() {
		return copyPath;
	}

	public void setCopyPath(String copyPath) {
		this.copyPath = copyPath;
	}

	public long getCopyRevision() {
		return copyRevision;
	}

	public void setCopyRevision(long copyRevision) {
		this.copyRevision = copyRevision;
	}

}
