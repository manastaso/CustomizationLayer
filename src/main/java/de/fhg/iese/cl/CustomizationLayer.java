package de.fhg.iese.cl;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import de.fhg.iese.cl.cm.CMAbstractionLayer;
import de.fhg.iese.cl.cm.CMAddDirDialog;
import de.fhg.iese.cl.cm.CMCommitMessage;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import java.util.Observer;


/**
 * @author  anastaso
 */
public class CustomizationLayer{
	
	private CMAbstractionLayer cmConnector = CMAbstractionLayer.currentCONNECTOR;
	
	private ConsoleGUI console=null;
	
	private String outputUI = null;
	
	private String coreAssetTag = "[CL] core asset addition/integration";
	private String instanceTag = "[CL] core asset instantiation/rebase";
	
	private int addingDirSelection = -1;
	private String pathForDirDialog = null;

	/**
	 * @param c
	 * @throws SVNException
	 */
	public CustomizationLayer(ConsoleGUI c) {
		this.console = c;
		cmConnector.setCustomizationLayer(this);
	}
	
	/**
	 * @param fileName
	 * @param repoPath
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	public String addCoreAsset(String fileName, String repoPath) throws CMException, IOException {
		String result=null;
		
		File coreAssetFile = new File(fileName);
		
		if (!coreAssetFile.exists())
			throw new IOException("File not found:"+fileName);
		if (coreAssetFile.isDirectory()) {
			repoPath = cmConnector.prepareRepoPath(repoPath,true,1);
			pathForDirDialog = coreAssetFile.getAbsolutePath();
			
			console.getDisplay().syncExec(new Runnable() {
				public void run() {
					CMAddDirDialog d = new CMAddDirDialog(console.getSShell(),pathForDirDialog);
					addingDirSelection = d.getSelection();
				}
			});
			switch (addingDirSelection) {
			case -1:
				return "aborted by user";
			case 0: //everything as core asset
				return addCoreAssetDirectory(coreAssetFile,repoPath,true);
			case 1: //only directory as core asset
				return addCoreAssetDirectory(coreAssetFile,repoPath,false);
			default:
				break;
			}
		}
		if (coreAssetFile.isFile()){
			repoPath = cmConnector.prepareRepoPath(repoPath,true,1);
			result = addCoreAssetFile(coreAssetFile,repoPath);
		}
			
		return result;
	}
	
	/**
	 * @param coreAssetDirectory
	 * @param repoPath
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	private String addCoreAssetDirectory(File coreAssetDirectory, String repoPath, boolean allAsCoreAssets) throws CMException, IOException {
		CMCommitMessage result=null;
		
		cmConnector.addDir(repoPath,"[CL] core asset addition/integration");
		
		if (allAsCoreAssets)
			result = cmConnector.importItem(coreAssetDirectory,repoPath,"[CL] core asset addition/integration",true);
		else
			result = cmConnector.importItem(coreAssetDirectory,repoPath,"[CL] core asset addition/integration",false);
		return result.toString();
	}
	
	/**
	 * @param coreAssetFile
	 * @param repoPath
	 * @return
	 * @throws SVNException 
	 * @throws IOException 
	 */
	private String addCoreAssetFile(File coreAssetFile, String repoPath) throws CMException, IOException {
		
		CMCommitMessage result=null;
		
		cmConnector.addDir(repoPath, "[CL] core asset addition/integration");
	
		result = cmConnector.importItem(coreAssetFile,repoPath,"[CL] core asset addition/integration",true);

		return result.toString();
		
	}

	public String commit(String assetPath) throws CMException {
		File assetFile = new File(assetPath);
		CMCommitMessage commitInfo = cmConnector.commitItem(assetFile, false, "test");
		return commitInfo.toString();
	}

	public void finalize() {
		cmConnector.dispose();
	  }

	/**
	 * @param revision
	 * @param dirPath
	 * @return
	 * @throws SVNException
	 */
	public ArrayList<String> getCoreAssets(long revision, String dirPath, boolean onlyDirs) throws CMException {
		
		dirPath = cmConnector.prepareRepoPath(dirPath,true,3);
			
		
		//if revision is <0 we take the last revision
		if (revision<=0)
			revision = cmConnector.getLatestRepositoryRevision();
		
		return cmConnector.getItemsWithTag(revision, dirPath, coreAssetTag, onlyDirs);
	}

	public ArrayList<String> getCoreDiff(String instanceName) throws CMException {
		
		ArrayList<String> diffs = new ArrayList<String>();

		instanceName = cmConnector.prepareRepoPath(instanceName,false,2);
		
		ArrayList<Instance> allInstances = getInstances("/", -1);
		
		Instance foundInstance = null;
		for (Iterator<Instance> iterator = allInstances.iterator(); iterator.hasNext();) {
			Instance instance = (Instance) iterator.next();
			if (instance.getPath().equals(instanceName))
			{
				foundInstance = instance;
				break;
			}
		}
		
		if (foundInstance != null){
			String coreAssetPath = foundInstance.getCopyPath();
			long coreAssetRevision = foundInstance.getCopyRevision(); 
					
			long changesCount = cmConnector.itemHasChanged(coreAssetPath,coreAssetRevision);
			if (changesCount > 0)
				diffs.add(coreAssetPath + " has been changed "+changesCount+" time(s) since instantiation/integration");
			else
				diffs.add(coreAssetPath + " has no changes since instantiation/integration");
		}
		else 
			throw new CMException("The specified asset:"+instanceName+" is not marked with:"+instanceTag);
		
		return diffs;
	}

	public ArrayList<String> getInstanceDiff(String coreAssetName, long revision) throws CMException {
		
		coreAssetName = cmConnector.prepareRepoPath(coreAssetName,false,1);
		
		long latestIntegrationRevision = cmConnector.getLatestItemRevisionSinceTag(coreAssetName,coreAssetTag);
		
		ArrayList<Instance> instances = cmConnector.getBranchedItemsWithTag(coreAssetName, revision,instanceTag,coreAssetTag);
		
		if (instances.size() == 0)
			throw new CMException("no instances available");
		
		ArrayList<String> diffs = new ArrayList<String>(); 
		
		for (Iterator<Instance> iterator = instances.iterator(); iterator.hasNext();) {
			Instance instance = (Instance) iterator.next();
			
			long changesCount = cmConnector.itemHasChanged(instance.getPath(),latestIntegrationRevision);
			if (changesCount > 0)
				diffs.add(instance.toString() + " has been changed "+ changesCount + " time(s) since instantiation/rebase");
			else
				diffs.add(instance.toString() + " has no changes since instantiation/rebase");
		}
		
		return diffs;
	}

	/**
	 * @param coreAssetName
	 * @throws SVNException 
	 * @throws CMException 
	 * @throws Exception
	 */
	public ArrayList<Instance> getInstances(String coreAssetName, long revision) throws CMException {
		
		coreAssetName = cmConnector.prepareRepoPath(coreAssetName,false,3);
		
		return cmConnector.getBranchedItemsWithTag(coreAssetName, revision, instanceTag, coreAssetTag);
	}

	public Observer getObserver() {
		return cmConnector.getObserver();
	}
	
	public String instantiateCoreAsset(String coreAssetPath, long coreAssetRevision, String instancePath) throws CMException {
		
		coreAssetPath = cmConnector.prepareRepoPath(coreAssetPath,false,1);
		instancePath = cmConnector.prepareRepoPath(instancePath,true,2);
		
		String importAnnotation = "[CL] core asset addition/integration";
		String tag = "[CL] core asset instantiation/rebase";
		
		cmConnector.addDir(instancePath,"[CL] core asset instance path creation");
		
		CMCommitMessage res = cmConnector.copyItemWithTag(coreAssetPath, coreAssetRevision, instancePath,
				importAnnotation, tag, false);
		
		return res.toString();
	}

	/**
	 * @throws SVNException
	 */
	public String setup() throws CMException {
		return cmConnector.setup();
	}

	public CMAbstractionLayer getCmConnector() {
		return cmConnector;
	}
	
	public void outputToUI(String output) {
		outputUI= output.replaceAll("\n", "\r\n");
		console.getDisplay().syncExec(new Runnable() {
			public void run() {
				console.println(outputUI);
			}
		});
	}

	public String rebase(String instancePath) throws CMException {
		instancePath = cmConnector.prepareRepoPath(instancePath, false, 2);
		cmConnector.copyItemWithTag(instancePath, -1, instancePath + "_temp_cl", instanceTag, instanceTag, true);
		CMCommitMessage result = cmConnector.copyItemWithTag(instancePath + "_temp_cl", -1, instancePath, instanceTag, instanceTag, true);
		return result.toString();
	}

	public String integrate(String instancePath) throws CMException {
		instancePath = cmConnector.prepareRepoPath(instancePath, false, 2);
		String coreAssetPath = cmConnector.traverseHistoryUntilFirstTag(instancePath,instanceTag);
		System.out.println(coreAssetPath);
		cmConnector.copyItemWithTag(coreAssetPath, -1, coreAssetPath + "_temp_cl", coreAssetTag, coreAssetTag, true);
		CMCommitMessage result = cmConnector.copyItemWithTag(coreAssetPath + "_temp_cl", -1, coreAssetPath, coreAssetTag, coreAssetTag + " integrated from:"+instancePath, true);
		return result.toString();
	}
}