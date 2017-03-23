package de.fhg.iese.cl.cm;

import java.io.File;
import java.util.ArrayList;
import java.util.Observer;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.Instance;

/**
 * @author anastaso
 *
 */
public interface CMAbstractionLayer {
	
	/**
	 * 
	 */
	CMAbstractionLayer currentCONNECTOR = de.fhg.iese.cl.cm.svn.SVNConnector.init();

	/**
	 * @param repoPath
	 * @throws CMException
	 */
	public void addDir(String repoPath, String annotation) throws CMException;

	/**
	 * @param wcPath
	 * @param keepLocks
	 * @param commitMessage
	 * @return
	 * @throws CMException
	 */
	public CMCommitMessage commitItem(File wcPath, boolean keepLocks, String commitMessage) throws CMException;

	/**
	 * @param sourcePath
	 * @param sourceRevision
	 * @param destPath
	 * @param tag
	 * @return
	 * @throws CMException
	 */
	public CMCommitMessage copyItemWithTag(String sourcePath, long sourceRevision,
			String destPath, String annotation, String targetTag, boolean isMove) throws CMException;

	/**
	 * 
	 */
	public void dispose();

	/**
	 * @param coreAssetName
	 * @param revision
	 * @param tag
	 * @return
	 * @throws CMException
	 */
	public ArrayList<Instance> getBranchedItemsWithTag(String coreAssetName,
			long revision, String branchTag, String controlTag) throws CMException;
	
	/**
	 * @param revision
	 * @param dirPath
	 * @param tag
	 * @return
	 * @throws CMException
	 */
	public ArrayList<String> getItemsWithTag(long revision, String dirPath,
			String tag, boolean onlyDirs) throws CMException;

	/**
	 * @return
	 * @throws CMException
	 */
	public long getLatestRepositoryRevision() throws CMException;

	public Observer getObserver();

	/**
	 * @param coreAssetDirectory
	 * @param repoPath
	 * @param tag 
	 * @return
	 * @throws CMException
	 */
	public CMCommitMessage importItem(File item, String repoPath, String tag, boolean markAllContents) throws CMException;

	/**
	 * @param path
	 * @param revision
	 * @return
	 * @throws CMException
	 */
	public long itemHasChanged(String path, long revision)throws CMException;

	/**
	 * @param repoPath
	 * @param b
	 * @return
	 */
	public String prepareRepoPath(String repoPath, boolean tailingSlash, int type) throws CMException;
	
	/**
	 * @param url
	 * @return
	 * @throws CMException
	 */
	public String setRepository(String url) throws CMException;

	/**
	 * @throws CMException
	 */
	public String setup() throws CMException;
	
	public String storeProperties(RepositoryProperties props) throws CMException;
	
	public RepositoryProperties readProperties(boolean decrypt) throws CMException;
	
	public File getPropertyFile() throws CMException;
	
	public void setCustomizationLayer(CustomizationLayer cl);

	public long getLatestItemRevisionSinceTag(String coreAssetName, String tag) throws CMException;

	public String traverseHistoryUntilFirstTag(String path, String tag) throws CMException;
}
