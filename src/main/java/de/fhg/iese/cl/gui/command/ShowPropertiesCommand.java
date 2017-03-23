package de.fhg.iese.cl.gui.command;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.cm.RepositoryProperties;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class ShowPropertiesCommand extends Command {

	public ShowPropertiesCommand(ConsoleGUI gui, CustomizationLayer cl,
			Token[] arguments) {
		super(gui, cl, arguments);
	}

	@Override
	public void execute() {
		RepositoryProperties props;
		try {
			props = cl.getCmConnector().readProperties(false);
			if (props != null)
				outputToUI(props.toString());
			else
				outputToUI("no properties available");
		} catch (CMException e) {
			outputToUI(e.getMessage());
		}
	}

	@Override
	public String getCommandName() {
		return "show-properties";
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
