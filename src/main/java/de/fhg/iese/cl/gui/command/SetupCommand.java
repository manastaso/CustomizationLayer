package de.fhg.iese.cl.gui.command;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class SetupCommand extends Command {

	public SetupCommand(ConsoleGUI gui, CustomizationLayer cl, Token[] arguments) {
		super(gui, cl, arguments);
	}

	@Override
	public void execute() {
		try {
			outputToUI("Initializing repository access from property file: "+cl.getCmConnector().getPropertyFile().getAbsolutePath());
			String result = cl.setup();
			outputToUI("done, obtained repository:"+result);
		} catch (CMException e) {
			outputToUI("Error while initializing repository access:"+e.getMessage());
		}
	}

	@Override
	public String getCommandName() {
		return "Initializing";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
