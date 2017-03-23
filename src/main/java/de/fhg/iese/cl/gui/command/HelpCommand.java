package de.fhg.iese.cl.gui.command;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class HelpCommand extends Command {
	
	private final String helpText = "General Commands"+
	"\n\thelp - Show this help message"+
	"\nAdministrative Commands"+
	"\n\tset-properties- Set repository access properties"+
	"\n\tshow-properties - Show the current repository access properties"+
	"\nVariability Commands"+
	"\n\tadd-core-asset - Add Core Asset"+
	"\n\tshow-core-assets - Show Core Assets"+
	"\n\tshow-instances - Show Instances of Core Asset"+
	"\n\tinstantiate-core-asset - Instantiate Core Asset"+
	"\n\tshow-instance-diff - Show changes in the Instances of a given Core Asset"+
	"\n\tshow-core-diff - Show changes in the Core Asset of a given Instance"+
	"\n\trebase - Mark last changes to instance as a rebase operation"+
	"\n\tintegrate - Mark last changes to core asset as an integration operation";
	
	private final String setProperties = "Sets the properties (i.e. repository URL, proxy URL, proxy port, username, password) " +
	"for accessing the repository. When the command is issued a dialog is opened for setting the properties. " +
	"The properties are stored in a file called 'properties.txt' in the current path. The password is being encrypted upon saving to the file." +
	"\n\nset-properties []";
	
	private final String showProperties = "Displays the properties (i.e. repository URL, proxy URL, proxy port, username, password) " +
	"for accessing the repository. The password is displayed in an encrypted form. The properties are read from the file 'properties.txt' in the current path" +
	"\n\nshow-properties []";
	
	private final String addCoreAsset = "Adds an unversioned file or directory from the local file system to the repository as a core asset. " +
	"If a directory is passed to the command the user is asked whether all contents of the directory should be recursively added as core assets." +
	"\n\nadd-core-asset local path [remote path]" +
	"\n\nlocal path - full path to a local file or directory.\nIf the path contains spaces, it must be enclosed in quotes" +
	"\n\n[remote path] - relative path in the repository, where the file or directory should be added. If left empty the root path will be taken."+
	"The path is relative to the repository server. "+
	"Example of a remote path : /framework/requirements";
	
	private final String showCoreAssets = "Displays a list of available core assets in the repository." +  
	"\n\nshow-core-assets [revision number] [remote path] [-d]" +
	"\n\n[revision number] - indicates that only the core assets of the given repository repository revision should be listed. If left empty the latest revision will be taken" +
	"\n\n[remote path] - indicates that only the core assets in the given repository path should be listed. If left empty all core assets will be listed" +
	"\n\n[-d] - indicates that only directories marked as core assets should be listed";
	
	private final String showInstances = "Displays a list of available instances in the repository." +  
	"\n\nshow-instances [remote path] [revision number]" +
	"\n\n [remote path] - indicates that only the instances in the given repository path should be listed" +
	"\n\n [revision number] - indicates that only the instances of the given repository repository revision should be listed. If left empty the latest revision will be taken";
	
	private final String instantiateCoreAsset = "Instantiates a core asset in the repository" +  
	"\n\ninstantiate-core-asset core_asset_path [revision number] [remote target path]" +
	"\n\ncore_asset_path - indicates the path of the core asset that must be instantiated" +
	"\n\nremote target path - indicates the path where the instance will be created" +
	"\n\n[revision number] - the revision of the core asset to be instantiated, if left empty the latest revision will be taken";
	
	private final String showInstanceDiff = "Shows whether the instances of a core asset have changed" +  
	"\n\nshow-instance-diff core_asset_path [revision number]" +
	"\n\ncore_asset_path - indicates the path of the core asset whose instances will be checked" +
	"\n\n[revision number] - the revision of the core asset from which to start checking, if left empty the check will start from the beginning to the revision graph";
	
	private final String showCoreAssetDiff = "Shows whether the core asset of an instance has changed" +  
	"\n\nshow-core-diff instance_path" +
	"\n\ninstance_path - indicates the path of the instance whose core asset will be checked";
	
	private final String rebase = "Mark last changes on an instance as a change propagation (rebase) from the core asset to the instance" +  
	"\n\nrebase instance_path " +
	"\n\ninstance_path - indicates the path of the instance to rebase";
	
	private final String integrate = "Mark last changes on a core asset as a change propagation (integration) from the instance to the core asset" +  
	"\n\nintegrate core_asset_path " +
	"\n\ninstance_path - indicates the path of the instance to be used as the source of the integration";
	
	private final String clear = "Clears the output text field" +  
	"\n\nclear [] ";
	
	private final String exit = "Exits the program" +  
	"\n\nexit [] ";
	
	private final String help = "Displays additional help on a command" +  
	"\n\nhelp command_name "+
	"\n\ncomand_name - name of the command to display help on";

	public HelpCommand(ConsoleGUI gui, CustomizationLayer cl, Token[] arguments) {
		super(gui, cl, arguments);
		// TODO Auto-generated constructor stub
	}

	public void execute() {
		
		String commandName = null;
		if (arguments[0] != null)
			commandName = arguments[0].image;
		
		Integer commandID = 0;
		
		if (commandName != null)
			commandID = CommandMap.map.get(commandName);
		
		switch (commandID) {
		case 0:
			outputToUI(helpText);
			break;
		case CommandSet.SET_PROPERTIES:
			outputToUI(setProperties);
			break;
		case CommandSet.ADD_CORE_ASSET:
			outputToUI(addCoreAsset);
			break;
		case CommandSet.SHOW_CORE_ASSETS:
			outputToUI(showCoreAssets);
			break;
		case CommandSet.SHOW_INSTANCES:
			outputToUI(showInstances);
			break;
		case CommandSet.INSTANTIATE_CORE_ASSET:
			outputToUI(instantiateCoreAsset);
			break;
		case CommandSet.SHOW_INSTANCE_DIFF:
			outputToUI(showInstanceDiff);
			break;
		case CommandSet.SHOW_CORE_ASSET_DIFF:
			outputToUI(showCoreAssetDiff);
			break;
		case CommandSet.SHOW_PROPERTIES:
			outputToUI(showProperties);
			break;
		case CommandSet.REBASE:
			outputToUI(rebase);
			break;
		case CommandSet.INTEGRATE:
			outputToUI(integrate);
			break;
		case CommandSet.CLEAR:
			outputToUI(clear);
			break;
		case CommandSet.EXIT:
			outputToUI(exit);
			break;
		case CommandSet.HELP:
			outputToUI(help);
			break;
		default:
			break;
		
		}
	}

	public void undo() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCommandName() {
		return "help";
	}

	@Override
	public void requestStop() {
		// TODO Auto-generated method stub
		
	}

}
