package de.fhg.iese.cl.gui.command;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class InstantiateCoreAssetCommand extends Command {
	
	private long revision = -1;

	public InstantiateCoreAssetCommand(ConsoleGUI gui, CustomizationLayer cl,
			Token[] arguments) {
		super(gui, cl, arguments);
	}

	@Override
	public void execute() {
		String inputRevisionStr = arguments[1] == null ? "-1" : arguments[1].toString();
		String sourcePath = null;
		String targetPath = null;
		try {
			revision = new Long(inputRevisionStr);
			
			if (arguments[0] != null)
				sourcePath = arguments[0].toString();
			else
				sourcePath = new String();
			
			if (arguments[2] != null)
				targetPath = arguments[2].toString();
			else
				targetPath = new String();
		
			String s = cl.instantiateCoreAsset(sourcePath ,revision, targetPath);
			outputToUI(s);
		} catch (CMException e) {
			outputToUI("CM Error:"+e);
		}
	}

	@Override
	public String getCommandName() {
		return "instantiate-core-asset";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestStop() {
		// TODO Auto-generated method stub
		
	}

}
