package de.fhg.iese.cl.gui.command;

import java.util.ArrayList;
import java.util.Iterator;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class ShowCoreAssetsCommand extends Command {

	private long revision=-1;
	private ArrayList<String> coreAssetList=null;
	String item;

	public ShowCoreAssetsCommand(ConsoleGUI gui, CustomizationLayer cl,
			Token[] arguments) {
		super(gui, cl, arguments);
	}

	@Override
	public void execute() {
		String inputRevisionStr = arguments[0] == null ? "-1" : arguments[0].toString();
		String path = null;;
		try {
			revision = new Long(inputRevisionStr);
			if (arguments[1] != null)
				path = arguments[1].toString();
			else
				path = new String(); 
			
			if (arguments[2] == null)
				coreAssetList = cl.getCoreAssets(revision, path, false);
			else
				coreAssetList = cl.getCoreAssets(revision, path, true);
			
			for (Iterator<String> iterator = coreAssetList.iterator(); iterator
			.hasNext();) {
				if (stop)
					break;
				item = (String) iterator.next();
				outputToUI(item);
			}
			outputToUI(coreAssetList.size() + " core asset(s) available");
		} catch (CMException e) {
			outputToUI("SVN Error: "+e.getMessage().toString());
		}
	}

	@Override
	public String getCommandName() {
		return "show-core-assets";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
