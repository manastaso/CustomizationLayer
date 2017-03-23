package de.fhg.iese.cl.gui.command;

import java.util.ArrayList;
import java.util.Iterator;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class ShowCoreDiffCommand extends Command {

	public ShowCoreDiffCommand(ConsoleGUI gui, CustomizationLayer cl,
			Token[] arguments) {
		super(gui, cl, arguments);
	}

	@Override
	public void execute() {
		try {
			ArrayList<String> coreDiffs = cl.getCoreDiff(arguments[0].toString());
			
			for (Iterator<String> iterator = coreDiffs.iterator(); iterator
			.hasNext();) {
				String coreDiffName = (String) iterator.next();
				outputToUI(coreDiffName);
			}
		} catch (CMException e) {
			outputToUI("CM Error: "+e.getMessage().toString());
		}
	}

	@Override
	public String getCommandName() {
		return "show-core-diff";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
