package de.fhg.iese.cl.gui.command;

import java.io.IOException;
import java.util.Observer;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class AddCoreAssetCommand extends Command {

	public AddCoreAssetCommand(ConsoleGUI gui, CustomizationLayer cl,
			Token[] arguments) {
		super(gui, cl, arguments);
	}

	@Override
	public void execute() {
		String targetPath = null;
		String sourcePath = null;
		try {
			
			if (arguments[1] != null)
				targetPath = arguments[1].toString();
			else
				targetPath = new String();
			
			if (arguments[0] != null)
				sourcePath = arguments[0].toString();
			else
				sourcePath = new String();
			
			Observer o = cl.getObserver();
			blackboard.addObserver(o);
			String s = cl.addCoreAsset(sourcePath,targetPath);
			outputToUI(s);
		} catch (CMException e) {
			outputToUI("CM Error:"+e.getMessage().toString());
		} catch (IOException e) {
			outputToUI("I/O Error:"+e.getMessage().toString());
		}
	}

	@Override
	public String getCommandName() {
		return "add-core-asset";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}
}
