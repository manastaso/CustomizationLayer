package de.fhg.iese.cl.cm.svn;

import java.io.File;

import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;

public class CLAuthenticationManager extends BasicAuthenticationManager {
	
	private String proxyHost;
	private String proxyPort;

	public CLAuthenticationManager(SVNAuthentication[] authentications) {
		super(authentications);
		// TODO Auto-generated constructor stub
	}

	public CLAuthenticationManager(String userName, String password, String proxyHost, String proxyPort) {
		super(userName, password);
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	public CLAuthenticationManager(String userName, File keyFile,
			String passphrase, int portNumber) {
		super(userName, keyFile, passphrase, portNumber);
	}

	@Override
	public String getProxyHost() {
		if (proxyHost.equals(""))
			return null;
		else
			return proxyHost;
	}

	@Override
	public int getProxyPort() {
		try {
			Integer i = new Integer(proxyPort);
			return i;
		}
		catch (Exception e)
		{
			return new Integer(0);
		}
		
	}

}
