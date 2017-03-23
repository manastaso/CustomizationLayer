package de.fhg.iese.cl.gui.command;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class IntegrateCommand extends Command {

	public IntegrateCommand(ConsoleGUI gui, CustomizationLayer cl,
			Token[] arguments) {
		super(gui, cl, arguments);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		try {
			String result = cl.integrate(arguments[0].toString());
			outputToUI(result);
		} catch (CMException e) {
			outputToUI(e.toString());
		}
	}

	@Override
	public String getCommandName() {
		return "integrate";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
