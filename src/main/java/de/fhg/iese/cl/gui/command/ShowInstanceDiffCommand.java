package de.fhg.iese.cl.gui.command;

import java.util.ArrayList;
import java.util.Iterator;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class ShowInstanceDiffCommand extends Command {
	
	private long revision = -1;

	public ShowInstanceDiffCommand(ConsoleGUI gui, CustomizationLayer cl,
			Token[] arguments) {
		super(gui, cl, arguments);
	}

	@Override
	public void execute() {
		String inputRevisionStr = arguments[1] == null ? "-1" : arguments[1].toString();
		revision = new Long(inputRevisionStr);
		try {
			ArrayList<String> instanceDiffs = cl.getInstanceDiff(arguments[0].toString(), revision);
			for (Iterator<String> iterator = instanceDiffs.iterator(); iterator
			.hasNext();) {
				String instanceDiffName = (String) iterator.next();
				outputToUI(instanceDiffName);
			}
		} catch (CMException e) {
			outputToUI("CM Error: "+e.getMessage().toString());
		}
	}
	
	@Override
	public String getCommandName() {
		return "show-instance-diff";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
