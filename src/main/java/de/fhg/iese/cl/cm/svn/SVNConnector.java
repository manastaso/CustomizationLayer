package de.fhg.iese.cl.cm.svn;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.Instance;
import de.fhg.iese.cl.InstanceSet;
import de.fhg.iese.cl.cm.*;
import de.fhg.iese.cl.cm.CMErrorMessage.CMErrorType;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;

import java.io.*;
import java.util.*;

public class SVNConnector implements CMAbstractionLayer {
	
	boolean run=true;
	private CommitEventHandler myCommitEventHandler;
	
	private CustomizationLayer cl;

	private static SVNClientManager theClientManager;
	
	public static CMAbstractionLayer init() {
		SVNConnector thisConnector = new SVNConnector();
		return thisConnector;
	}

	private SVNRepository repository=null;

	private SVNURL repositoryURL=null;

	public void addDir(String repoPath, String annotation) throws CMException {		
		if (repoPath.equals("/"))
			return;
		
		SVNURL repoPathURL;
		SVNURL[] repoPathURLArray = new SVNURL[1];
		try {
			repoPathURL = SVNURL.parseURIDecoded(repository.getRepositoryRoot(true)+repoPath);
			repoPathURLArray[0] = repoPathURL;
			SVNCommitClient myCommitClient = theClientManager.getCommitClient();
			myCommitClient.setEventHandler(myCommitEventHandler);
			myCommitClient.doMkDir(repoPathURLArray, annotation, null, true);
		}
		catch (SVNException e) {
			if ((e.getErrorMessage().getErrorCode().equals(SVNErrorCode.FS_ALREADY_EXISTS)) &&
			   (e.getErrorMessage().getErrorCode().equals(SVNErrorCode.RA_DAV_ALREADY_EXISTS)))
				throwCMException(e);
		}
	}

	public CMCommitMessage commitItem(File wcPath, boolean keepLocks, String commitMessage)
	throws CMException {
		CMCommitMessage result = null; 
		try {
			SVNCommitInfo info = theClientManager.getCommitClient().doCommit(new File[] { wcPath }, keepLocks,
							 commitMessage, false, true);
			result = createCMCommitMessage(info);
		}
		catch (Exception e) {
			throwCMException(e);
		}
		return result;
	}

	private SVNCommitInfo copy(SVNURL srcURL, SVNRevision srcRevision, SVNURL dstURL, String commitMessage, boolean isMove) throws SVNException {
		SVNCopySource[] copySrc = new SVNCopySource[1];
		copySrc[0] = new SVNCopySource(srcRevision,srcRevision,srcURL);
		return theClientManager.getCopyClient().doCopy(copySrc, dstURL, isMove, false, false, commitMessage, null);
	}

	public CMCommitMessage copyItemWithTag(String sourcePath,
			long sourceRevision, String dstPath, String annotation, String targetTag, boolean isMove)
			throws CMException {
		
		CMCommitMessage result=null;
		SVNCommitInfo resultCommitInfo;
		
		//if revision is <0 we take the last revision
		if (sourceRevision < 0)
			sourceRevision = getLatestItemRevisionSinceTag(sourcePath,null);
		else
			if ( !isItemRevisionValid(sourcePath, sourceRevision) )  
				throw new CMException("The specified revision is not valid");
		
		if ( !hasItemRevisionTag(sourcePath, sourceRevision, annotation))
			throw new CMException("The specified asset:"+sourcePath+" is not marked with:"+annotation);
		
		SVNRevision revision = SVNRevision.create(sourceRevision);

		try {
			SVNURL coreAssetPathURL = SVNURL.parseURIDecoded(repository.getRepositoryRoot(true)+sourcePath);
			SVNURL instancePathURL = SVNURL.parseURIDecoded(repository.getRepositoryRoot(true)+dstPath);
			resultCommitInfo = copy(coreAssetPathURL, revision, instancePathURL, targetTag, isMove);
			result = createCMCommitMessage(resultCommitInfo);
		} catch (Exception e) {
			throwCMException(e);
		}

		return result;
	}
	
	private long countChangesSinceRevision(String path, long revision) throws CMException {
		long res=0;
		Collection<?> logEntries;
		try {
			logEntries = getRepository().log(new String[] {path}, 
													  null, getLatestRepositoryRevision(), 
													  revision, true, true);
			for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
				checkCancelled();
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();
				if ((logEntry.getChangedPaths().size() > 0)) {
					Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();
					for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths
					.hasNext();) {
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
						.getChangedPaths().get(changedPaths.next());
						if (entryPath.getType() == SVNLogEntryPath.TYPE_MODIFIED)
							res += 1;
					}
				}
			}				
		} catch (Exception e) {
			throwCMException(e);
		}
		return res;
	}

	private CMCommitMessage createCMCommitMessage(SVNCommitInfo info) {
		CMCommitMessage result;
		SVNErrorMessage svnErrMsg = info.getErrorMessage();
		CMErrorMessage errMsg = null;
		if (svnErrMsg != null) {
			CMErrorType errorType;
			switch (svnErrMsg.getType()) {
			case SVNErrorMessage.TYPE_ERROR:
				errorType = CMErrorType.TYPE_ERROR;
				break;
			case SVNErrorMessage.TYPE_WARNING:
				errorType = CMErrorType.TYPE_WARNING;
				break;
			default:
				errorType = CMErrorType.UNKNOWN_ERROR_MESSAGE;
				break;
			}
				
			errMsg = new CMErrorMessage(svnErrMsg.getFullMessage(),errorType);
		}
		
		Long newRevision = new Long(info.getNewRevision());
		
		result = new CMCommitMessage(info.getDate(),info.getAuthor(),newRevision.toString(),errMsg);
		return result;
	}
	
	public void dispose() {
		if (theClientManager != null)
			theClientManager.dispose();
		run=false;
	}
	
	public ArrayList<Instance> getBranchedItemsWithTag(String path,
			long revision, String branchTag, String controlTag) throws CMException {
		
		InstanceSet instances = new InstanceSet();
		long startRevision = getLatestRepositoryRevision();
		long endRevision = 1;
		boolean checkRevision =  revision < 0 ? false : true; 
		
		Collection<?> logEntries;
		try {
			
			logEntries = getRepository().log(null, null,
					startRevision, endRevision, true, true);
			for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
				checkCancelled();
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();
				long coreAssetRevision = logEntry.getRevision();

				if ((logEntry.getChangedPaths().size() > 0) && logEntry.getMessage().startsWith(branchTag)) {
					Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();
					for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths
					.hasNext();) {
						/*
						 * obtains a next SVNLogEntryPath
						 */
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
						.getChangedPaths().get(changedPaths.next());
						
						String copyPath = prepareRepoPath(entryPath.getCopyPath(),false,0);
						long copyRevision = entryPath.getCopyRevision();
						
						if ((copyPath != null) && (copyPath.startsWith(path)) && (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED))
						{
								if (checkRevision)
								{
									if (copyRevision == revision)
									{
										Instance i = new Instance(entryPath.getPath(),coreAssetRevision,copyPath,entryPath.getCopyRevision());
										instances.add(i);
									}
								}								
								else
								{
										Instance i = new Instance(entryPath.getPath(),coreAssetRevision,copyPath,entryPath.getCopyRevision());
										instances.add(i);
								}
						}
					}
				}
			}

		} catch (Exception e) {
			throwCMException(e);
		}
		return instances;
	}
	
	private String getCoreAssetRoot() throws SVNException {
		return getRepositoryRoot() + "/trunk";
	}
	
	private String getInstanceRoot() throws SVNException {
		return getRepositoryRoot() + "/branches";
	}

	public ArrayList<String> getItemsWithTag(long revision, String dirPath, String tag, boolean onlyDirs) throws CMException {
		
		Collection<?> logEntries=null;
		ArrayList<String> itemList = new ArrayList<String>();
		try {
			logEntries = getRepository().log(new String[] {""}, 
													  null,
													  revision, 1, true, true);
		for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
			checkCancelled();
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();

			if ((logEntry.getChangedPaths().size() > 0) && logEntry.getMessage().equals(tag)) {
				Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();

				for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths
				.hasNext();) {
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
					String coreAssetPath = entryPath.getPath();
					if ( (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED) &&
						 !(entryPath.getPath().contains("_temp_cl"))
					   )
					{
					if (onlyDirs) {
						try {
							SVNNodeKind nodeKind = repository.checkPath(coreAssetPath, revision);
							if ((coreAssetPath.startsWith(dirPath)) && (nodeKind.equals(SVNNodeKind.DIR)))
									itemList.add(entryPath.getPath());
						} catch (Exception e) {
							throwCMException(e);
						}	
					}
					else
						if (coreAssetPath.startsWith(dirPath))
								itemList.add(entryPath.getPath());
					}
				}
			}
		}
		}
		catch (Exception e) {
			throwCMException(e);
		}
		
		return itemList;
	}

	public long getLatestItemRevisionSinceTag(String path, String tag) throws CMException {
		long res=-1;
		Collection<?> logEntries;
		try {
			logEntries = getRepository().log(new String[] {path}, 
													  null, getLatestRepositoryRevision(), 
													  1, true, true);
			for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
				checkCancelled();
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();
				if (tag != null)
				{
					if (logEntry.getMessage().startsWith(tag))
						return logEntry.getRevision();
				}
				else
					return logEntry.getRevision();
			}
		} catch (Exception e) {
			throwCMException(e);
		}
		return res;
	}
	
	public long getLatestRepositoryRevision() throws CMException {
		long res=-1;
		try {
			res = getRepository().getLatestRevision();
		} catch (Exception e) {
			throwCMException(e);
		}
		return res;
	}

	public Observer getObserver() {
		return myCommitEventHandler;
	}

	public File getPropertyFile() throws CMException {
		File propertyFile = new File("properties.txt");
		try {
			propertyFile.createNewFile();
		} catch (IOException e) {
			throwCMException(e);
		}
		return propertyFile;
	}
		
	/**
	 * @param absolutePath
	 * @param rootPath
	 * @return
	 */
	private String getRelativePath(String absolutePath, String rootPath) {
		String relativePath=null;
		
		relativePath = absolutePath.substring(rootPath.length(),absolutePath.length());
		
		relativePath = relativePath.replace(File.separatorChar, '/');
		
		return relativePath;
	}
	
	public SVNRepository getRepository() throws CMException{
		if (repository == null)
			throwCMException(new Exception("Repository not set"));
		return repository;
	}
	
	private String getRepositoryRoot() throws SVNException {
		String result = repositoryURL.toString().replace(repository.getRepositoryRoot(true).toString(),"");
		return result;
	}
	
	public SVNURL getRepositoryURL() throws CMException{
		if (repositoryURL == null)
			throwCMException(new Exception("Repository URL not set"));
		return repositoryURL;
	}
	
	private boolean hasItemRevisionTag(String coreAssetPath, long coreAssetRevision,String tag) throws CMException {
		
		boolean result = false;
		
		Collection<?> logEntries;
		try {
			coreAssetPath = prepareRepoPath(coreAssetPath, false,0);
			
			logEntries = getRepository().log(new String[] {""}, 
													  null, coreAssetRevision, 
													  1, true, true);
			for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
				checkCancelled();
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();

				if ((logEntry.getChangedPaths().size() > 0) && logEntry.getMessage().equals(tag)) {
					Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();

					for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths
					.hasNext();) {
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
						String entryPathString = entryPath.getPath();
						if (entryPathString.equals(coreAssetPath))
							return true;
						else
							result = false;
					}
				}
			}
		} catch (Exception e) {
			throwCMException(e);
		}
		
		return result;
	}

	public CMCommitMessage importItem(File item, String repoPath, String tag, boolean markAllContents)
			throws CMException {
		
		CMCommitMessage result = null;
		SVNCommitInfo commitInfo = null;
		
		String coreAssetAbsolutePath = item.getAbsolutePath();
		String coreAssetRootDirectory = item.getParent();

		String relativePath = getRelativePath(coreAssetAbsolutePath, coreAssetRootDirectory);
		
		SVNURL repoPathURL;
		try {
			repoPathURL = SVNURL.parseURIDecoded(repository.getRepositoryRoot(true)+repoPath+relativePath);
			SVNCommitClient myCommitClient = theClientManager.getCommitClient();
			myCommitClient.setEventHandler(myCommitEventHandler);
			if (markAllContents)
				 commitInfo = myCommitClient.doImport(item, repoPathURL, tag, true);
			else {
				 if (item.isFile())
					 throwCMException(new CMException("cannot add a file like that"));
				 addDir(repoPath+relativePath, tag);
				 commitInfo = myCommitClient.doImport(item, repoPathURL, "[CL] adding unmarked core asset contents", true);
			}
			result = createCMCommitMessage(commitInfo);
		} catch (Exception e) {
			throwCMException(e);
		}
		
		return result;
	}

	private boolean isItemRevisionValid(String coreAssetPath, long coreAssetRevision) throws CMException {
		boolean res=false;
		Collection<?> logEntries;
		try {
			logEntries = getRepository().log(new String[] {coreAssetPath}, 
					null, getLatestRepositoryRevision(), 
					1, true, true);
			for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
				checkCancelled();
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();

				if (logEntry.getRevision() == coreAssetRevision)
					return true;
			}
		} catch (Exception e) {
			throwCMException(e);
		}

		return res;
	}

	public long itemHasChanged(String path, long revision) throws CMException {
		
		long changesCount = countChangesSinceRevision(path, revision);
		
		return changesCount;
	}

	/**
	 * @param repoPath
	 * @return
	 * @throws SVNException 
	 */
	public String prepareRepoPath(String repoPath, boolean withTailingSlash, int type) throws CMException {
				
		String result=null;

		int length=0;

		if ((repoPath == null) || (repoPath.equals("/")))
			result = new String();
		else
			if (repoPath.length()==0)
				result = new String();
			else
				if ((repoPath != null) && (repoPath.length()>0))
				{
					length = repoPath.length();
					if (withTailingSlash)
					{
						if (repoPath.charAt(length-1) != '/')
							result = repoPath + "/";
						else 
							result = repoPath;
					}
					else
						if (repoPath.charAt(length-1) == '/') {
							result = repoPath.substring(0, length-1);
						}
						else
							result = repoPath;
					if (repoPath.charAt(0) != '/') {
						result = "/" + result;
					}
				}
		try {
			switch (type) {
			case 1:
				if (!result.startsWith("/trunk"))
					result = getCoreAssetRoot() + result;
				else
					result = getRepositoryRoot() + result;
				break;
			case 2:
				if (!result.startsWith("/branches"))
					result = getInstanceRoot() + result;
				else
					result = getRepositoryRoot() + result;
				break;
			case 3:
				if (!result.startsWith(getRepositoryRoot()))
					result = getRepositoryRoot() + result;
			default:
				break;
			}	
		}
		catch (Exception e) {
			throwCMException(e);
		}
			
		return result;
	}

	//TODO check jre compatibility
	public RepositoryProperties readProperties(boolean decrypt) throws CMException {
		String username = null;
		String password = null;
		String repositoryURL = null;
		String repositoryProxyURL = null;
		String repositoryProxyPort = null;
		
		RepositoryProperties repoProperties = null;
		//Reader reader;
		FileInputStream readerStream = null;
		try {
			File propertyFile = new File("properties.txt");
			propertyFile.createNewFile();
			readerStream = new FileInputStream(propertyFile);
			Properties p = new Properties();
			p.load(readerStream);
			if (!p.isEmpty())
			{
			 username = p.getProperty("username");
			 if (decrypt) {
				 DesEncrypter encrypter = new DesEncrypter("CustomizationLayer");
				 password = encrypter.decrypt(p.getProperty("password"));	 
			 }
			 else
				 password = p.getProperty("password");
			 
			 repositoryURL = p.getProperty( "repositoryURL");
			 repositoryProxyURL = p.getProperty( "repositoryProxyURL");
			 repositoryProxyPort = p.getProperty( "repositoryProxyPort");
			 repoProperties = new RepositoryProperties(username, password, repositoryURL, 
													  repositoryProxyURL,repositoryProxyPort);
			}
		} catch (FileNotFoundException e1) {
			throwCMException(e1);
		} catch (IOException e2) {
			throwCMException(e2);
		}
		finally {
		    if (readerStream != null)
                try {
                    readerStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return repoProperties;
	}

	public void setCustomizationLayer(CustomizationLayer cl) {
		this.cl = cl;
	}

	public String setRepository(String url) throws CMException {
		
		String result=null;
		
		if (repository != null) {
			getRepository().closeSession();
		}
		try {
			repositoryURL = SVNURL.parseURIEncoded(url);
			String protocol = getRepositoryURL().getProtocol();
			if (protocol.startsWith("http"))
				DAVRepositoryFactory.setup();
			else
			if (protocol.equals("file"))
				FSRepositoryFactory.setup();
			else
			if (protocol.equals("svn"))
				SVNRepositoryFactoryImpl.setup();
			
			repository = SVNRepositoryFactory.create(repositoryURL);
			result = getRepository().toString();
		} catch (Exception e) {
			throwCMException(new Exception("Error while setting SVN repository URL:"+e.getMessage()));
		}
				
		return result;
	}
	
	public String setup() throws CMException {
		RepositoryProperties repoProperties = readProperties(true);
		String result = null;
		myCommitEventHandler = new CommitEventHandler(cl);

		if (repoProperties != null) 
		{
			result = setRepository(repoProperties.repositoryURL);
			
			DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
			ISVNAuthenticationManager myAuthManager = new CLAuthenticationManager(repoProperties.username, 
																				  repoProperties.password,
																				  repoProperties.repositoryProxyURL,
																				  repoProperties.repositoryProxyPort);
			repository.setAuthenticationManager(myAuthManager);
			
			try {
				theClientManager = SVNClientManager.newInstance(options,myAuthManager);
				addDir(getRepositoryRoot()+"/trunk", "creating trunk directory");
				addDir(getRepositoryRoot()+"/branches", "creating branches directory");
			} catch (Exception e) {
				throwCMException(new Exception("Error while setting up SVN access:"+e.getMessage()));
			}
		}
		else
			throwCMException(new Exception("No repository properties available"));
		
		return result;
	}

	//TODO check JRE compatibility
	public String storeProperties(RepositoryProperties props) throws CMException {
		
		//FileWriter writer = null;
		FileOutputStream writerStream = null;
		File propertyFile = new File("properties.txt");
		try 
		{ 
		  //writer = new FileWriter( propertyFile ); 
		  writerStream = new FileOutputStream(propertyFile);
		 
		  Properties p = new Properties(System.getProperties()); 
		  
		  p.setProperty( "repositoryURL", props.repositoryURL );
		  p.setProperty( "repositoryProxyURL", props.repositoryProxyURL );
		  p.setProperty( "repositoryProxyPort", props.repositoryProxyPort );
		  p.setProperty( "username", props.username );
		  
		  DesEncrypter encrypter = new DesEncrypter("CustomizationLayer");
		  p.setProperty("password", encrypter.encrypt(props.password));
		  
		  p.store(writerStream, "Repository Properties");
		  //p.store( writer, "Repository Properties" );
		  return "Repository properties stored under:" + propertyFile.getAbsolutePath();
		} 
		catch ( IOException e ) 
		{  
		 return e.getMessage();
		} 
		finally 
		{ 
		  try { /*writer.close();*/writerStream.close(); } catch ( IOException e ) {  return e.getMessage(); } 
		}
	}

	private void throwCMException(Exception e) throws CMException {
		Throwable cause = e.getCause();
		CMException exception = new CMException(e.toString(),cause);
		exception.setStackTrace(e.getStackTrace());
		throw exception;
	}

	public String traverseHistoryUntilFirstTag (String path, String tag) throws CMException {
		
		long revision = getLatestRepositoryRevision();
		String copyPath = null;
		
		try {
			if ( !hasItemRevisionTag(path, revision, tag) && (!path.equals(getRepositoryRoot())) )
				throw new CMException("The specified asset:"+path+" is not marked with:"+tag);
		} catch (Exception e1) {
			throwCMException(e1);
		}
		
		Collection<?> logEntries;
		try {
			logEntries = getRepository().log(new String[] {path}, null,
					revision, 1, true, false);
			for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
				checkCancelled();
				SVNLogEntry logEntry = (SVNLogEntry) entries.next();

				if ((logEntry.getChangedPaths().size() > 0) && logEntry.getMessage().startsWith(tag)) {
					Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();
					for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths
					.hasNext();) {
						/*
						 * obtains a next SVNLogEntryPath
						 */
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
						.getChangedPaths().get(changedPaths.next());
						
						copyPath = prepareRepoPath(entryPath.getCopyPath(),false,0);
					}
				}
			}

		} catch (Exception e) {
			throwCMException(e);
		}
		
		return copyPath;
	}
	
	public void checkCancelled() throws SVNCancelException {
    	if (!run) throw new SVNCancelException();
    }
}
