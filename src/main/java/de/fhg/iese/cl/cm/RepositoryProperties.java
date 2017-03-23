package de.fhg.iese.cl.cm;

public class RepositoryProperties {
	public String username = null;


	public String password = null;
	public String repositoryURL = null;
	public String repositoryProxyURL = null;

	public String repositoryProxyPort = null;
	public RepositoryProperties(String username, String password,
			String repositoryURL, String repositoryProxyURL,
			String repositoryProxyPort) {
		super();
		this.username = username;
		this.password = password;
		this.repositoryURL = repositoryURL;
		this.repositoryProxyURL = repositoryProxyURL;
		this.repositoryProxyPort = repositoryProxyPort;
	}
	
	
	@Override
	public String toString() {
		String s = "\nrepositoryURL = " + repositoryURL + "\n" +
				   "repositoryProxyURL = " + repositoryProxyURL + "\n" +
				   "repositoryProxyPort = " + repositoryProxyPort + "\n" +
				   "username = " + username + "\n" +
				   "password = " + password;
		return s;
	}
	
	
}
